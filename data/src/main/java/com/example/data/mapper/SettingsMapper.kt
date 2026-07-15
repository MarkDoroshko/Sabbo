package com.example.data.mapper

import com.example.domain.entity.Interval
import com.example.domain.entity.Settings

fun Int.toInterval(): Interval =
    Interval.entries.firstOrNull { it.minutes == this } ?: Settings.DEFAULT_INTERVAL
