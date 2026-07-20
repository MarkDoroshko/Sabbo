package com.example.presentation.screen.settings

import app.cash.turbine.test
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.domain.usecase.settings.GetSettingsUseCase
import com.example.domain.usecase.settings.UpdateIntervalUseCase
import com.example.domain.usecase.settings.UpdateLanguageUseCase
import com.example.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.example.domain.usecase.settings.UpdateWifiOnlyUseCase
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
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSettingsUseCase = mockk<GetSettingsUseCase>()
    private val updateIntervalUseCase = mockk<UpdateIntervalUseCase>()
    private val updateLanguageUseCase = mockk<UpdateLanguageUseCase>()
    private val updateNotificationsEnabledUseCase = mockk<UpdateNotificationsEnabledUseCase>()
    private val updateWifiOnlyUseCase = mockk<UpdateWifiOnlyUseCase>()

    private val defaultSettings = Settings(
        language = Settings.DEFAULT_LANGUAGE,
        interval = Settings.DEFAULT_INTERVAL,
        notificationsEnabled = Settings.DEFAULT_NOTIFICATIONS_ENABLED,
        wifiOnly = Settings.DEFAULT_WIFI_ONLY
    )

    private fun viewModel(settingsFlow: Settings = defaultSettings): SettingsViewModel {
        every { getSettingsUseCase() } returns flowOf(settingsFlow)
        return SettingsViewModel(
            getSettingsUseCase,
            updateIntervalUseCase,
            updateLanguageUseCase,
            updateNotificationsEnabledUseCase,
            updateWifiOnlyUseCase
        )
    }

    @Test
    fun `settings starts with the default value and then emits the value from getSettingsUseCase`() =
        runTest {
            val loadedSettings = Settings(Language.FRENCH, Interval.HOUR_2, true, true)
            val viewModel = viewModel(loadedSettings)

            viewModel.settings.test {
                assertEquals(defaultSettings, awaitItem())
                assertEquals(loadedSettings, awaitItem())
            }
        }

    @Test
    fun `updateLanguage delegates to updateLanguageUseCase`() = runTest {
        coEvery { updateLanguageUseCase(Language.GERMAN) } returns Unit
        val viewModel = viewModel()

        viewModel.updateLanguage(Language.GERMAN)
        advanceUntilIdle()

        coVerify { updateLanguageUseCase(Language.GERMAN) }
    }

    @Test
    fun `updateInterval delegates to updateIntervalUseCase`() = runTest {
        coEvery { updateIntervalUseCase(Interval.HOUR_4) } returns Unit
        val viewModel = viewModel()

        viewModel.updateInterval(Interval.HOUR_4)
        advanceUntilIdle()

        coVerify { updateIntervalUseCase(Interval.HOUR_4) }
    }

    @Test
    fun `updateNotificationsEnabled delegates to updateNotificationsEnabledUseCase`() = runTest {
        coEvery { updateNotificationsEnabledUseCase(true) } returns Unit
        val viewModel = viewModel()

        viewModel.updateNotificationsEnabled(true)
        advanceUntilIdle()

        coVerify { updateNotificationsEnabledUseCase(true) }
    }

    @Test
    fun `updateWifiOnly delegates to updateWifiOnlyUseCase`() = runTest {
        coEvery { updateWifiOnlyUseCase(true) } returns Unit
        val viewModel = viewModel()

        viewModel.updateWifiOnly(true)
        advanceUntilIdle()

        coVerify { updateWifiOnlyUseCase(true) }
    }
}
