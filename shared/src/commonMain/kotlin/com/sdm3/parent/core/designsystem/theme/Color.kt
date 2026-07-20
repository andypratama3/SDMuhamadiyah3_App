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
    primary = Color(0xFF0066CC),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF004494),
    secondary = Color(0xFF00C853),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF006400),
    tertiary = Color(0xFF7C4DFF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF4527A0),
    error = Color(0xFFFF5252),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceMuted = Color(0xFF6B7280),
    onSurfaceFaint = Color(0xFF9CA3AF),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB),
    outlineSubtle = Color(0xFFF3F4F6),
    inverseSurface = Color(0xFF1F2937),
    inverseOnSurface = Color(0xFFF9FAFB),
    inversePrimary = Color(0xFF60A5FA),
    scrim = Color(0xFF000000).copy(alpha = 0.4f),
    surfaceGlass = Color.White.copy(alpha = 0.7f),
    glassOutline = Color.White.copy(alpha = 0.3f),
    navigationActive = Color(0xFF00C853),
    success = Color(0xFF00C853),
    warning = Color(0xFFFF9800),
    danger = Color(0xFFFF5252),
    info = Color(0xFF2196F3),
    disabled = Color(0xFFD1D5DB),
)

fun productSchoolDarkColors() = ProductSchoolColors(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF001B3D),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF69F0AE),
    onSecondary = Color(0xFF003300),
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFB388FF),
    onTertiary = Color(0xFF220055),
    tertiaryContainer = Color(0xFF311B92),
    onTertiaryContainer = Color(0xFFEDE7F6),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceMuted = Color(0xFF94A3B8),
    onSurfaceFaint = Color(0xFF64748B),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    outlineSubtle = Color(0xFF334155),
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFF0066CC),
    scrim = Color(0xFF000000).copy(alpha = 0.6f),
    surfaceGlass = Color(0xFF1E293B).copy(alpha = 0.8f),
    glassOutline = Color.White.copy(alpha = 0.1f),
    navigationActive = Color(0xFF00E676),
    success = Color(0xFF00E676),
    warning = Color(0xFFFFAB40),
    danger = Color(0xFFFF5252),
    info = Color(0xFF40C4FF),
    disabled = Color(0xFF475569),
)

fun ProductSchoolColors.toMaterialColorScheme(darkTheme: Boolean) =
    if (darkTheme) toDarkMaterialColorScheme() else toLightMaterialColorScheme()

private fun ProductSchoolColors.toLightMaterialColorScheme() = androidx.compose.material3.lightColorScheme(
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

private fun ProductSchoolColors.toDarkMaterialColorScheme() = androidx.compose.material3.darkColorScheme(
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
