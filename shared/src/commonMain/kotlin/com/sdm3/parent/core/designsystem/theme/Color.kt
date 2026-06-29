package com.sdm3.parent.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ProductSchoolColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
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
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val scrim: Color,
    val surfaceGlass: Color,
    val glassOutline: Color,
    val navigationActive: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val info: Color,
    val disabled: Color,
)

fun productSchoolLightColors() = ProductSchoolColors(
    primary = Color(0xFF001B3D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001B3D),
    secondary = Color(0xFFD4AF37),
    onSecondary = Color(0xFF1A1300),
    secondaryContainer = Color(0xFFFED65B),
    onSecondaryContainer = Color(0xFF745C00),
    tertiary = Color(0xFF191C1D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE1E3E4),
    onTertiaryContainer = Color(0xFF454748),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
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
    inverseSurface = Color(0xFF293138),
    inverseOnSurface = Color(0xFFE9F2FB),
    inversePrimary = Color(0xFFB1C7F2),
    scrim = Color(0xFF000000).copy(alpha = 0.32f),
    surfaceGlass = Color.White.copy(alpha = 0.55f),
    glassOutline = Color.White.copy(alpha = 0.2f),
    navigationActive = Color(0xFF2E7D32),
    success = Color(0xFF1E8E5A),
    warning = Color(0xFFC98A1D),
    danger = Color(0xFFC13A3A),
    info = Color(0xFF2D6CDF),
    disabled = Color(0xFFC4C6CF),
)

fun productSchoolDarkColors() = ProductSchoolColors(
    primary = Color(0xFFB1C7F2),
    onPrimary = Color(0xFF001B3D),
    primaryContainer = Color(0xFF001B3D),
    onPrimaryContainer = Color(0xFFB1C7F2),
    secondary = Color(0xFFE9C349),
    onSecondary = Color(0xFF241A00),
    secondaryContainer = Color(0xFF574500),
    onSecondaryContainer = Color(0xFFFFE088),
    tertiary = Color(0xFFC5C7C8),
    onTertiary = Color(0xFF191C1D),
    tertiaryContainer = Color(0xFF454748),
    onTertiaryContainer = Color(0xFFE1E3E4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF001B3D),
    onBackground = Color(0xFFE6EFF8),
    surface = Color(0xFF001B3D),
    onSurface = Color(0xFFE6EFF8),
    onSurfaceMuted = Color(0xFFC4C6CF),
    onSurfaceFaint = Color(0xFF8E9199),
    surfaceVariant = Color(0xFF44474E),
    onSurfaceVariant = Color(0xFFC4C6CF),
    outline = Color(0xFF8E9199),
    outlineSubtle = Color(0xFF44474E),
    inverseSurface = Color(0xFFF7F8FA),
    inverseOnSurface = Color(0xFF0F1722),
    inversePrimary = Color(0xFF001B3D),
    scrim = Color(0xFF000000),
    surfaceGlass = Color(0xFF001B3D).copy(alpha = 0.7f),
    glassOutline = Color.White.copy(alpha = 0.1f),
    navigationActive = Color(0xFF66BB6A),
    success = Color(0xFF4CAF50),
    warning = Color(0xFFFFCA28),
    danger = Color(0xFFEF5350),
    info = Color(0xFF64B5F6),
    disabled = Color(0xFF616161),
)

fun ProductSchoolColors.toMaterialColorScheme() = androidx.compose.material3.lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    scrim = scrim,
)

// Backward-compatible color vals (existing code still compiles)
val Primary = Color(0xFF001B3D)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD6E3FF)
val OnPrimaryContainer = Color(0xFF001B3D)
val Secondary = Color(0xFFD4AF37)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFFED65B)
val OnSecondaryContainer = Color(0xFF745C00)
val Tertiary = Color(0xFF191C1D)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFE1E3E4)
val OnTertiaryContainer = Color(0xFF454748)
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)
val Background = Color(0xFFF6FAFF)
val OnBackground = Color(0xFF141D23)
val Surface = Color(0xFFF6FAFF)
val OnSurface = Color(0xFF141D23)
val SurfaceVariant = Color(0xFFDBE4ED)
val OnSurfaceVariant = Color(0xFF44474E)
val Outline = Color(0xFF74777F)
val OutlineVariant = Color(0xFFC4C6CF)
val Divider = Color(0xFFC4C6CF)
val TextPrimary = OnBackground
val TextSecondary = OnSurfaceVariant
val TextTertiary = Color(0xFF74777F)
val Disabled = Color(0xFFC4C6CF)
val Border = Outline
val StatusSuccess = Color(0xFF16A34A)
val StatusWarning = Color(0xFFF59E0B)
val StatusDanger = Color(0xFFBA1A1A)
val StatusInfo = Color(0xFF495F84)
val Scrim = Color(0xFF000000).copy(alpha = 0.32f)
val GlassSurface = Color(0xFFFFFFFF).copy(alpha = 0.4f)
val GlassOutline = Color(0xFFFFFFFF).copy(alpha = 0.2f)
