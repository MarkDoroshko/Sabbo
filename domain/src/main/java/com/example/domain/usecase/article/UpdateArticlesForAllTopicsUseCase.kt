package com.example.domain.usecase.article

import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateArticlesForAllTopicsUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): List<String> {
        val settings = settingsRepository.getSettings().first()

        return articleRepository.updateArticlesForAllTopics(settings.language)
    }
}