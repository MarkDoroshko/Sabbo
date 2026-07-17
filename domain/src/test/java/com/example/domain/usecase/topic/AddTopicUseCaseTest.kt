package com.example.domain.usecase.topic

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TopicRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AddTopicUseCaseTest {
    private val topicRepo = mockk<TopicRepository>()
    private val articleRepo = mockk<ArticleRepository>()
    private val settingsRepo = mockk<SettingsRepository>()

    private val useCase = AddTopicUseCase(topicRepo, articleRepo, settingsRepo)

    private val topic = "Kotlin"
    private val language = Language.ENGLISH

    @Test
    fun `returns Success when article repository updates articles successfully`() = runTest {
        coJustRun { topicRepo.addTopic(topic) }
        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )
        coEvery { articleRepo.updateArticlesForTopic(topic, language) } returns AppResult.Success(
            true
        )

        val result = useCase(topic)

        assertEquals(AppResult.Success(Unit), result)
        coVerify { topicRepo.addTopic(topic) }
    }

    @Test
    fun `returns Failure unchanged when article repository fails to update articles`() = runTest {
        coJustRun { topicRepo.addTopic(topic) }
        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )
        coEvery {
            articleRepo.updateArticlesForTopic(topic, language)
        } returns AppResult.Failure(DomainError.NoConnection)

        val result = useCase(topic)

        assertEquals(AppResult.Failure(DomainError.NoConnection), result)
    }

    @Test
    fun `adds topic before reading settings and updating articles`() = runTest {
        coJustRun { topicRepo.addTopic(topic) }
        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )
        coEvery { articleRepo.updateArticlesForTopic(topic, language) } returns AppResult.Success(
            true
        )

        useCase(topic)

        coVerifyOrder {
            topicRepo.addTopic(topic)
            settingsRepo.getSettings()
            articleRepo.updateArticlesForTopic(topic, language)
        }
    }
}
