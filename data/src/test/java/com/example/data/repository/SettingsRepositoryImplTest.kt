package com.example.data.repository

import android.content.Context
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryImplTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        val context = mockk<Context>(relaxed = true)
        every { context.filesDir } returns tempFolder.root
        every { context.applicationContext } returns context

        repository = SettingsRepositoryImpl(context)
    }

    @Test
    fun `getSettings returns defaults when nothing was saved yet`() = runTest {
        val settings = repository.getSettings().first()

        assertEquals(
            Settings(
                language = Settings.DEFAULT_LANGUAGE,
                interval = Settings.DEFAULT_INTERVAL,
                notificationsEnabled = Settings.DEFAULT_NOTIFICATIONS_ENABLED,
                wifiOnly = Settings.DEFAULT_WIFI_ONLY
            ),
            settings
        )
    }

    @Test
    fun `updateLanguage persists the new language`() = runTest {
        repository.updateLanguage(Language.FRENCH)

        val settings = repository.getSettings().first()

        assertEquals(Language.FRENCH, settings.language)
    }

    @Test
    fun `updateInterval persists the raw minutes value and maps it back to the same Interval`() =
        runTest {
            repository.updateInterval(Interval.HOUR_1)

            val settings = repository.getSettings().first()

            assertEquals(Interval.HOUR_1, settings.interval)
        }

    @Test
    fun `updateNotificationsEnabled persists the flag`() = runTest {
        repository.updateNotificationsEnabled(true)

        val settings = repository.getSettings().first()

        assertEquals(true, settings.notificationsEnabled)
    }

    @Test
    fun `updateWifiOnly persists the flag`() = runTest {
        repository.updateWifiOnly(true)

        val settings = repository.getSettings().first()

        assertEquals(true, settings.wifiOnly)
    }

    @Test
    fun `updating one field does not overwrite the previously saved values of the others`() =
        runTest {
            repository.updateLanguage(Language.GERMAN)
            repository.updateWifiOnly(true)

            val settings = repository.getSettings().first()

            assertEquals(Language.GERMAN, settings.language)
            assertEquals(true, settings.wifiOnly)
        }
}
