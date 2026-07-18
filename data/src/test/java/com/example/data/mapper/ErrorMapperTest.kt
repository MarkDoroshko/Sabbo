package com.example.data.mapper

import com.example.data.remote.entity.AppHttpException
import com.example.domain.error.DomainError
import kotlinx.io.IOException
import org.junit.Assert.*
import org.junit.Test

class ErrorMapperTest {
    @Test
    fun `toDomainError returns ServerError with 500-599 code in AppHttpException`() {
        val exception = AppHttpException(code = 500)

        val result = exception.toDomainError()

        assertEquals(DomainError.ServerError, result)
    }

    @Test
    fun `toDomainError returns RateLimited with errorCode = rateLimited in AppHttpException`() {
        val exception = AppHttpException(code = 421, errorCode = "rateLimited")

        val result = exception.toDomainError()

        assertEquals(DomainError.RateLimited, result)
    }

    @Test
    fun `toDomainError returns Unknown with other case in AppHttpException`() {
        val exception = AppHttpException(code = 404)

        val result = exception.toDomainError()

        assertEquals(DomainError.Unknown, result)
    }

    @Test
    fun `toDomainError returns NoConnection with IOException`() {
        val exception = IOException()

        val result = exception.toDomainError()

        assertEquals(DomainError.NoConnection, result)
    }

    @Test
    fun `toDomainError returns Unknown with other exception`() {
        val exception = Exception()

        val result = exception.toDomainError()

        assertEquals(DomainError.Unknown, result)
    }
}