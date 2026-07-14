package com.example.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.theme.SabboTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    subtitle: String,
    onRefreshArticles: () -> Unit,
    onClearArticles: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Column {
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            Row {
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = onRefreshArticles,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(R.drawable.refresh),
                        contentDescription = stringResource(R.string.refresh_articles)
                    )
                }

                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = onClearArticles,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(R.drawable.delete),
                        contentDescription = stringResource(R.string.clear_articles)
                    )
                }
            }
        }
    )
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun AppTopBarLightPreview() {
    SabboTheme(darkTheme = false) {
        AppTopBar(
            subtitle = stringResource(R.string.feed_subtitle_all_topics, 8),
            onRefreshArticles = {},
            onClearArticles = {}
        )
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun AppTopBarDarkPreview() {
    SabboTheme(darkTheme = true) {
        AppTopBar(
            subtitle = stringResource(R.string.feed_subtitle_all_topics, 8),
            onRefreshArticles = {},
            onClearArticles = {}
        )
    }
}