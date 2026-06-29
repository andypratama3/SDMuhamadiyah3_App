package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ProductSchoolShapes(
    val radiusSmall: Dp = 8.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusPill: Dp = 999.dp,
)

val productSchoolComponentShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Backward-compatible vals
val SDM3Shapes = productSchoolComponentShapes
val SmallShape = RoundedCornerShape(8.dp)
val MediumShape = RoundedCornerShape(12.dp)
val LargeShape = RoundedCornerShape(16.dp)

val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(16.dp)
val InputShape = RoundedCornerShape(16.dp)
val ChipShape = RoundedCornerShape(999.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val DialogShape = RoundedCornerShape(24.dp)
val NavBarShape = RoundedCornerShape(999.dp)
val OtpBoxShape = RoundedCornerShape(12.dp)
