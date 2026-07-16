package com.example.presentation.mapper

import com.example.domain.error.DomainError
import com.example.presentation.R

fun DomainError.toMessageRes(): Int = when (this) {
    DomainError.NoConnection -> R.string.no_internet_connection
    DomainError.ServerError -> R.string.server_error
    DomainError.RateLimited -> R.string.rate_limited
    DomainError.Unknown -> R.string.refresh_failed_partial
}