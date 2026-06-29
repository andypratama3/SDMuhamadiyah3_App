@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.eduocto.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.FontResource
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.inter_bold
import sdmuhammadiyah3samarinda.shared.generated.resources.inter_medium
import sdmuhammadiyah3samarinda.shared.generated.resources.inter_regular
import sdmuhammadiyah3samarinda.shared.generated.resources.inter_semibold

private val eduOctoInterFonts: List<Pair<FontResource, FontWeight>> = listOf(
    Res.font.inter_regular to FontWeight.Normal,
    Res.font.inter_medium to FontWeight.Medium,
    Res.font.inter_semibold to FontWeight.SemiBold,
    Res.font.inter_bold to FontWeight.Bold,
)

internal val eduOctoInterFontFamily: FontFamily
    @Composable get() = FontFamily(
        eduOctoInterFonts.map { (resource, weight) ->
            org.jetbrains.compose.resources.Font(
                resource = resource,
                weight = weight,
            )
        }
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

@Composable
fun eduOctoTypography(): EduOctoTypography {
    val inter = eduOctoInterFontFamily
    return EduOctoTypography(
        displayLarge = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Bold,
            fontSize = 34.sp, lineHeight = 41.sp, letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp, lineHeight = 35.sp, letterSpacing = (-0.2).sp,
        ),
        titleLarge = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp, lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp, lineHeight = 24.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Normal,
            fontSize = 16.sp, lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Normal,
            fontSize = 14.sp, lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Normal,
            fontSize = 13.sp, lineHeight = 18.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Medium,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp,
        ),
        metadata = TextStyle(
            fontFamily = inter, fontWeight = FontWeight.Normal,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp,
        ),
    )
}

fun EduOctoTypography.toMaterialTypography() = Typography(
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
