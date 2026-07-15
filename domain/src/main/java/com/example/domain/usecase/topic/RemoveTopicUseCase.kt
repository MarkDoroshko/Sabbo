package com.example.domain.usecase.topic

import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class RemoveTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    suspend operator fun invoke(topic: String) = topicRepository.removeTopic(topic)
}