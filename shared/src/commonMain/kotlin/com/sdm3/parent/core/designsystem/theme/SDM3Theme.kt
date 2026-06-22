package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SDM3LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    error = Error,
    onError = OnError,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceVariant = SurfaceVariant,
    outline = Border,
    outlineVariant = Divider
)

private val SDM3DarkColorScheme = darkColorScheme(
    primary = Primary.copy(alpha = 0.85f),
    onPrimary = OnPrimary,
    primaryContainer = OnPrimaryContainer,
    onPrimaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    error = Error,
    onError = OnError,
    background = OnSurface,
    onBackground = Surface,
    surface = OnSurface,
    onSurface = Surface,
    onSurfaceVariant = TextTertiary,
    surfaceVariant = OnSurface.copy(alpha = 0.05f),
    outline = OnSurface.copy(alpha = 0.2f),
    outlineVariant = OnSurface.copy(alpha = 0.1f)
)

@Composable
fun SDM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SDM3DarkColorScheme else SDM3LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SDM3Typography,
        shapes = SDM3Shapes,
        content = content
    )
}
