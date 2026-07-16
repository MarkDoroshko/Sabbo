package com.example.domain.usecase.topic

import com.example.domain.error.AppResult
import com.example.domain.error.map
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
    private val articleRepository: ArticleRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(topic: String): AppResult<Unit> {
        topicRepository.addTopic(topic)

        val settings = settingsRepository.getSettings().first()
        return articleRepository.updateArticlesForTopic(topic, settings.language).map { }
    }
}