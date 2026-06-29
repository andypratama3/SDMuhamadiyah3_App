package com.sdm3.parent.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ProductSchoolSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

data class ProductSchoolElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 4.dp,
    val level3: Dp = 8.dp,
    val level4: Dp = 16.dp,
)

object Spacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val xxxxl = 48.dp
    val xxxxxl = 64.dp
}

object Elevation {
    val level0 = 0.dp
    val level1 = 4.dp
    val level2 = 8.dp
    val level3 = 12.dp
    val shadowY1 = 4.dp
    val shadowBlur1 = 18.dp
    val shadowOpacity1 = 0.08f
    val shadowY2 = 8.dp
    val shadowBlur2 = 28.dp
    val shadowOpacity2 = 0.12f
    val shadowY3 = 12.dp
    val shadowBlur3 = 36.dp
    val shadowOpacity3 = 0.16f
}
