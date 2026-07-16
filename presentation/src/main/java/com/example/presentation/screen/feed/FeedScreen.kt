package com.example.presentation.screen.feed

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.domain.entity.Article
import com.example.presentation.R
import com.example.presentation.components.AppTopBar
import com.example.presentation.mapper.toMessage
import com.example.presentation.theme.SabboTheme
import kotlinx.coroutines.launch

@Composable
fun FeedRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: FeedViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clearedMessage = stringResource(R.string.articles_cleared_message)
    val refreshFailedMessage = stringResource(R.string.refresh_failed_partial)
    val clearFailedMessage = stringResource(R.string.clear_articles_failed)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FeedEffect.ArticlesCleared -> scope.launch {
                    snackbarHostState.showSnackbar(clearedMessage)
                }

                FeedEffect.RefreshPartial -> Toast.makeText(
                    context, refreshFailedMessage, Toast.LENGTH_SHORT
                ).show()

                is FeedEffect.ClearFailed -> Toast.makeText(
                    context,
                    effect.message ?: clearFailedMessage,
                    Toast.LENGTH_SHORT
                ).show()

                is FeedEffect.RefreshFailed -> Toast.makeText(
                    context, effect.error.toMessage(context), Toast.LENGTH_SHORT
                ).show()

                is FeedEffect.NavigateToArticle -> context.startActivity(
                    Intent(Intent.ACTION_VIEW, effect.url.toUri())
                )
            }
        }
    }

    FeedScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefreshArticles = { viewModel.processIntent(FeedIntent.RefreshArticles) },
        onClearArticles = { viewModel.processIntent(FeedIntent.ClearArticles) },
        onOpenArticle = { url -> viewModel.processIntent(FeedIntent.OpenArticle(url)) },
        onSelectTopic = { topic -> viewModel.processIntent(FeedIntent.SelectTopic(topic)) },
        modifier = modifier
    )
}

@Composable
fun FeedScreen(
    state: FeedState,
    snackbarHostState: SnackbarHostState,
    onRefreshArticles: () -> Unit,
    onClearArticles: () -> Unit,
    onOpenArticle: (String) -> Unit,
    onSelectTopic: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                subtitle = if (state.selectedTopic == null) stringResource(
                    R.string.feed_subtitle_all_topics,
                    state.articles.size
                ) else stringResource(
                    R.string.feed_subtitle_specific_topic,
                    state.selectedTopic.uppercase()
                ),
                onRefreshArticles = onRefreshArticles,
                onClearArticles = onClearArticles
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TopicChip(
                        label = stringResource(R.string.topic_all_chip),
                        selected = state.selectedTopic == null,
                        onClick = { onSelectTopic(null) }
                    )

                    state.topics.forEach { topic ->
                        TopicChip(
                            label = topic,
                            selected = state.selectedTopic == topic,
                            onClick = { onSelectTopic(topic) }
                        )
                    }
                }
            }

            if (!state.isLoading && state.articles.isEmpty()) {
                item { FeedEmptyState() }
            } else {
                itemsIndexed(state.articles) { index, article ->
                    ArticleItem(
                        article = article,
                        isFirst = index == 0,
                        onOpenArticle = onOpenArticle
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (selected) MaterialTheme.colorScheme.inverseSurface else Color.Transparent
    val contentColor =
        if (selected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Composable
private fun ArticleItem(
    article: Article,
    isFirst: Boolean,
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = article.topic.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "·",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formatRelativeTime(article.publishedAt),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!article.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(if (isFirst) 180.dp else 140.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Text(
            text = article.title,
            style = if (isFirst) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = article.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = article.sourceName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.clickable { onOpenArticle(article.url) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.read_article),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Icon(
                    painter = painterResource(R.drawable.start),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Composable
private fun formatRelativeTime(publishedAt: Long): String {
    val diffMillis = (System.currentTimeMillis() - publishedAt).coerceAtLeast(0)
    val minutes = diffMillis / 60_000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        hours < 1 -> stringResource(R.string.time_minutes_ago, minutes.toInt())
        days < 1 -> stringResource(R.string.time_hours_ago, hours.toInt())
        else -> stringResource(R.string.time_days_ago, days.toInt())
    }
}

@Composable
private fun FeedEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.feed_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.feed_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun FeedScreenLightPreview() {
    SabboTheme(darkTheme = false) {
        FeedScreen(
            state = FeedState(
                topics = listOf("Android", "Kotlin", "Compose"),
                selectedTopic = "Android",
                articles = listOf(
                    Article(
                        title = "Jetpack Compose: State Management",
                        description = "Learn how to manage state in Jetpack Compose with this comprehensive guide.",
                        imageUrl = null,
                        sourceName = "Android Developers",
                        publishedAt = System.currentTimeMillis() - 3600000,
                        url = "https://developer.android.com/jetpack/compose/state",
                        topic = "Android"
                    ),
                    Article(
                        title = "Kotlin Multiplatform in Action",
                        description = "A deep dive into building cross-platform applications with Kotlin Multiplatform.",
                        imageUrl = null,
                        sourceName = "Kotlin Blog",
                        publishedAt = System.currentTimeMillis() - 86400000,
                        url = "https://blog.jetbrains.com/kotlin/",
                        topic = "Kotlin"
                    )
                ),
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefreshArticles = {},
            onClearArticles = {},
            onOpenArticle = {},
            onSelectTopic = {}
        )
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun FeedScreenDarkPreview() {
    SabboTheme(darkTheme = true) {
        FeedScreen(
            state = FeedState(
                topics = listOf("Android", "Kotlin", "Compose"),
                selectedTopic = null,
                articles = listOf(
                    Article(
                        title = "Jetpack Compose: State Management",
                        description = "Learn how to manage state in Jetpack Compose with this comprehensive guide.",
                        imageUrl = null,
                        sourceName = "Android Developers",
                        publishedAt = System.currentTimeMillis() - 3600000,
                        url = "https://developer.android.com/jetpack/compose/state",
                        topic = "Android"
                    ),
                    Article(
                        title = "Kotlin Multiplatform in Action",
                        description = "A deep dive into building cross-platform applications with Kotlin Multiplatform.",
                        imageUrl = null,
                        sourceName = "Kotlin Blog",
                        publishedAt = System.currentTimeMillis() - 86400000,
                        url = "https://blog.jetbrains.com/kotlin/",
                        topic = "Kotlin"
                    )
                ),
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefreshArticles = {},
            onClearArticles = {},
            onOpenArticle = {},
            onSelectTopic = {}
        )
    }
}
