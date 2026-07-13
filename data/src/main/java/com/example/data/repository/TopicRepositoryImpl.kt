package com.example.data.repository

import com.example.data.local.dao.TopicsDao
import com.example.data.local.model.TopicDbModel
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicsDao: TopicsDao
) : TopicRepository {
    override fun getAllTopics(): Flow<List<String>> {
        return topicsDao.getAllTopics().map { topics ->
            topics.map { it.topic }
        }
    }

    override suspend fun addTopic(topic: String) {
        topicsDao.addTopic(TopicDbModel(topic))
    }

    override suspend fun removeTopic(topic: String) {
        topicsDao.deleteTopic(TopicDbModel(topic))
    }
}