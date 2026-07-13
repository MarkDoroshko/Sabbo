package com.example.data.di

import com.example.data.BuildConfig
import com.example.data.remote.dto.parseErrorBody
import com.example.domain.entity.AppHttpException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val BASE_API_URL = "https://newsapi.org/v2/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json
    ): HttpClient {
        return HttpClient(CIO) {
            // Общие настройки для всех запросов через клиент
            defaultRequest {
                url(BASE_API_URL)
                accept(ContentType.Application.Json)
                header("X-Api-Key", BuildConfig.NEWS_API_KEY)
            }

            // Перехват ответов и обработка ошибок
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode >= 400) {
                        val body = response.body<String>()
                        val errorDetails = parseErrorBody(body)

                        throw AppHttpException(
                            code = statusCode,
                            errorCode = errorDetails?.code,
                            message = errorDetails?.message ?: "HTTP $statusCode"
                        )
                    }
                }
            }

            // Сериализация и десериализация
            install(ContentNegotiation) {
                json(json)
            }

            // Логирование
            install(Logging) {
                level = LogLevel.INFO

                sanitizeHeader { header ->
                    header.equals(HttpHeaders.Authorization, ignoreCase = true) ||
                            header.equals("X-Api-Key", ignoreCase = true)
                }
            }

            // Retry логика
            install(HttpRequestRetry) {
                maxRetries = 5
                retryIf { _, response ->
                    response.status.value in 500..599
                }
                exponentialDelay()
            }
        }
    }
}