package com.example.data.mapper

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import org.junit.Assert.*
import org.junit.Test

class SettingsMapperTest {
    @Test
    fun `toInterval returns a valid Interval with a expected number`() {
        val intervalInt = 15

        val result = intervalInt.toInterval()

        assertEquals(Interval.MIN_15, result)
    }

    @Test
    fun `toInterval return a default Interval with a unexpected numbed`() {
        val intervalInt = 566

        val result = intervalInt.toInterval()

        assertEquals(Settings.DEFAULT_INTERVAL, result)
    }

    @Test
    fun `toQueryParam returns the string en in english`() {
        val lang = Language.ENGLISH

        val result = lang.toQueryParam()

        assertEquals("en", result)
    }

    @Test
    fun `toQueryParam returns the string ru in russian`() {
        val lang = Language.RUSSIAN

        val result = lang.toQueryParam()

        assertEquals("ru", result)
    }

    @Test
    fun `toQueryParam returns the string fr in french`() {
        val lang = Language.FRENCH

        val result = lang.toQueryParam()

        assertEquals("fr", result)
    }

    @Test
    fun `toQueryParam returns the string de in german`() {
        val lang = Language.GERMAN

        val result = lang.toQueryParam()

        assertEquals("de", result)
    }
}