package com.example.domain.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppResultMapTest {

    @Test
    fun `map applies transform to Success and wraps the new value`() {
        val success: AppResult<Int> = AppResult.Success(5)

        val result = success.map { it * 2 }

        assertEquals(AppResult.Success(10), result)
    }

    @Test
    fun `map returns Failure unchanged without calling transform`() {
        var transformWasCalled = false
        val failure: AppResult<Int> = AppResult.Failure(DomainError.NoConnection)

        val result = failure.map {
            transformWasCalled = true
            it.toString()
        }

        assertEquals(AppResult.Failure(DomainError.NoConnection), result)
        assertFalse(transformWasCalled)
    }
}
