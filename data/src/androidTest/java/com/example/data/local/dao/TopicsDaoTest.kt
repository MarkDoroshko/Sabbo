package com.example.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.database.NewsDatabase
import com.example.data.local.model.TopicDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopicsDaoTest {
    private lateinit var db: NewsDatabase
    private lateinit var dao: TopicsDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = NewsDatabase::class.java
        ).build()

        dao = db.topicsDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `getAllTopics returns topic from table`() = runTest {
        val topicDbModel = TopicDbModel("Kotlin")

        dao.addTopic(topicDbModel)

        val currentTopics = dao.getAllTopics().first()

        assertEquals(listOf(topicDbModel), currentTopics)
    }

    @Test
    fun `addTopic ignores insert when the same topic already exists`() = runTest {
        val topicDbModel = TopicDbModel("Kotlin")

        dao.addTopic(topicDbModel)
        dao.addTopic(topicDbModel)

        val currentTopics = dao.getAllTopics().first()

        assertEquals(listOf(topicDbModel), currentTopics)
    }

    @Test
    fun `deleteTopic removes the topic from the table`() = runTest {
        val topicDbModel = TopicDbModel("Kotlin")
        dao.addTopic(topicDbModel)

        dao.deleteTopic(topicDbModel)

        val currentTopics = dao.getAllTopics().first()

        assertEquals(emptyList<TopicDbModel>(), currentTopics)
    }
}
