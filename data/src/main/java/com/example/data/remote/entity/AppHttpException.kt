package com.example.data.remote.entity

import kotlinx.io.IOException

data class AppHttpException(
    val code: Int,
    val errorCode: String? = null,
    override val message: String = "HTTP $code"
) : IOException(message)