package com.example.domain.usecase

import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class GetAllTopicsUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    operator fun invoke() = topicRepository.getAllTopics()
}