package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.dao.ArticlesDao
import com.example.data.local.dao.TopicsDao
import com.example.data.local.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNewsDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase = Room.databaseBuilder(
        context = context,
        klass = NewsDatabase::class.java,
        name = "news.db"
    ).build()

    @Provides
    @Singleton
    fun provideTopicsDao(
        database: NewsDatabase
    ): TopicsDao = database.topicsDao()

    @Provides
    @Singleton
    fun provideArticlesDao(
        database: NewsDatabase
    ): ArticlesDao = database.articlesDao()
}