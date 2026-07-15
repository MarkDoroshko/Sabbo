package com.example.domain.usecase.settings

import com.example.domain.entity.Interval
import com.example.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(interval: Interval) =
        settingsRepository.updateInterval(interval)
}