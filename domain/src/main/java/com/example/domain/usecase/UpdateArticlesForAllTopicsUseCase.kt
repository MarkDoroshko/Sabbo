package com.example.domain.usecase

import com.example.domain.repository.ArticleRepository
import javax.inject.Inject

class UpdateArticlesForAllTopicsUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke() = articleRepository.updateArticlesForAllTopics()
}