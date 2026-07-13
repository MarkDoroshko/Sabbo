package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.model.TopicDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicsDao {
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<TopicDbModel>>

    @Insert(onConflict = IGNORE)
    suspend fun addTopic(topicDbModel: TopicDbModel)

    @Transaction
    @Delete
    suspend fun deleteTopic(topicDbModel: TopicDbModel)
}