package com.example.domain.usecase.article

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class UpdateArticlesForTopicUseCaseTest {
    private val settingsRepo = mockk<SettingsRepository>()
    private val articleRepo = mockk<ArticleRepository>()

    private val useCase = UpdateArticlesForTopicUseCase(articleRepo, settingsRepo)

    val topic = "Kotlin"
    val language = Language.RUSSIAN

    @Test
    fun `calls method updateArticlesForTopic with language value from settings`() = runTest {
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

        assertEquals(AppResult.Success(true), result)
    }

    @Test
    fun `returns error when repository update fails`() = runTest {
        val error = AppResult.Failure(DomainError.ServerError)

        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )

        coEvery { articleRepo.updateArticlesForTopic(topic, language) } returns error

        val result = useCase(topic)

        assertEquals(error, result)
    }
}