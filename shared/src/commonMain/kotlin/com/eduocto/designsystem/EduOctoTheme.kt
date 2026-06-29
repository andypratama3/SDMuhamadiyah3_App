package com.eduocto.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.sp

private fun eduOctoTypographyDefault() = EduOctoTypography(
    displayLarge = androidx.compose.ui.text.TextStyle(fontSize = 34.sp, lineHeight = 41.sp),
    displayMedium = androidx.compose.ui.text.TextStyle(fontSize = 28.sp, lineHeight = 35.sp),
    titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    metadata = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
)

val LocalEduOctoColors = staticCompositionLocalOf { eduOctoLightColors() }
val LocalEduOctoTypography = staticCompositionLocalOf { eduOctoTypographyDefault() }
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) eduOctoDarkColors() else eduOctoLightColors()
    val typography = eduOctoTypography()
    val materialShapes = Shapes(
        extraSmall = RoundedCornerShape(EduOctoTheme.shapes.radiusSmall),
        small = RoundedCornerShape(EduOctoTheme.shapes.radiusSmall),
        medium = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium),
        large = RoundedCornerShape(EduOctoTheme.shapes.radiusLarge),
        extraLarge = RoundedCornerShape(EduOctoTheme.shapes.radiusLarge),
    )
    val spacing = EduOctoSpacing()
    val shapes = EduOctoShapes()

    CompositionLocalProvider(
        LocalEduOctoColors provides colors,
        LocalEduOctoTypography provides typography,
        LocalEduOctoSpacing provides spacing,
        LocalEduOctoShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(darkTheme),
            typography = typography.toMaterialTypography(),
            shapes = materialShapes,
            content = content,
        )
    }
}
