package com.example.domain.usecase.topic

import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class AddTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    suspend operator fun invoke(topic: String) = topicRepository.addTopic(topic)
}