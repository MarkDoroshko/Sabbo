package com.example.data.mapper

import com.example.data.local.model.ArticleDbModel
import com.example.data.remote.dto.NewsResponseDto
import com.example.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun List<ArticleDbModel>.toEntities(): List<Article> {
    return map {
        Article(
            title = it.title,
            description = it.description,
            imageUrl = it.imageUrl,
            sourceName = it.sourceName,
            publishedAt = it.publishedAt,
            url = it.url,
            topic = it.topic
        )
    }
}

fun NewsResponseDto.toDbModels(topic: String): List<ArticleDbModel> {
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            publishedAt = it.publishedAt.toTimestamp(),
            url = it.url,
            topic = topic,
        )
    }
}

private fun String.toTimestamp(): Long {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}