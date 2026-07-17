package com.example.domain.usecase.article

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateArticlesForAllTopicsUseCaseTest {
    private val settingsRepo = mockk<SettingsRepository>()
    private val articlesRepo = mockk<ArticleRepository>()

    private val useCase = UpdateArticlesForAllTopicsUseCase(articlesRepo, settingsRepo)

    private val language = Language.RUSSIAN

    @Test
    fun `returns list of topics when repository update all topics`() = runTest {
        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )

        coEvery { articlesRepo.updateArticlesForAllTopics(language) } returns listOf(
            "Kotlin",
            "Java"
        )

        val result = useCase()

        assertEquals(listOf("Kotlin", "Java"), result)
    }

    @Test
    fun `returns empty list of topics when repository update all topics`() = runTest {
        every { settingsRepo.getSettings() } returns flowOf(
            Settings(
                language = language,
                interval = Interval.MIN_15,
                notificationsEnabled = true,
                wifiOnly = false
            )
        )

        coEvery { articlesRepo.updateArticlesForAllTopics(language) } returns listOf()

        val result = useCase()

        assertEquals(emptyList<String>(), result)
    }
}