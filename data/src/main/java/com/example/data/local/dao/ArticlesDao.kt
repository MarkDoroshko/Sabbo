package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.example.data.local.model.ArticleDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM articles WHERE topic = :topic ORDER BY publishedAt DESC")
    fun getAllArticlesByTopic(topic: String): Flow<List<ArticleDbModel>>

    @Query("SELECT * FROM articles WHERE topic in (:topics) ORDER BY publishedAt DESC")
    fun getAllArticlesByTopics(topics: List<String>): Flow<List<ArticleDbModel>>

    @Query("DELETE FROM articles WHERE topic in (:topics)")
    suspend fun deleteArticlesByTopics(topics: List<String>)

    @Insert(onConflict = IGNORE)
    suspend fun addArticles(articles: List<ArticleDbModel>): List<Long>
}