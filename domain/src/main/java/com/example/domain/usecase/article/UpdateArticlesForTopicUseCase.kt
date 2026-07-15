package com.example.domain.usecase.article

import com.example.domain.repository.ArticleRepository
import javax.inject.Inject

class UpdateArticlesForTopicUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(topic: String) = articleRepository.updateArticlesForTopic(topic)
}