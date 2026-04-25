package com.mindbridge.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Violet500,
    onSecondary = Color.White,
    secondaryContainer = Violet300,
    onSecondaryContainer = Violet600,
    tertiary = Teal400,
    background = Warm50,
    onBackground = Warm900,
    surface = Color.White,
    onSurface = Warm900,
    surfaceVariant = Warm100,
    onSurfaceVariant = Warm600,
    outline = Warm300,
    error = Error,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal400,
    onPrimary = Teal900,
    primaryContainer = Teal800,
    onPrimaryContainer = Teal100,
    secondary = Violet400,
    onSecondary = Violet600,
    secondaryContainer = Violet600,
    onSecondaryContainer = Violet300,
    tertiary = Teal300,
    background = Warm900,
    onBackground = Warm100,
    surface = Warm800,
    onSurface = Warm100,
    surfaceVariant = Warm700,
    onSurfaceVariant = Warm300,
    outline = Warm600,
    error = Color(0xFFFF6B6B),
    onError = Color.White
)

@Composable
fun MindBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MindBridgeTypography,
        content = content
    )
}
