package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.ArticlesDao
import com.example.data.local.dao.TopicsDao
import com.example.data.local.model.ArticleDbModel
import com.example.data.mapper.toDbModels
import com.example.data.mapper.toEntities
import com.example.data.remote.api.NewsApi
import com.example.domain.entity.Article
import com.example.domain.repository.ArticleRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val topicsDao: TopicsDao,
    private val articlesDao: ArticlesDao
) : ArticleRepository {
    override fun getArticlesByTopic(topic: String): Flow<List<Article>> {
        return articlesDao.getAllArticlesByTopic(topic).map { articleDbModels ->
            articleDbModels.toEntities()
        }
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return articlesDao.getAllArticlesByTopics(topics).map { articleDbModels ->
            articleDbModels.toEntities()
        }
    }

    override suspend fun updateArticlesForTopic(topic: String): Boolean {
        val articles = loadArticles(topic)
        val ids = articlesDao.addArticles(articles)
        return ids.any { it != -1L }
    }

    override suspend fun updateArticlesForAllTopics(): List<String> {
        val topics = topicsDao.getAllTopics().first()

        return supervisorScope {
            topics.map { topicModel ->
                async {
                    try {
                        if (updateArticlesForTopic(topicModel.topic)) topicModel.topic else null
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e("ArticleRepositoryImpl", "topic=${topicModel.topic}", e)
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        articlesDao.deleteArticlesByTopics(topics)
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            newsApi.getArticles(topic).toDbModels(topic)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ArticleRepositoryImpl", e.stackTraceToString())
            throw e
        }
    }
}