package com.example.presentation.screen.topics

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopicsScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysTopicsCountAndList() {
        composeRule.setContent {
            TopicsScreen(
                topics = listOf("Kotlin", "Java"),
                topicInput = "",
                onTopicInputChange = {},
                onAddTopic = {},
                onRemoveTopic = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.topics_count_label, 2)
        ).assertExists()
        composeRule.onNodeWithText("Kotlin").assertExists()
        composeRule.onNodeWithText("Java").assertExists()
    }

    @Test
    fun showsEmptyMessageWhenThereAreNoTopics() {
        composeRule.setContent {
            TopicsScreen(
                topics = emptyList(),
                topicInput = "",
                onTopicInputChange = {},
                onAddTopic = {},
                onRemoveTopic = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.topics_empty_message)
        ).assertExists()
    }

    @Test
    fun typingInTheInputFieldTriggersOnTopicInputChange() {
        var typedValue: String? = null
        composeRule.setContent {
            TopicsScreen(
                topics = emptyList(),
                topicInput = "",
                onTopicInputChange = { typedValue = it },
                onAddTopic = {},
                onRemoveTopic = {}
            )
        }

        composeRule.onNodeWithTag("topic_input_field").performTextInput("Compose")

        assertEquals("Compose", typedValue)
    }

    @Test
    fun clickingAddButtonTriggersOnAddTopic() {
        var addTopicCalled = false
        composeRule.setContent {
            TopicsScreen(
                topics = emptyList(),
                topicInput = "Compose",
                onTopicInputChange = {},
                onAddTopic = { addTopicCalled = true },
                onRemoveTopic = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.add_topic_button)
        ).performClick()

        assertEquals(true, addTopicCalled)
    }

    @Test
    fun clickingRemoveIconTriggersOnRemoveTopicWithTheCorrectTopic() {
        var removedTopic: String? = null
        composeRule.setContent {
            TopicsScreen(
                topics = listOf("Kotlin", "Java"),
                topicInput = "",
                onTopicInputChange = {},
                onAddTopic = {},
                onRemoveTopic = { removedTopic = it }
            )
        }

        val removeLabel = composeRule.activity.getString(R.string.remove_topic)
        composeRule.onAllNodesWithContentDescription(removeLabel)[1].performClick()

        assertEquals("Java", removedTopic)
    }
}
