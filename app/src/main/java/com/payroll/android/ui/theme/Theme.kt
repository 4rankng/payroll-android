package com.payroll.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Sky500,
    onPrimary = White,
    primaryContainer = Sky100,
    onPrimaryContainer = Sky900,
    secondary = Sky600,
    onSecondary = White,
    secondaryContainer = Sky200,
    onSecondaryContainer = Sky800,
    tertiary = Sky400,
    background = Gray50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray800,
    surfaceVariant = Sky50,
    onSurfaceVariant = Gray600,
    outline = Gray300,
    error = Red500,
)

@Composable
fun PayrollAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Force light theme to match web frontend
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
