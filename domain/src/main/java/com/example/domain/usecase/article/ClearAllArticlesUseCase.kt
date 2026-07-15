package com.example.domain.usecase.article

import com.example.domain.repository.ArticleRepository
import javax.inject.Inject

class ClearAllArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(topics: List<String>) = articleRepository.clearAllArticles(topics)
}