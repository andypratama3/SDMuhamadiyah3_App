package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalProductSchoolColors = staticCompositionLocalOf { productSchoolLightColors() }
val LocalProductSchoolTypography = staticCompositionLocalOf { baseTypography }
val LocalProductSchoolSpacing = staticCompositionLocalOf { ProductSchoolSpacing() }
val LocalProductSchoolShapes = staticCompositionLocalOf { ProductSchoolShapes() }
val LocalProductSchoolElevation = staticCompositionLocalOf { ProductSchoolElevation() }

object ProductSchoolTheme {
    val colors: ProductSchoolColors
        @Composable get() = LocalProductSchoolColors.current
    val typography: ProductSchoolTypography
        @Composable get() = LocalProductSchoolTypography.current
    val spacing: ProductSchoolSpacing
        @Composable get() = LocalProductSchoolSpacing.current
    val shapes: ProductSchoolShapes
        @Composable get() = LocalProductSchoolShapes.current
    val elevation: ProductSchoolElevation
        @Composable get() = LocalProductSchoolElevation.current
}

@Composable
fun ProductSchoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) productSchoolDarkColors() else productSchoolLightColors()
    val typography = productSchoolTypography()

    CompositionLocalProvider(
        LocalProductSchoolColors provides colors,
        LocalProductSchoolTypography provides typography,
        LocalProductSchoolSpacing provides ProductSchoolSpacing(),
        LocalProductSchoolShapes provides ProductSchoolShapes(),
        LocalProductSchoolElevation provides ProductSchoolElevation(),
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = productSchoolComponentShapes,
            content = content,
        )
    }
}

@Composable
fun SDM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = ProductSchoolTheme(darkTheme = darkTheme, content = content)
