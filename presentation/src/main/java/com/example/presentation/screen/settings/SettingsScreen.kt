package com.example.presentation.screen.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.entity.Interval
import com.example.domain.entity.Language
import com.example.domain.entity.Settings
import com.example.presentation.R
import com.example.presentation.components.AppTopBar
import com.example.presentation.theme.SabboTheme

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.updateNotificationsEnabled(it)
        }
    )

    SettingsScreen(
        settings = settings,
        onLanguageSelected = viewModel::updateLanguage,
        onIntervalSelected = viewModel::updateInterval,
        onNotificationsEnabledChanged = { enabled ->
            if (enabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else viewModel.updateNotificationsEnabled(true)
            } else viewModel.updateNotificationsEnabled(false)
        },
        onWifiOnlyChanged = viewModel::updateWifiOnly,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    settings: Settings,
    onLanguageSelected: (Language) -> Unit,
    onIntervalSelected: (Interval) -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onWifiOnlyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                subtitle = stringResource(R.string.settings_subtitle)
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
                Text(
                    text = stringResource(R.string.settings_screen_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            item {
                SettingsSection(
                    title = stringResource(R.string.settings_section_language),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Language.entries.forEachIndexed { index, language ->
                        if (index != 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }

                        LanguageOption(
                            label = language.displayName(),
                            selected = settings.language == language,
                            onClick = { onLanguageSelected(language) }
                        )
                    }
                }
            }

            item {
                SettingsSection(
                    title = stringResource(R.string.settings_section_interval),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Interval.entries.forEach { interval ->
                            IntervalChip(
                                label = interval.displayLabel(),
                                selected = settings.interval == interval,
                                onClick = { onIntervalSelected(interval) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(
                    title = stringResource(R.string.settings_section_notifications),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    SettingsToggleRow(
                        label = stringResource(R.string.push_notifications_label),
                        hint = stringResource(R.string.push_notifications_hint),
                        checked = settings.notificationsEnabled,
                        onCheckedChange = onNotificationsEnabledChanged,
                        toggleTestTag = "notifications_toggle"
                    )

                    SettingsToggleRow(
                        label = stringResource(R.string.wifi_only_label),
                        hint = stringResource(R.string.wifi_only_hint),
                        checked = settings.wifiOnly,
                        onCheckedChange = onWifiOnlyChanged,
                        toggleTestTag = "wifi_only_toggle"
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        content()
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@Composable
private fun IntervalChip(
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
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    toggleTestTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        SettingsToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(toggleTestTag)
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
private fun SettingsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surfaceVariant,
        label = "toggleTrack"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onBackground,
        label = "toggleThumb"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 21.dp else 3.dp,
        label = "toggleThumbOffset"
    )

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .offset(x = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
private fun Language.displayName(): String = when (this) {
    Language.ENGLISH -> stringResource(R.string.language_english)
    Language.RUSSIAN -> stringResource(R.string.language_russian)
    Language.FRENCH -> stringResource(R.string.language_french)
    Language.GERMAN -> stringResource(R.string.language_german)
}

@Composable
private fun Interval.displayLabel(): String = when (this) {
    Interval.MIN_15 -> stringResource(R.string.interval_15_min)
    Interval.MIN_30 -> stringResource(R.string.interval_30_min)
    Interval.HOUR_1 -> stringResource(R.string.interval_1_hour)
    Interval.HOUR_2 -> stringResource(R.string.interval_2_hours)
    Interval.HOUR_4 -> stringResource(R.string.interval_4_hours)
    Interval.HOUR_8 -> stringResource(R.string.interval_8_hours)
    Interval.HOUR_24 -> stringResource(R.string.interval_24_hours)
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SabboTheme(darkTheme = false) {
        SettingsScreen(
            settings = Settings(
                language = Language.ENGLISH,
                interval = Interval.HOUR_1,
                notificationsEnabled = true,
                wifiOnly = false
            ),
            onLanguageSelected = {},
            onIntervalSelected = {},
            onNotificationsEnabledChanged = {},
            onWifiOnlyChanged = {}
        )
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun SettingsScreenDarkPreview() {
    SabboTheme(darkTheme = true) {
        SettingsScreen(
            settings = Settings(
                language = Language.ENGLISH,
                interval = Interval.HOUR_1,
                notificationsEnabled = true,
                wifiOnly = false
            ),
            onLanguageSelected = {},
            onIntervalSelected = {},
            onNotificationsEnabledChanged = {},
            onWifiOnlyChanged = {}
        )
    }
}
