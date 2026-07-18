package com.example.data.repository

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Operation
import androidx.work.WorkManager
import com.example.data.local.dao.ArticlesDao
import com.example.data.local.dao.TopicsDao
import com.example.data.local.model.ArticleDbModel
import com.example.data.local.model.TopicDbModel
import com.example.data.mapper.toQueryParam
import com.example.data.remote.api.NewsApi
import com.example.data.remote.dto.ArticleDto
import com.example.data.remote.dto.NewsResponseDto
import com.example.data.remote.dto.SourceDto
import com.example.domain.entity.Article
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.RefreshConfig
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import io.ktor.utils.io.CancellationException
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ArticleRepositoryImplTest {
    private val newsApi = mockk<NewsApi>()
    private val topicsDao = mockk<TopicsDao>()
    private val articlesDao = mockk<ArticlesDao>()
    private val workManager = mockk<WorkManager>()

    private val repository = ArticleRepositoryImpl(newsApi, topicsDao, articlesDao, workManager)

    private val language = Language.ENGLISH

    private fun articleDto(topic: String) = ArticleDto(
        title = "Title $topic",
        description = "Description $topic",
        publishedAt = "2024-01-01T12:00:00Z",
        url = "https://example.com/$topic",
        source = SourceDto(name = "Source")
    )

    @Test
    fun `getArticlesByTopic maps stored ArticleDbModel list into domain Article list`() = runTest {
        val topic = "Kotlin"
        val dbModel = ArticleDbModel(
            title = "Title",
            description = "Description",
            imageUrl = "Image",
            sourceName = "Source",
            publishedAt = 1_700_000_000_000L,
            url = "Url",
            topic = topic
        )
        every { articlesDao.getAllArticlesByTopic(topic) } returns flowOf(listOf(dbModel))

        val result = repository.getArticlesByTopic(topic).first()

        assertEquals(
            listOf(
                Article(
                    title = "Title",
                    description = "Description",
                    imageUrl = "Image",
                    sourceName = "Source",
                    publishedAt = 1_700_000_000_000L,
                    url = "Url",
                    topic = topic
                )
            ),
            result
        )
    }

    @Test
    fun `getArticlesByTopics maps stored ArticleDbModel list into domain Article list`() = runTest {
        val topics = listOf("Kotlin", "Java")
        val dbModel = ArticleDbModel(
            title = "Title",
            description = "Description",
            imageUrl = "Image",
            sourceName = "Source",
            publishedAt = 1_700_000_000_000L,
            url = "Url",
            topic = "Kotlin"
        )
        every { articlesDao.getAllArticlesByTopics(topics) } returns flowOf(listOf(dbModel))

        val result = repository.getArticlesByTopics(topics).first()

        assertEquals(
            listOf(
                Article(
                    title = "Title",
                    description = "Description",
                    imageUrl = "Image",
                    sourceName = "Source",
                    publishedAt = 1_700_000_000_000L,
                    url = "Url",
                    topic = "Kotlin"
                )
            ),
            result
        )
    }

    @Test
    fun `updateArticlesForTopic returns Success true when at least one article is newly inserted`() =
        runTest {
            val topic = "Kotlin"
            coEvery { newsApi.getArticles(topic, language.toQueryParam()) } returns NewsResponseDto(
                articles = listOf(articleDto(topic))
            )
            coEvery { articlesDao.addArticles(any()) } returns listOf(1L)

            val result = repository.updateArticlesForTopic(topic, language)

            assertEquals(AppResult.Success(true), result)
        }

    @Test
    fun `updateArticlesForTopic returns Success false when all articles were already stored`() =
        runTest {
            val topic = "Kotlin"
            coEvery { newsApi.getArticles(topic, language.toQueryParam()) } returns NewsResponseDto(
                articles = listOf(articleDto(topic))
            )
            // Room возвращает -1 для строки, отклонённой OnConflictStrategy.IGNORE — то есть дубликата
            coEvery { articlesDao.addArticles(any()) } returns listOf(-1L)

            val result = repository.updateArticlesForTopic(topic, language)

            assertEquals(AppResult.Success(false), result)
        }

    @Test
    fun `updateArticlesForTopic returns Failure NoConnection when the network call throws IOException`() =
        runTest {
            val topic = "Kotlin"
            coEvery { newsApi.getArticles(topic, language.toQueryParam()) } throws IOException("no network")

            val result = repository.updateArticlesForTopic(topic, language)

            assertEquals(AppResult.Failure(DomainError.NoConnection), result)
            coVerify(exactly = 0) { articlesDao.addArticles(any()) }
        }

    @Test
    fun `updateArticlesForTopic rethrows CancellationException instead of wrapping it into Failure`() =
        runTest {
            val topic = "Kotlin"
            coEvery {
                newsApi.getArticles(topic, language.toQueryParam())
            } throws CancellationException("cancelled")

            var caught: CancellationException? = null
            try {
                repository.updateArticlesForTopic(topic, language)
            } catch (e: CancellationException) {
                caught = e
            }

            assertNotNull(caught)
        }

    @Test
    fun `updateArticlesForAllTopics returns only topics that received new articles`() = runTest {
        every { topicsDao.getAllTopics() } returns flowOf(
            listOf(TopicDbModel("Kotlin"), TopicDbModel("Java"), TopicDbModel("Android"))
        )

        // Kotlin: пришли новые статьи
        coEvery { newsApi.getArticles("Kotlin", language.toQueryParam()) } returns NewsResponseDto(
            articles = listOf(articleDto("Kotlin"))
        )
        coEvery {
            articlesDao.addArticles(match { models -> models.all { it.topic == "Kotlin" } })
        } returns listOf(1L)

        // Java: статьи пришли, но все уже были в базе (addArticles вернул -1)
        coEvery { newsApi.getArticles("Java", language.toQueryParam()) } returns NewsResponseDto(
            articles = listOf(articleDto("Java"))
        )
        coEvery {
            articlesDao.addArticles(match { models -> models.all { it.topic == "Java" } })
        } returns listOf(-1L)

        // Android: сеть упала
        coEvery {
            newsApi.getArticles("Android", language.toQueryParam())
        } throws IOException("no network")

        val result = repository.updateArticlesForAllTopics(language)

        assertEquals(listOf("Kotlin"), result)
    }

    @Test
    fun `clearAllArticles delegates deletion to articlesDao`() = runTest {
        val topics = listOf("Kotlin", "Java")
        coJustRun { articlesDao.deleteArticlesByTopics(topics) }

        repository.clearAllArticles(topics)

        coVerify { articlesDao.deleteArticlesByTopics(topics) }
    }

    @Test
    fun `startBackgroundRefresh enqueues unique periodic work with CANCEL_AND_REENQUEUE policy`() {
        every {
            workManager.enqueueUniquePeriodicWork(any(), any(), any())
        } returns mockk<Operation>()

        repository.startBackgroundRefresh(
            RefreshConfig(language = Language.ENGLISH, interval = Interval.MIN_30, wifiOnly = true)
        )

        verify {
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = "Refresh Data",
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request = any()
            )
        }
    }
}
