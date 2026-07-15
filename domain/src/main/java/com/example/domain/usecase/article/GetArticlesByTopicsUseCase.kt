package com.example.domain.usecase.article

import com.example.domain.repository.ArticleRepository
import javax.inject.Inject

class GetArticlesByTopicsUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    operator fun invoke(topics: List<String>) = articleRepository.getArticlesByTopics(topics)
}