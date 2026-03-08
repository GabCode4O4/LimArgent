package com.example.limargent.ui.theme

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
    primary = MinimalWhite,
    onPrimary = MinimalBlack,
    background = MinimalBackgroundDark,
    surface = MinimalSurfaceDark,
    onBackground = MinimalWhite,
    onSurface = MinimalWhite,
    surfaceVariant = MinimalSurfaceDark,
    onSurfaceVariant = MinimalGrayText,
    primaryContainer = MinimalWhite,
    onPrimaryContainer = MinimalBlack,
    secondaryContainer = MinimalBackgroundLight,
    onSecondaryContainer = MinimalBlack
)

private val LightColorScheme = lightColorScheme(
    primary = MinimalBlack,
    onPrimary = MinimalWhite,
    background = MinimalBackgroundLight,
    surface = MinimalSurfaceLight,
    onBackground = MinimalBlack,
    onSurface = MinimalBlack,
    surfaceVariant = MinimalSurfaceLight,
    primaryContainer = MinimalBlack,
    onPrimaryContainer = MinimalWhite,
    onSurfaceVariant = MinimalGrayText,
    secondaryContainer = MinimalSurfaceDark,
    onSecondaryContainer = MinimalWhite
)

@Composable
fun LimArgentTheme(
    theme: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}