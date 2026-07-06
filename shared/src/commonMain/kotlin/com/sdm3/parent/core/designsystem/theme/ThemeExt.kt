package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Teks/ikon di atas permukaan primary (hero card, header gelap, splash). */
@Composable
fun heroContentColor(): Color = MaterialTheme.colorScheme.onPrimary

/** Gradien latar splash — navy→biru muda (light) atau navy→hitam (dark). */
@Composable
fun splashBackgroundBrush(): Brush {
    val colorScheme = MaterialTheme.colorScheme
    return Brush.verticalGradient(
        colors = if (isSystemInDarkTheme()) {
            listOf(colorScheme.primary, colorScheme.background)
        } else {
            listOf(colorScheme.primary, colorScheme.primaryContainer.copy(alpha = 0.85f))
        },
    )
}

/** Overlay gelap di atas gambar atau konten (avatar upload, foto, dll). */
@Composable
fun overlayScrimColor(alpha: Float = 0.55f): Color =
    MaterialTheme.colorScheme.scrim.copy(alpha = alpha)

/** Isian glassmorphism (input, chip transparan). */
@Composable
fun glassSurfaceColor(): Color = ProductSchoolTheme.colors.surfaceGlass

/** Garis tepi glassmorphism. */
@Composable
fun glassBorderColor(): Color = ProductSchoolTheme.colors.glassOutline

@Composable
fun statusSuccessColor(): Color = ProductSchoolTheme.colors.success

@Composable
fun statusWarningColor(): Color = ProductSchoolTheme.colors.warning

@Composable
fun statusDangerColor(): Color = ProductSchoolTheme.colors.danger

@Composable
fun statusInfoColor(): Color = ProductSchoolTheme.colors.info
