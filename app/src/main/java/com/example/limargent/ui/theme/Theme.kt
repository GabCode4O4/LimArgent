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
    primary = MinimalWhite, // Le texte principal et les boutons seront blancs sur fond noir
    onPrimary = MinimalBlack,
    background = MinimalBackgroundDark,
    surface = MinimalSurfaceDark,
    onBackground = MinimalWhite,
    onSurface = MinimalWhite,
    surfaceVariant = MinimalSurfaceDark,
    onSurfaceVariant = MinimalGrayText
)

private val LightColorScheme = lightColorScheme(
    primary = MinimalBlack, // Le texte principal et les boutons seront noirs sur fond blanc
    onPrimary = MinimalWhite,
    background = MinimalBackgroundLight,
    surface = MinimalSurfaceLight,
    onBackground = MinimalBlack,
    onSurface = MinimalBlack,
    surfaceVariant = MinimalSurfaceLight,
    onSurfaceVariant = MinimalGrayText
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