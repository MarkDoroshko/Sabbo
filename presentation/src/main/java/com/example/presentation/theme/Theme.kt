package com.example.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    // Фон экрана целиком (Лента, Темы, Настройки)
    background = Black100,
    // Карточка статьи (если понадобится фон-заливка), поверхность иконки приложения
    surface = Black200,

    // Основной текст: заголовки, wordmark «Саббо», значения переключателей, активный таб
    onBackground = White400,
    onSurface = White400,

    // Трек нижней навигации (пилюля-контейнер), плейсхолдер картинки статьи
    surfaceVariant = Black300,
    // Второстепенный текст: excerpt статьи, подписи, source · readTime, hint-тексты настроек
    onSurfaceVariant = White400.copy(alpha = 0.55f),

    // Разделители карточек, границы неактивных чипов и radio
    outline = White400.copy(alpha = 0.12f),
    // Резервный более заметный divider (не используется по умолчанию, доступен для акцентных секций)
    outlineVariant = White400.copy(alpha = 0.18f),

    // Тег темы над заголовком статьи (KTOR, ANDROID…), акцентная точка рядом с логотипом
    primary = Yellow100,

    // Фон активного чипа / активного таба / кнопки «Добавить» (тёмная заливка на светлом фоне)
    inverseSurface = White400,
    // Текст на активном чипе / табе / кнопке «Добавить»
    inverseOnSurface = Black100
)

private val LightColorScheme = lightColorScheme(
    // Фон экрана целиком (Лента, Темы, Настройки)
    background = White200,
    // Карточка статьи (если понадобится фон-заливка), поверхность иконки приложения
    surface = White100,

    // Основной текст: заголовки, wordmark «Саббо», значения переключателей, активный таб
    onBackground = Black100,
    onSurface = Black100,

    // Трек нижней навигации (пилюля-контейнер), плейсхолдер картинки статьи
    surfaceVariant = White300,
    // Второстепенный текст: excerpt статьи, подписи, source · readTime, hint-тексты настроек
    onSurfaceVariant = Black100.copy(alpha = 0.55f),

    // Разделители карточек, границы неактивных чипов и radio
    outline = Black100.copy(alpha = 0.12f),
    // Резервный более заметный divider (не используется по умолчанию, доступен для акцентных секций)
    outlineVariant = Black100.copy(alpha = 0.15f),

    // Тег темы над заголовком статьи (KTOR, ANDROID…), акцентная точка рядом с логотипом
    primary = Red100,

    // Фон активного чипа / активного таба / кнопки «Добавить» (тёмная заливка на светлом фоне)
    inverseSurface = Black100,
    // Текст на активном чипе / табе / кнопке «Добавить»
    inverseOnSurface = White200
)

@Composable
fun SabboTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}