package com.example.data.mapper

import com.example.data.local.model.ArticleDbModel
import com.example.data.remote.dto.ArticleDto
import com.example.data.remote.dto.NewsResponseDto
import com.example.data.remote.dto.SourceDto
import com.example.domain.entity.Article
import org.junit.Test
import org.junit.Assert.*
import kotlin.time.Instant

class ArticleMapperTest {
    @Test
    fun `toEntities copies all fields from ArticleDbModel to Article`() {
        val articleDbModels = listOf(
            ArticleDbModel(
                title = "Article Title",
                description = "Article Desc",
                imageUrl = "Article Image Url",
                sourceName = "Article Name",
                publishedAt = Instant.parse("2024-01-01T12:00:00Z").toEpochMilliseconds(),
                url = "Article Url",
                topic = "Article Topic"
            )
        )

        val result = articleDbModels.toEntities()

        assertEquals(
            listOf(
                Article(
                    title = "Article Title",
                    description = "Article Desc",
                    imageUrl = "Article Image Url",
                    sourceName = "Article Name",
                    publishedAt = Instant.parse("2024-01-01T12:00:00Z").toEpochMilliseconds(),
                    url = "Article Url",
                    topic = "Article Topic"
                )
            ),
            result
        )
    }

    @Test
    fun `toDbModels extracts SourceName from nested SourceData and parses publishedAt in milliseconds`() {
        val publishedAtString = "2024-01-01T12:00:00Z"

        val newsResponseDto = NewsResponseDto(
            articles = listOf(
                ArticleDto(
                    title = "Article Title",
                    description = "Article Desc",
                    publishedAt = publishedAtString,
                    url = "Article Url",
                    urlToImage = "Article Image Url",
                    source = SourceDto(name = "Article Name")
                )
            )
        )

        val result = newsResponseDto.toDbModels(topic = "Kotlin")

        assertEquals(
            listOf(
                ArticleDbModel(
                    title = "Article Title",
                    description = "Article Desc",
                    imageUrl = "Article Image Url",
                    sourceName = "Article Name",
                    publishedAt = Instant.parse(publishedAtString).toEpochMilliseconds(),
                    url = "Article Url",
                    topic = "Kotlin"
                )
            ),
            result
        )
    }
}