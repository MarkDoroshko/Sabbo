package com.example.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.components.AppBottomNavigation
import com.example.presentation.screen.feed.FeedRoute
import com.example.presentation.screen.topics.TopicsRoute

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val bottomInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = bottomInsets,
        bottomBar = {
            AppBottomNavigation(
                navController = navController,
                currentDestination = backStackEntry?.destination
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = Feed
        ) {
            composable<Feed> { FeedRoute() }
            composable<Topics> { TopicsRoute() }
            composable<Settings> { /* экран настроек */ }
        }
    }
}