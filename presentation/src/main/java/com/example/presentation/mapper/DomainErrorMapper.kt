package com.example.presentation.mapper

import android.content.Context
import com.example.domain.error.DomainError
import com.example.presentation.R

fun DomainError.toMessage(context: Context): String = when (this) {
    DomainError.NoConnection -> context.getString(R.string.no_internet_connection)
    DomainError.ServerError -> context.getString(R.string.server_error)
    DomainError.RateLimited -> context.getString(R.string.rate_limited)
    DomainError.Unknown -> context.getString(R.string.refresh_failed_partial)
}