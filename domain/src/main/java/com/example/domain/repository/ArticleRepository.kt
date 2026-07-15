package com.example.domain.repository

import com.example.domain.entity.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticlesByTopic(topic: String): Flow<List<Article>>

    fun getArticlesByTopics(topics: List<String>): Flow<List<Article>>

    suspend fun updateArticlesForTopic(topic: String): Boolean

    suspend fun updateArticlesForAllTopics(): List<String>

    suspend fun clearAllArticles(topics: List<String>)
}