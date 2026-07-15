package com.example.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ErrorResponse(
    val status: String,
    val code: String,
    val message: String
)

fun parseErrorBody(body: String?, json: Json): ErrorResponse? {
    return try {
        if (body.isNullOrBlank()) null
        else json.decodeFromString<ErrorResponse>(body)
    } catch (_: Exception) {
        null
    }
}