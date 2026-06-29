package com.eduocto.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class EduOctoColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val onSurfaceFaint: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineSubtle: Color,
    val surfaceGlass: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val info: Color,
)

fun eduOctoLightColors() = EduOctoColors(
    primary = Color(0xFF001B3D),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFD4AF37),
    onSecondary = Color(0xFF1A1300),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF0F1722),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F1722),
    onSurfaceMuted = Color(0xFF525B66),
    onSurfaceFaint = Color(0xFF8B93A0),
    surfaceVariant = Color(0xFFDBE4ED),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFFE2E6EB),
    outlineSubtle = Color(0xFFEFF1F4),
    surfaceGlass = Color(0x8CFFFFFF),
    success = Color(0xFF1E8E5A),
    warning = Color(0xFFC98A1D),
    danger = Color(0xFFC13A3A),
    info = Color(0xFF2D6CDF),
)

fun eduOctoDarkColors() = EduOctoColors(
    primary = Color(0xFFB1C7F2),
    onPrimary = Color(0xFF001B3D),
    secondary = Color(0xFFE9C349),
    onSecondary = Color(0xFF241A00),
    background = Color(0xFF0F1722),
    onBackground = Color(0xFFE6EFF8),
    surface = Color(0xFF141D23),
    onSurface = Color(0xFFE6EFF8),
    onSurfaceMuted = Color(0xFFC4C6CF),
    onSurfaceFaint = Color(0xFF8E9199),
    surfaceVariant = Color(0xFF44474E),
    onSurfaceVariant = Color(0xFFC4C6CF),
    outline = Color(0xFF8E9199),
    outlineSubtle = Color(0xFF44474E),
    surfaceGlass = Color(0x1A0F1722),
    success = Color(0xFF4CAF50),
    warning = Color(0xFFFFCA28),
    danger = Color(0xFFEF5350),
    info = Color(0xFF64B5F6),
)

private fun lightColorSchemeFrom(colors: EduOctoColors): ColorScheme = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    outline = colors.outline,
    error = colors.danger,
    onError = Color.White,
)

private fun darkColorSchemeFrom(colors: EduOctoColors): ColorScheme = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    outline = colors.outline,
    error = colors.danger,
    onError = Color.White,
)

fun EduOctoColors.toMaterialColorScheme(isDark: Boolean = false): ColorScheme =
    if (isDark) darkColorSchemeFrom(this) else lightColorSchemeFrom(this)
