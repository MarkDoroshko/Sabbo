package com.example.domain.usecase

import com.example.domain.repository.ArticleRepository
import javax.inject.Inject

class GetArticlesByTopicUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    operator fun invoke(topic: String) = articleRepository.getArticlesByTopic(topic)
}