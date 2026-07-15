package com.example.data.remote.api

import com.example.data.remote.dto.NewsResponseDto

interface NewsApi {
    suspend fun getArticles(topic: String, language: String): NewsResponseDto
}