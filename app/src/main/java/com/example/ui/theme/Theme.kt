package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FlipWhite,
    onPrimary = FlipBlack,
    primaryContainer = FlipCardDark,
    onPrimaryContainer = FlipWhite,
    secondary = FlipTextMuted,
    onSecondary = FlipWhite,
    background = FlipBackgroundDark,
    onBackground = FlipWhite,
    surface = FlipCardDark,
    onSurface = FlipWhite,
    surfaceVariant = FlipCardDarkBottom,
    onSurfaceVariant = FlipTextMuted,
    outline = FlipCardBorderDark,
    error = FlipAccentRed,
    onError = FlipWhite
)

private val LightColorScheme = lightColorScheme(
    primary = FlipTextDark,
    onPrimary = FlipWhite,
    primaryContainer = FlipCardLightBottom,
    onPrimaryContainer = FlipTextDark,
    secondary = FlipTextMuted,
    onSecondary = FlipTextDark,
    background = FlipBackgroundLight,
    onBackground = FlipTextDark,
    surface = FlipCardLight,
    onSurface = FlipTextDark,
    surfaceVariant = FlipCardLightBottom,
    onSurfaceVariant = FlipTextMuted,
    outline = FlipCardBorderLight,
    error = FlipAccentRed,
    onError = FlipWhite
)

@Composable
fun FlipClockTheme(
    darkTheme: Boolean = true, // Default to stunning dark monochrome for flip clock
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
