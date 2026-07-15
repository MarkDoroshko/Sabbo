package com.example.data.mapper

import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings

fun Int.toInterval(): Interval =
    Interval.entries.firstOrNull { it.minutes == this } ?: Settings.DEFAULT_INTERVAL

fun Language.toQueryParam(): String = when (this) {
    Language.ENGLISH -> "en"
    Language.RUSSIAN -> "ru"
    Language.FRENCH -> "fr"
    Language.GERMAN -> "de"
}
