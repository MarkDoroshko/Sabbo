package com.example.presentation.screen.topics

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.presentation.components.AppTopBar
import com.example.presentation.mapper.toMessage
import com.example.presentation.theme.SabboTheme

@Composable
fun TopicsRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsViewModel = hiltViewModel()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val topicInput by viewModel.topicInput.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TopicsEffect.AddTopicFailed -> Toast.makeText(
                    context, effect.error.toMessage(context), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    TopicsScreen(
        topics = topics,
        topicInput = topicInput,
        onTopicInputChange = viewModel::onTopicInputChange,
        onAddTopic = viewModel::addTopic,
        onRemoveTopic = viewModel::removeTopic,
        modifier = modifier
    )
}

@Composable
fun TopicsScreen(
    topics: List<String>,
    topicInput: String,
    onTopicInputChange: (String) -> Unit,
    onAddTopic: () -> Unit,
    onRemoveTopic: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                subtitle = stringResource(R.string.topics_subtitle)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text(
                        text = stringResource(R.string.topics_screen_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = stringResource(R.string.topics_screen_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            item {
                TopicInputField(
                    value = topicInput,
                    onValueChange = onTopicInputChange,
                    onAdd = onAddTopic,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                Text(
                    text = stringResource(R.string.topics_count_label, topics.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 8.dp,
                        top = 24.dp
                    )
                )
            }

            if (topics.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.topics_empty_message),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(topics) { index, topic ->
                    TopicRow(
                        index = index,
                        topic = topic,
                        onRemove = { onRemoveTopic(topic) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.topic_input_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                }
            )

            AddTopicButton(onClick = onAdd)
        }

        HorizontalDivider(
            thickness = 1.5.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun AddTopicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.inverseSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.add_topic_button),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
private fun TopicRow(
    index: Int,
    topic: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "%02d".format(index + 1),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = topic,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.clear),
                    contentDescription = stringResource(R.string.remove_topic),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun TopicsScreenLightPreview() {
    SabboTheme(darkTheme = false) {
        TopicsScreen(
            topics = listOf("Android", "Kotlin", "Compose", "Hilt", "Room"),
            topicInput = "Test",
            onTopicInputChange = {},
            onAddTopic = {},
            onRemoveTopic = {}
        )
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun TopicsScreenDarkPreview() {
    SabboTheme(darkTheme = true) {
        TopicsScreen(
            topics = listOf("Android", "Kotlin", "Compose", "Hilt", "Room"),
            topicInput = "Jetpack",
            onTopicInputChange = {},
            onAddTopic = {},
            onRemoveTopic = {}
        )
    }
}
