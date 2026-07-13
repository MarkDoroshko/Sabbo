package com.example.domain.entity

import java.io.IOException

data class AppHttpException(
    val code: Int,
    val errorCode: String? = null,
    override val message: String = "HTTP $code"
) : IOException(message)
