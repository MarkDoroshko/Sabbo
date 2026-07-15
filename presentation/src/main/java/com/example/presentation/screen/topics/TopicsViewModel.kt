package com.example.presentation.screen.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.topic.AddTopicUseCase
import com.example.domain.usecase.topic.GetAllTopicsUseCase
import com.example.domain.usecase.topic.RemoveTopicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val addTopicUseCase: AddTopicUseCase,
    private val removeTopicUseCase: RemoveTopicUseCase,
    getAllTopicsUseCase: GetAllTopicsUseCase
) : ViewModel() {
    val topics: StateFlow<List<String>> = getAllTopicsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _topicInput = MutableStateFlow("")
    val topicInput: StateFlow<String> = _topicInput.asStateFlow()

    fun onTopicInputChange(value: String) {
        _topicInput.value = value
    }

    fun addTopic() {
        val topic = _topicInput.value.trim()
        if (topic.isEmpty() || topics.value.contains(topic)) return

        viewModelScope.launch { addTopicUseCase(topic) }
        _topicInput.value = ""
    }

    fun removeTopic(topic: String) {
        viewModelScope.launch {
            removeTopicUseCase(topic)
        }
    }
}