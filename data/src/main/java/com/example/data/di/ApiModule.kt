package com.example.data.di

import com.example.data.remote.api.NewsApi
import com.example.data.remote.api.NewsApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApiModule {
    @Binds
    @Singleton
    fun bindNewsApi(impl: NewsApiImpl): NewsApi
}