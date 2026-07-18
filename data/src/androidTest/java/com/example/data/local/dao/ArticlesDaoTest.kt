package com.example.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.database.NewsDatabase
import com.example.data.local.model.ArticleDbModel
import com.example.data.local.model.TopicDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticlesDaoTest {
    private lateinit var db: NewsDatabase
    private lateinit var dao: ArticlesDao
    private lateinit var topicsDao: TopicsDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = NewsDatabase::class.java
        ).build()

        dao = db.articlesDao()
        topicsDao = db.topicsDao()
    }

    @After
    fun tearDown() = db.close()

    private fun article(
        topic: String,
        url: String,
        publishedAt: Long
    ) = ArticleDbModel(
        title = "Title $url",
        description = "Description $url",
        imageUrl = "Image $url",
        sourceName = "Source",
        publishedAt = publishedAt,
        url = url,
        topic = topic
    )

    @Test
    fun `addArticles inserts a new article and returns its rowId`() = runTest {
        topicsDao.addTopic(TopicDbModel("Kotlin"))
        val newArticle = article(topic = "Kotlin", url = "url1", publishedAt = 1_000L)

        val ids = dao.addArticles(listOf(newArticle))

        assertEquals(1, ids.size)
        assertTrue("expected a real rowId, got -1 (ignored insert)", ids[0] != -1L)
    }

    @Test
    fun `getAllArticlesByTopic returns articles ordered by publishedAt descending`() = runTest {
        topicsDao.addTopic(TopicDbModel("Kotlin"))
        val older = article(topic = "Kotlin", url = "url-old", publishedAt = 1_000L)
        val newer = article(topic = "Kotlin", url = "url-new", publishedAt = 2_000L)
        // вставляем в "неправильном" порядке специально, чтобы проверить, что порядок в ответе
        // определяет ORDER BY, а не порядок вставки
        dao.addArticles(listOf(older, newer))

        val result = dao.getAllArticlesByTopic("Kotlin").first()

        assertEquals(listOf(newer, older), result)
    }

    @Test
    fun `addArticles ignores duplicate article with the same url and topic`() = runTest {
        topicsDao.addTopic(TopicDbModel("Kotlin"))
        val newArticle = article(topic = "Kotlin", url = "url1", publishedAt = 1_000L)
        dao.addArticles(listOf(newArticle))

        val secondInsertIds = dao.addArticles(listOf(newArticle))

        assertEquals(listOf(-1L), secondInsertIds)
        assertEquals(listOf(newArticle), dao.getAllArticlesByTopic("Kotlin").first())
    }

    @Test
    fun `getAllArticlesByTopics returns articles only for the requested topics`() = runTest {
        topicsDao.addTopic(TopicDbModel("Kotlin"))
        topicsDao.addTopic(TopicDbModel("Java"))
        topicsDao.addTopic(TopicDbModel("Android"))
        val kotlinArticle = article(topic = "Kotlin", url = "url-kotlin", publishedAt = 1_000L)
        val javaArticle = article(topic = "Java", url = "url-java", publishedAt = 2_000L)
        val androidArticle = article(topic = "Android", url = "url-android", publishedAt = 3_000L)
        dao.addArticles(listOf(kotlinArticle, javaArticle, androidArticle))

        val result = dao.getAllArticlesByTopics(listOf("Kotlin", "Java")).first()

        assertEquals(listOf(javaArticle, kotlinArticle), result)
    }

    @Test
    fun `deleteArticlesByTopics removes only articles for the specified topics`() = runTest {
        topicsDao.addTopic(TopicDbModel("Kotlin"))
        topicsDao.addTopic(TopicDbModel("Java"))
        val kotlinArticle = article(topic = "Kotlin", url = "url-kotlin", publishedAt = 1_000L)
        val javaArticle = article(topic = "Java", url = "url-java", publishedAt = 2_000L)
        dao.addArticles(listOf(kotlinArticle, javaArticle))

        dao.deleteArticlesByTopics(listOf("Kotlin"))

        assertEquals(emptyList<ArticleDbModel>(), dao.getAllArticlesByTopic("Kotlin").first())
        assertEquals(listOf(javaArticle), dao.getAllArticlesByTopic("Java").first())
    }

    @Test
    fun `deleting a topic cascades and removes its articles`() = runTest {
        val topic = TopicDbModel("Kotlin")
        topicsDao.addTopic(topic)
        dao.addArticles(listOf(article(topic = "Kotlin", url = "url1", publishedAt = 1_000L)))

        topicsDao.deleteTopic(topic)

        assertEquals(emptyList<ArticleDbModel>(), dao.getAllArticlesByTopic("Kotlin").first())
    }

    @Test
    fun `addArticles throws when the topic does not exist`() {
        val orphanArticle = article(topic = "NonExistent", url = "url1", publishedAt = 1_000L)

        assertThrows(Exception::class.java) {
            runBlocking { dao.addArticles(listOf(orphanArticle)) }
        }
    }
}
