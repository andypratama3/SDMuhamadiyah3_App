package com.eduocto.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class EduOctoShapes(
    val radiusSmall: Dp = 8.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusPill: Dp = 999.dp,
)
