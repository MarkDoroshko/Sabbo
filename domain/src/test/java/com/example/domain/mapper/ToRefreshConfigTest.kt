package com.example.domain.mapper

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.RefreshConfig
import com.example.domain.entity.Settings
import org.junit.Test
import org.junit.Assert.*

class ToRefreshConfigTest {
    @Test
    fun `toRefreshConfig copy language, interval and wifiOnly, but discards notificationsEnabled`() {
        val settings = Settings(
            language = Language.RUSSIAN,
            interval = Interval.MIN_15,
            notificationsEnabled = true,
            wifiOnly = false
        )

        val refreshConfig = settings.toRefreshConfig()

        assertEquals(
            RefreshConfig(
                language = Language.RUSSIAN,
                interval = Interval.MIN_15,
                wifiOnly = false
            ),
            refreshConfig
        )
    }
}