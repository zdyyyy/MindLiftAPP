package com.example.music.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)
private val Green = Color(0xFF91D394)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = Black,
    surface = White,
    onSurface = Black,
    background = White,
    onBackground = Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    onPrimary = White,
    surface = Black,
    onSurface = White,
    background = Black,
    onBackground = White
)

@Composable
fun MusicplayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}