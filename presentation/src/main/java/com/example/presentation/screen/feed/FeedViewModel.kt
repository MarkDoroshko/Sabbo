package com.example.presentation.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Article
import com.example.domain.error.AppResult
import com.example.domain.error.DomainError
import com.example.domain.usecase.article.ClearAllArticlesUseCase
import com.example.domain.usecase.article.GetArticlesByTopicUseCase
import com.example.domain.usecase.article.GetArticlesByTopicsUseCase
import com.example.domain.usecase.article.UpdateArticlesForAllTopicsUseCase
import com.example.domain.usecase.article.UpdateArticlesForTopicUseCase
import com.example.domain.usecase.topic.GetAllTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val updateArticlesForAllTopicsUseCase: UpdateArticlesForAllTopicsUseCase,
    private val updateArticlesForTopicUseCase: UpdateArticlesForTopicUseCase,
    private val getAllTopicsUseCase: GetAllTopicsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val getArticlesByTopicUseCase: GetArticlesByTopicUseCase
) : ViewModel() {
    private val selectedTopic = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    private val _effect = Channel<FeedEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeArticles()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeArticles() {
        viewModelScope.launch {
            combine(getAllTopicsUseCase(), selectedTopic) { topics, topic -> topics to topic }
                .flatMapLatest { (topics, topic) ->
                    val articlesFlow = if (topic == null) {
                        getArticlesByTopicsUseCase(topics)
                    } else {
                        getArticlesByTopicUseCase(topic)
                    }
                    articlesFlow.map { articles ->
                        FeedState(
                            topics = topics,
                            selectedTopic = topic,
                            articles = articles,
                            isLoading = false
                        )
                    }
                }
                .collect { newState -> _state.value = newState }
        }
    }

    fun processIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.SelectTopic -> selectedTopic.value = intent.topic
            is FeedIntent.OpenArticle -> sendEffect(FeedEffect.NavigateToArticle(intent.url))
            FeedIntent.RefreshArticles -> refreshArticles()
            FeedIntent.ClearArticles -> clearArticles()
        }
    }

    private fun refreshArticles() {
        viewModelScope.launch {
            val topic = _state.value.selectedTopic
            if (topic == null) {
                val updatedTopics = updateArticlesForAllTopicsUseCase()
                if (updatedTopics.size < _state.value.topics.size) {
                    _effect.send(FeedEffect.RefreshPartial)
                }
            } else {
                val result = updateArticlesForTopicUseCase(topic)
                if (result is AppResult.Failure) _effect.send(FeedEffect.RefreshFailed(result.error))
            }
        }
    }

    private fun clearArticles() {
        viewModelScope.launch {
            try {
                clearAllArticlesUseCase(_state.value.topics)
                _effect.send(FeedEffect.ArticlesCleared)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _effect.send(FeedEffect.ClearFailed(e.message))
            }
        }
    }

    private fun sendEffect(effect: FeedEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

data class FeedState(
    val topics: List<String> = emptyList(),
    val selectedTopic: String? = null,          // null = "Все"

    val articles: List<Article> = emptyList(),

    val isLoading: Boolean = true
)

sealed interface FeedIntent {
    data class SelectTopic(val topic: String?) : FeedIntent
    data class OpenArticle(val url: String) : FeedIntent
    data object RefreshArticles : FeedIntent
    data object ClearArticles : FeedIntent
}

sealed interface FeedEffect {
    data object ArticlesCleared : FeedEffect                       // снэкбар
    data object RefreshPartial : FeedEffect                        // тост, часть тем не обновилась
    data class RefreshFailed(val error: DomainError) : FeedEffect  // тост
    data class ClearFailed(val message: String?) : FeedEffect      // тост
    data class NavigateToArticle(val url: String) : FeedEffect
}