package com.example.domain.usecase.settings

import com.example.domain.entity.Language
import com.example.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(language: Language) = settingsRepository.updateLanguage(language)
}