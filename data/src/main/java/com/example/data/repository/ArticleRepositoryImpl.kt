package com.example.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.background.RefreshDataWorker
import com.example.data.local.dao.ArticlesDao
import com.example.data.local.dao.TopicsDao
import com.example.data.local.model.ArticleDbModel
import com.example.data.mapper.toDbModels
import com.example.data.mapper.toDomainError
import com.example.data.mapper.toEntities
import com.example.data.mapper.toQueryParam
import com.example.data.remote.api.NewsApi
import com.example.domain.entity.Article
import com.example.domain.entity.Language
import com.example.domain.entity.RefreshConfig
import com.example.domain.error.AppResult
import com.example.domain.error.map
import com.example.domain.repository.ArticleRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val topicsDao: TopicsDao,
    private val articlesDao: ArticlesDao,
    private val workManager: WorkManager
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

    override suspend fun updateArticlesForTopic(
        topic: String,
        language: Language
    ): AppResult<Boolean> {
        return loadArticles(topic, language).map { articleDbModels ->
            val ids = articlesDao.addArticles(articleDbModels)
            ids.any { it != 1L }
        }
    }

    override suspend fun updateArticlesForAllTopics(language: Language): List<String> {
        val topics = topicsDao.getAllTopics().first()

        return supervisorScope {
            topics.map { topicModel ->
                async {
                    when (val result = updateArticlesForTopic(topicModel.topic, language)) {
                        is AppResult.Success -> if (result.data) topicModel.topic else null
                        is AppResult.Failure -> {
                            Log.e(
                                "ArticleRepositoryImpl",
                                "topic=${topicModel.topic}: ${result.error}"
                            )
                            null
                        }
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        articlesDao.deleteArticlesByTopics(topics)
    }

    override fun startBackgroundRefresh(refreshConfig: RefreshConfig) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (refreshConfig.wifiOnly) NetworkType.UNMETERED
                else NetworkType.CONNECTED
            )
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            refreshConfig.interval.minutes.toLong(), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "Refresh Data",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }

    private suspend fun loadArticles(
        topic: String,
        language: Language
    ): AppResult<List<ArticleDbModel>> {
        return try {
            AppResult.Success(newsApi.getArticles(topic, language.toQueryParam()).toDbModels(topic))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ArticleRepositoryImpl", e.stackTraceToString())
            AppResult.Failure(e.toDomainError())
        }
    }
}