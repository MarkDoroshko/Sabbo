package com.example.presentation.screen.feed

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.entity.Article
import com.example.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun article(topic: String, url: String, title: String) = Article(
        title = title,
        description = "Description",
        imageUrl = null,
        sourceName = "Source",
        publishedAt = System.currentTimeMillis(),
        url = url,
        topic = topic
    )

    @Test
    fun showsEmptyStateWhenThereAreNoArticles() {
        composeRule.setContent {
            FeedScreen(
                state = FeedState(isLoading = false, articles = emptyList()),
                snackbarHostState = SnackbarHostState(),
                onRefreshArticles = {},
                onClearArticles = {},
                onOpenArticle = {},
                onSelectTopic = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.feed_empty_title)
        ).assertExists()
    }

    @Test
    fun displaysArticleTitles() {
        val articles = listOf(
            article(topic = "Kotlin", url = "url1", title = "Kotlin 2.4 released"),
            article(topic = "Java", url = "url2", title = "Java virtual threads")
        )
        composeRule.setContent {
            FeedScreen(
                state = FeedState(isLoading = false, articles = articles),
                snackbarHostState = SnackbarHostState(),
                onRefreshArticles = {},
                onClearArticles = {},
                onOpenArticle = {},
                onSelectTopic = {}
            )
        }

        composeRule.onNodeWithText("Kotlin 2.4 released").assertExists()
        composeRule.onNodeWithText("Java virtual threads").assertExists()
    }

    @Test
    fun clickingReadArticleTriggersOnOpenArticleWithTheArticleUrl() {
        var openedUrl: String? = null
        val articles = listOf(
            article(topic = "Kotlin", url = "url1", title = "First"),
            article(topic = "Java", url = "url2", title = "Second")
        )
        composeRule.setContent {
            FeedScreen(
                state = FeedState(isLoading = false, articles = articles),
                snackbarHostState = SnackbarHostState(),
                onRefreshArticles = {},
                onClearArticles = {},
                onOpenArticle = { openedUrl = it },
                onSelectTopic = {}
            )
        }

        val readLabel = composeRule.activity.getString(R.string.read_article)
        composeRule.onAllNodesWithText(readLabel)[1].performClick()

        assertEquals("url2", openedUrl)
    }

    @Test
    fun clickingATopicChipTriggersOnSelectTopicWithThatTopic() {
        var selectedTopic: String? = "unset"
        composeRule.setContent {
            FeedScreen(
                state = FeedState(topics = listOf("Kotlin", "Java"), isLoading = false),
                snackbarHostState = SnackbarHostState(),
                onRefreshArticles = {},
                onClearArticles = {},
                onOpenArticle = {},
                onSelectTopic = { selectedTopic = it }
            )
        }

        composeRule.onNodeWithText("Java").performClick()

        assertEquals("Java", selectedTopic)
    }
}
