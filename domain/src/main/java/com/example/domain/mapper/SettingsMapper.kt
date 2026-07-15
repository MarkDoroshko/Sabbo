package com.example.domain.mapper

import com.example.domain.entity.RefreshConfig
import com.example.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig = RefreshConfig(language, interval, wifiOnly)