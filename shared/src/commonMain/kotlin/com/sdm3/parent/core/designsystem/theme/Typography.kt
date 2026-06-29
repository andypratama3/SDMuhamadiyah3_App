package com.sdm3.parent.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class ProductSchoolTypography(
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

internal val baseTypography = ProductSchoolTypography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 41.sp, letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 35.sp, letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp,
    ),
    metadata = TextStyle(
        fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp,
    ),
)

@Composable
fun productSchoolTypography(): ProductSchoolTypography {
    val inter = interFontFamily
    return baseTypography.copy(
        displayLarge = baseTypography.displayLarge.copy(fontFamily = inter),
        displayMedium = baseTypography.displayMedium.copy(fontFamily = inter),
        titleLarge = baseTypography.titleLarge.copy(fontFamily = inter),
        titleMedium = baseTypography.titleMedium.copy(fontFamily = inter),
        bodyLarge = baseTypography.bodyLarge.copy(fontFamily = inter),
        bodyMedium = baseTypography.bodyMedium.copy(fontFamily = inter),
        bodySmall = baseTypography.bodySmall.copy(fontFamily = inter),
        labelLarge = baseTypography.labelLarge.copy(fontFamily = inter),
        labelSmall = baseTypography.labelSmall.copy(fontFamily = inter),
        metadata = baseTypography.metadata.copy(fontFamily = inter),
    )
}

fun ProductSchoolTypography.toMaterialTypography() = Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelLarge,
    labelSmall = labelSmall,
)

val SDM3Typography = Typography(
    displayLarge = baseTypography.displayLarge,
    displayMedium = baseTypography.displayMedium,
    titleLarge = baseTypography.titleLarge,
    titleMedium = baseTypography.titleMedium,
    bodyLarge = baseTypography.bodyLarge,
    bodyMedium = baseTypography.bodyMedium,
    bodySmall = baseTypography.bodySmall,
    labelLarge = baseTypography.labelLarge,
    labelSmall = baseTypography.labelSmall,
)
