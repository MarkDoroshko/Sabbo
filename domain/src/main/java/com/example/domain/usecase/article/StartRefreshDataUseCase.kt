package com.example.domain.usecase.article

import com.example.domain.mapper.toRefreshConfig
import com.example.domain.repository.ArticleRepository
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StartRefreshDataUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        settingsRepository.getSettings()
            .map { it.toRefreshConfig() }
            .distinctUntilChanged()
            .onEach { articleRepository.startBackgroundRefresh(it) }
            .collect()
    }
}