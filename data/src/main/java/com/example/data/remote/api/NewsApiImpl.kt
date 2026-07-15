package com.example.data.remote.api

import com.example.data.remote.dto.NewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class NewsApiImpl @Inject constructor(
    private val client: HttpClient
) : NewsApi {
    override suspend fun getArticles(topic: String, language: String): NewsResponseDto {
        return client.get("everything") {
            parameter("q", topic)
            parameter("language", language)
        }.body()
    }
}