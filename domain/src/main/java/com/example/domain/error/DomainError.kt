package com.example.domain.error

sealed interface DomainError {
    data object NoConnection : DomainError
    data object Unknown : DomainError
    data object ServerError : DomainError
    data object RateLimited : DomainError
}