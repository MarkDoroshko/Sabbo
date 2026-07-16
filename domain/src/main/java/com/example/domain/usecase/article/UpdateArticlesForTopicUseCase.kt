package com.example.domain.usecase.article

import com.example.domain.error.AppResult
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateArticlesForTopicUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(topic: String): AppResult<Boolean> {
        val settings = settingsRepository.getSettings().first()

        return articleRepository.updateArticlesForTopic(topic, settings.language)
    }
}