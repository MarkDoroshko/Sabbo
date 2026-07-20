package com.example.presentation.screen.feed

import app.cash.turbine.test
import com.example.domain.entity.Article
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import com.example.domain.usecase.article.ClearAllArticlesUseCase
import com.example.domain.usecase.article.GetArticlesByTopicUseCase
import com.example.domain.usecase.article.GetArticlesByTopicsUseCase
import com.example.domain.usecase.article.UpdateArticlesForAllTopicsUseCase
import com.example.domain.usecase.article.UpdateArticlesForTopicUseCase
import com.example.domain.usecase.topic.GetAllTopicsUseCase
import com.example.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clearAllArticlesUseCase = mockk<ClearAllArticlesUseCase>()
    private val updateArticlesForAllTopicsUseCase = mockk<UpdateArticlesForAllTopicsUseCase>()
    private val updateArticlesForTopicUseCase = mockk<UpdateArticlesForTopicUseCase>()
    private val getAllTopicsUseCase = mockk<GetAllTopicsUseCase>()
    private val getArticlesByTopicsUseCase = mockk<GetArticlesByTopicsUseCase>()
    private val getArticlesByTopicUseCase = mockk<GetArticlesByTopicUseCase>()

    private fun article(topic: String, url: String) = Article(
        title = "Title $url",
        description = "Description $url",
        imageUrl = null,
        sourceName = "Source",
        publishedAt = 1_000L,
        url = url,
        topic = topic
    )

    private fun viewModel(): FeedViewModel = FeedViewModel(
        clearAllArticlesUseCase,
        updateArticlesForAllTopicsUseCase,
        updateArticlesForTopicUseCase,
        getAllTopicsUseCase,
        getArticlesByTopicsUseCase,
        getArticlesByTopicUseCase
    )

    @Test
    fun `state loads articles for all topics on start, without needing a subscriber`() = runTest {
        val topics = listOf("Kotlin", "Java")
        val articles = listOf(article("Kotlin", "url1"), article("Java", "url2"))
        every { getAllTopicsUseCase() } returns flowOf(topics)
        every { getArticlesByTopicsUseCase(topics) } returns flowOf(articles)

        val viewModel = viewModel()
        // в отличие от topics в TopicsViewModel, здесь не нужен внешний collector:
        // observeArticles() запускается сразу в init через обычный MutableStateFlow
        advanceUntilIdle()

        assertEquals(
            FeedState(topics = topics, selectedTopic = null, articles = articles, isLoading = false),
            viewModel.state.value
        )
    }

    @Test
    fun `SelectTopic switches from getArticlesByTopicsUseCase to getArticlesByTopicUseCase`() =
        runTest {
            val topics = listOf("Kotlin", "Java")
            every { getAllTopicsUseCase() } returns flowOf(topics)
            every { getArticlesByTopicsUseCase(topics) } returns flowOf(emptyList())
            val kotlinArticles = listOf(article("Kotlin", "url1"))
            every { getArticlesByTopicUseCase("Kotlin") } returns flowOf(kotlinArticles)

            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.processIntent(FeedIntent.SelectTopic("Kotlin"))
            advanceUntilIdle()

            assertEquals(
                FeedState(topics = topics, selectedTopic = "Kotlin", articles = kotlinArticles, isLoading = false),
                viewModel.state.value
            )
            coVerify(exactly = 1) { getArticlesByTopicsUseCase(topics) }
            coVerify(exactly = 1) { getArticlesByTopicUseCase("Kotlin") }
        }

    @Test
    fun `OpenArticle intent sends NavigateToArticle effect`() = runTest {
        every { getAllTopicsUseCase() } returns flowOf(emptyList())
        every { getArticlesByTopicsUseCase(emptyList()) } returns flowOf(emptyList())
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(FeedIntent.OpenArticle("https://example.com"))

            assertEquals(FeedEffect.NavigateToArticle("https://example.com"), awaitItem())
        }
    }

    @Test
    fun `RefreshArticles with no topic selected sends RefreshPartial when some topics failed to update`() =
        runTest {
            val topics = listOf("Kotlin", "Java", "Android")
            every { getAllTopicsUseCase() } returns flowOf(topics)
            every { getArticlesByTopicsUseCase(topics) } returns flowOf(emptyList())
            coEvery { updateArticlesForAllTopicsUseCase() } returns listOf("Kotlin", "Java")

            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.processIntent(FeedIntent.RefreshArticles)

                assertEquals(FeedEffect.RefreshPartial, awaitItem())
            }
        }

    @Test
    fun `RefreshArticles with a topic selected sends RefreshFailed on Failure`() = runTest {
        val topics = listOf("Kotlin")
        every { getAllTopicsUseCase() } returns flowOf(topics)
        every { getArticlesByTopicsUseCase(topics) } returns flowOf(emptyList())
        every { getArticlesByTopicUseCase("Kotlin") } returns flowOf(emptyList())
        coEvery { updateArticlesForTopicUseCase("Kotlin") } returns AppResult.Failure(DomainError.NoConnection)

        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.processIntent(FeedIntent.SelectTopic("Kotlin"))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(FeedIntent.RefreshArticles)

            assertEquals(FeedEffect.RefreshFailed(DomainError.NoConnection), awaitItem())
        }
    }

    @Test
    fun `ClearArticles sends ArticlesCleared on success`() = runTest {
        val topics = listOf("Kotlin")
        every { getAllTopicsUseCase() } returns flowOf(topics)
        every { getArticlesByTopicsUseCase(topics) } returns flowOf(emptyList())
        coEvery { clearAllArticlesUseCase(topics) } returns Unit

        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(FeedIntent.ClearArticles)

            assertEquals(FeedEffect.ArticlesCleared, awaitItem())
        }
    }

    @Test
    fun `ClearArticles sends ClearFailed with the exception message on failure`() = runTest {
        val topics = listOf("Kotlin")
        every { getAllTopicsUseCase() } returns flowOf(topics)
        every { getArticlesByTopicsUseCase(topics) } returns flowOf(emptyList())
        coEvery { clearAllArticlesUseCase(topics) } throws RuntimeException("db is locked")

        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(FeedIntent.ClearArticles)

            assertEquals(FeedEffect.ClearFailed("db is locked"), awaitItem())
        }
    }
}
