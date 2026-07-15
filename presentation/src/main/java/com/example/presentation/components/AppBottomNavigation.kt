package com.example.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.R
import com.example.presentation.navigation.Feed
import com.example.presentation.navigation.Settings
import com.example.presentation.navigation.Topics
import com.example.presentation.theme.SabboTheme

data class BottomTab(
    val route: Any,
    val labelRes: Int
)

val bottomTabs: List<BottomTab> = listOf(
    BottomTab(route = Feed, labelRes = R.string.feed_tab),
    BottomTab(route = Topics, labelRes = R.string.topics_tab),
    BottomTab(route = Settings, labelRes = R.string.settings_tab)
)

@Composable
fun AppBottomNavigation(
    navController: NavController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(50)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomTabs.forEach { tab ->
                val selected = currentDestination?.hasRoute(tab.route::class) ?: false

                val backgroundColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.inverseSurface else Color.Transparent,
                    label = "pillBackground"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (selected)
                        MaterialTheme.colorScheme.inverseOnSurface
                    else
                        MaterialTheme.colorScheme.onSurface,
                    label = "pillContent"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(tab.labelRes),
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBottomNavigationPreviewContent() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Feed) {
        composable<Feed> {}
        composable<Topics> {}
        composable<Settings> {}
    }
    val currentDestination = navController.currentDestination

    AppBottomNavigation(
        navController = navController,
        currentDestination = currentDestination
    )
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun AppBottomNavigationLightPreview() {
    SabboTheme(darkTheme = false) {
        AppBottomNavigationPreviewContent()
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun AppBottomNavigationDarkPreview() {
    SabboTheme(darkTheme = true) {
        AppBottomNavigationPreviewContent()
    }
}