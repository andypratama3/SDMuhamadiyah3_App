# 03 — Design System

## Purpose

This file defines the design system as a layered set of tokens and rules that every component and
screen in EduOcto must consume — never hardcode raw values (hex colors, dp values, font sizes)
directly in a Composable. Always reference a token.

## Token Layers

```
Layer 1 — Primitive tokens   (raw values: hex colors, dp scale, font sizes)
Layer 2 — Semantic tokens    (role-based: "primary", "surface", "onSurface", "danger")
Layer 3 — Component tokens   (component-specific: "ButtonPrimaryBackground", "CardElevation")
```

Composables consume **Layer 3** tokens. Layer 3 tokens reference Layer 2. Layer 2 references Layer 1.
This indirection is what allows theming (e.g. a future dark mode, or white-label for multi-school
SaaS) without touching component code.

## Kotlin Representation

```kotlin
// shared/src/commonMain/kotlin/com/eduocto/designsystem/EduOctoTheme.kt

@Immutable
data class EduOctoColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceGlass: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val outline: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val info: Color,
)

@Immutable
data class EduOctoTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelSmall: TextStyle,
    val metadata: TextStyle,
)

@Immutable
data class EduOctoSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

@Immutable
data class EduOctoShapes(
    val radiusSmall: Dp = 8.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusPill: Dp = 999.dp,
)

val LocalEduOctoColors = staticCompositionLocalOf<EduOctoColors> {
    error("No EduOctoColors provided")
}
val LocalEduOctoTypography = staticCompositionLocalOf<EduOctoTypography> {
    error("No EduOctoTypography provided")
}
val LocalEduOctoSpacing = staticCompositionLocalOf { EduOctoSpacing() }
val LocalEduOctoShapes = staticCompositionLocalOf { EduOctoShapes() }

object EduOctoTheme {
    val colors: EduOctoColors
        @Composable get() = LocalEduOctoColors.current
    val typography: EduOctoTypography
        @Composable get() = LocalEduOctoTypography.current
    val spacing: EduOctoSpacing
        @Composable get() = LocalEduOctoSpacing.current
    val shapes: EduOctoShapes
        @Composable get() = LocalEduOctoShapes.current
}

@Composable
fun EduOctoTheme(
    colors: EduOctoColors = eduOctoLightColors(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalEduOctoColors provides colors,
        LocalEduOctoTypography provides eduOctoTypography(),
        LocalEduOctoSpacing provides EduOctoSpacing(),
        LocalEduOctoShapes provides EduOctoShapes(),
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            content = content,
        )
    }
}
```

**Rule:** every new Composable in `20-COMPONENT-LIBRARY.md` reads colors/typography/spacing through
`EduOctoTheme.colors`, `EduOctoTheme.typography`, `EduOctoTheme.spacing` — never `Color(0xFF...)`
inline, never a magic `Dp` literal.

## Principles

1. **Tokens before pixels.** No raw hex, no raw dp, no raw sp outside the token files themselves.
2. **Material 3 as substrate, not skin.** EduOcto rides on Material 3's a11y and platform behavior,
   but every visible color/shape/type value is overridden by EduOcto tokens.
3. **One source per concept.** Spacing lives only in `06-SPACING-SYSTEM.md` + `EduOctoSpacing`.
   Color lives only in `04-COLOR-SYSTEM.md` + `EduOctoColors`. No duplicate definitions.
4. **Cross-platform parity.** Tokens are defined once in `commonMain` and apply identically on
   Android, iOS, and Desktop. Platform-specific adjustments (e.g. iOS uses SF-style spring curves)
   are documented exceptions, not silent divergence.
