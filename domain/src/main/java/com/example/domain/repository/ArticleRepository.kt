package com.example.domain.repository

import com.example.domain.entity.Article
import com.example.domain.entity.Language
import com.example.domain.entity.RefreshConfig
import com.example.domain.error.AppResult
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticlesByTopic(topic: String): Flow<List<Article>>

    fun getArticlesByTopics(topics: List<String>): Flow<List<Article>>

    suspend fun updateArticlesForTopic(topic: String, language: Language): AppResult<Boolean>

    suspend fun updateArticlesForAllTopics(language: Language): List<String>

    suspend fun clearAllArticles(topics: List<String>)

    fun startBackgroundRefresh(refreshConfig: RefreshConfig)
}