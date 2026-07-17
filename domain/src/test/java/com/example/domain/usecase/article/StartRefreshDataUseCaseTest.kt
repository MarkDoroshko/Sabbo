package com.example.domain.usecase.article

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.RefreshConfig
import com.example.domain.entity.Settings
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StartRefreshDataUseCaseTest {
    private val articleRepo = mockk<ArticleRepository>()
    private val settingsRepo = mockk<SettingsRepository>()

    private val useCase = StartRefreshDataUseCase(articleRepo, settingsRepo)

    private val settingsRussian15 = Settings(
        language = Language.RUSSIAN,
        interval = Interval.MIN_15,
        notificationsEnabled = false,
        wifiOnly = false
    )

    // notificationsEnabled не входит в RefreshConfig, поэтому маппится в тот же конфиг, что и settingsRussian15
    private val settingsRussian15NotificationsOn =
        settingsRussian15.copy(notificationsEnabled = true)

    private val settingsRussian30 = settingsRussian15.copy(interval = Interval.MIN_30)

    @Test
    fun `starts background refresh only when mapped RefreshConfig actually changes`() = runTest {
        every { settingsRepo.getSettings() } returns flowOf(
            settingsRussian15,
            settingsRussian15NotificationsOn,
            settingsRussian30
        )
        justRun { articleRepo.startBackgroundRefresh(any()) }

        useCase()

        verify(exactly = 2) { articleRepo.startBackgroundRefresh(any()) }
        verifyOrder {
            articleRepo.startBackgroundRefresh(
                RefreshConfig(
                    Language.RUSSIAN,
                    Interval.MIN_15,
                    false
                )
            )
            articleRepo.startBackgroundRefresh(
                RefreshConfig(
                    Language.RUSSIAN,
                    Interval.MIN_30,
                    false
                )
            )
        }
    }
}
