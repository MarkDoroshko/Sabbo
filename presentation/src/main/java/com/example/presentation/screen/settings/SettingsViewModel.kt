package com.example.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.domain.usecase.settings.GetSettingsUseCase
import com.example.domain.usecase.settings.UpdateIntervalUseCase
import com.example.domain.usecase.settings.UpdateLanguageUseCase
import com.example.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.example.domain.usecase.settings.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateWifiOnlyUseCase: UpdateWifiOnlyUseCase
) : ViewModel() {
    val settings: StateFlow<Settings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Settings(
                language = Settings.DEFAULT_LANGUAGE,
                interval = Settings.DEFAULT_INTERVAL,
                notificationsEnabled = Settings.DEFAULT_NOTIFICATIONS_ENABLED,
                wifiOnly = Settings.DEFAULT_WIFI_ONLY
            )
        )

    fun updateInterval(interval: Interval) {
        viewModelScope.launch { updateIntervalUseCase(interval) }
    }

    fun updateLanguage(language: Language) {
        viewModelScope.launch { updateLanguageUseCase(language) }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { updateNotificationsEnabledUseCase(enabled) }
    }

    fun updateWifiOnly(wifiOnly: Boolean) {
        viewModelScope.launch { updateWifiOnlyUseCase(wifiOnly) }
    }
}