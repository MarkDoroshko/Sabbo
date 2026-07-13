package com.example.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.ArticlesDao
import com.example.data.local.dao.TopicsDao
import com.example.data.local.model.ArticleDbModel
import com.example.data.local.model.TopicDbModel

@Database(
    entities = [TopicDbModel::class, ArticleDbModel::class],
    version = 1
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun topicsDao(): TopicsDao
    abstract fun articlesDao(): ArticlesDao
}