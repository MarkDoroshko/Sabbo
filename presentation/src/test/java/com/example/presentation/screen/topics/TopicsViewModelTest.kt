package com.example.presentation.screen.topics

import app.cash.turbine.test
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import com.example.domain.usecase.topic.AddTopicUseCase
import com.example.domain.usecase.topic.GetAllTopicsUseCase
import com.example.domain.usecase.topic.RemoveTopicUseCase
import com.example.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val addTopicUseCase = mockk<AddTopicUseCase>()
    private val removeTopicUseCase = mockk<RemoveTopicUseCase>()
    private val getAllTopicsUseCase = mockk<GetAllTopicsUseCase>()

    private fun viewModel(initialTopics: List<String> = emptyList()): TopicsViewModel {
        every { getAllTopicsUseCase() } returns flowOf(initialTopics)
        return TopicsViewModel(addTopicUseCase, removeTopicUseCase, getAllTopicsUseCase)
    }

    @Test
    fun `topics emits the list from getAllTopicsUseCase`() = runTest {
        val viewModel = viewModel(initialTopics = listOf("Kotlin", "Java"))

        viewModel.topics.test {
            assertEquals(emptyList<String>(), awaitItem())
            assertEquals(listOf("Kotlin", "Java"), awaitItem())
        }
    }

    @Test
    fun `addTopic calls the use case and clears the input on success`() = runTest {
        val viewModel = viewModel()
        coEvery { addTopicUseCase("Kotlin") } returns AppResult.Success(Unit)

        viewModel.onTopicInputChange("Kotlin")
        viewModel.addTopic()
        advanceUntilIdle()

        coVerify { addTopicUseCase("Kotlin") }
        assertEquals("", viewModel.topicInput.value)
    }

    @Test
    fun `addTopic does nothing when the input is blank`() = runTest {
        val viewModel = viewModel()

        viewModel.onTopicInputChange("   ")
        viewModel.addTopic()
        advanceUntilIdle()

        coVerify(exactly = 0) { addTopicUseCase(any()) }
    }

    @Test
    fun `addTopic does not call the use case when the topic already exists`() = runTest {
        val viewModel = viewModel(initialTopics = listOf("Kotlin"))

        // topics стартует лениво (WhileSubscribed) — без подписки addTopic() увидел бы
        // topics.value == emptyList() (initialValue), и проверка на дубликат никогда бы не сработала
        val collectJob = launch { viewModel.topics.collect {} }
        advanceUntilIdle()

        viewModel.onTopicInputChange("Kotlin")
        viewModel.addTopic()
        advanceUntilIdle()

        coVerify(exactly = 0) { addTopicUseCase(any()) }
        collectJob.cancel()
    }

    @Test
    fun `addTopic sends AddTopicFailed effect when the use case returns Failure`() = runTest {
        val viewModel = viewModel()
        coEvery { addTopicUseCase("Kotlin") } returns AppResult.Failure(DomainError.NoConnection)

        viewModel.effect.test {
            viewModel.onTopicInputChange("Kotlin")
            viewModel.addTopic()

            assertEquals(TopicsEffect.AddTopicFailed(DomainError.NoConnection), awaitItem())
        }
    }

    @Test
    fun `removeTopic delegates to removeTopicUseCase`() = runTest {
        val viewModel = viewModel()
        coEvery { removeTopicUseCase("Kotlin") } returns Unit

        viewModel.removeTopic("Kotlin")
        advanceUntilIdle()

        coVerify { removeTopicUseCase("Kotlin") }
    }
}
