package com.example.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.presentation.R

val InstrumentSerif = FontFamily(
    Font(R.font.instrument_serif_regular, style = FontStyle.Normal),
    Font(R.font.instrument_serif_italic, style = FontStyle.Italic)
)

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.W400),
    Font(R.font.manrope_medium, FontWeight.W500),
    Font(R.font.manrope_semibold, FontWeight.W600),
    Font(R.font.manrope_bold, FontWeight.W700)
)

val Typography = Typography(
    // wordmark "Саббо"
    displayLarge = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp, lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    // hero title (первая статья ленты)
    headlineLarge = TextStyle(
        fontFamily = InstrumentSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp, lineHeight = 35.sp,
        letterSpacing = (-0.5).sp
    ),
    // заголовок обычной статьи
    headlineMedium = TextStyle(
        fontFamily = InstrumentSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 23.sp,
        lineHeight = 27.sp,
        letterSpacing = (-0.5).sp
    ),
    // заголовки экранов: "Подписки", "Настройки"
    titleLarge = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    // подписи секций настроек ("Интервал обновления")
    titleMedium = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    // числовой префикс списка тем ("01", "02"…)
    titleSmall = TextStyle(
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    // текст инпута добавления темы, значения radio (Русский/English/中文)
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // excerpt статьи
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    // hint-тексты под настройками ("Сообщать о новых статьях")
    bodySmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    // текст на кнопках/чипах/табах ("Добавить", "Читать", чипы тем, bottom nav)
    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    // капшены/мета в верхнем регистре ("Сегодня · 8 материалов", source · readTime)
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    )
)