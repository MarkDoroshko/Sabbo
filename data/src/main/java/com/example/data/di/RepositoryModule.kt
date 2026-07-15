package com.example.data.di

import com.example.data.repository.ArticleRepositoryImpl
import com.example.data.repository.TopicRepositoryImpl
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.TopicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository

    @Binds
    @Singleton
    fun bindTopicRepository(impl: TopicRepositoryImpl): TopicRepository
}
