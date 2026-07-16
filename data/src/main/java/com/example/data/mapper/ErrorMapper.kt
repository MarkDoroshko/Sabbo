package com.example.data.mapper

import com.example.domain.entity.AppHttpException
import com.example.domain.error.DomainError
import kotlinx.io.IOException

fun Throwable.toDomainError(): DomainError = when (this) {
    is AppHttpException -> when {
        code in 500..599 -> DomainError.ServerError
        errorCode == "rateLimited" -> DomainError.RateLimited
        else -> DomainError.Unknown
    }
    is IOException -> DomainError.NoConnection
    else -> DomainError.Unknown
}