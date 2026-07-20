package com.example.presentation.screen.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val defaultSettings = Settings(
        language = Language.ENGLISH,
        interval = Interval.HOUR_1,
        notificationsEnabled = false,
        wifiOnly = false
    )

    @Test
    fun selectingALanguageTriggersOnLanguageSelected() {
        var selectedLanguage: Language? = null
        composeRule.setContent {
            SettingsScreen(
                settings = defaultSettings,
                onLanguageSelected = { selectedLanguage = it },
                onIntervalSelected = {},
                onNotificationsEnabledChanged = {},
                onWifiOnlyChanged = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.language_french)
        ).performClick()

        assertEquals(Language.FRENCH, selectedLanguage)
    }

    @Test
    fun selectingAnIntervalTriggersOnIntervalSelected() {
        var selectedInterval: Interval? = null
        composeRule.setContent {
            SettingsScreen(
                settings = defaultSettings,
                onLanguageSelected = {},
                onIntervalSelected = { selectedInterval = it },
                onNotificationsEnabledChanged = {},
                onWifiOnlyChanged = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.interval_8_hours)
        ).performClick()

        assertEquals(Interval.HOUR_8, selectedInterval)
    }

    @Test
    fun togglingNotificationsTriggersOnNotificationsEnabledChanged() {
        var notificationsEnabled: Boolean? = null
        composeRule.setContent {
            SettingsScreen(
                settings = defaultSettings,
                onLanguageSelected = {},
                onIntervalSelected = {},
                onNotificationsEnabledChanged = { notificationsEnabled = it },
                onWifiOnlyChanged = {}
            )
        }

        composeRule.onNodeWithTag("notifications_toggle").performClick()

        assertEquals(true, notificationsEnabled)
    }

    @Test
    fun togglingWifiOnlyTriggersOnWifiOnlyChanged() {
        var wifiOnly: Boolean? = null
        composeRule.setContent {
            SettingsScreen(
                settings = defaultSettings,
                onLanguageSelected = {},
                onIntervalSelected = {},
                onNotificationsEnabledChanged = {},
                onWifiOnlyChanged = { wifiOnly = it }
            )
        }

        composeRule.onNodeWithTag("wifi_only_toggle").performClick()

        assertEquals(true, wifiOnly)
    }
}
