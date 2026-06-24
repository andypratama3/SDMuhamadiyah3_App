package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SDM3Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Convenience aliases matching Material3 sizing vocabulary
val SmallShape = SDM3Shapes.small      // 12.dp
val MediumShape = SDM3Shapes.medium    // 16.dp
val LargeShape = SDM3Shapes.large      // 24.dp

// Existing custom shapes
val CardShape = RoundedCornerShape(24.dp)
val ButtonShape = RoundedCornerShape(18.dp)
val InputShape = RoundedCornerShape(18.dp)
val ChipShape = RoundedCornerShape(999.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val DialogShape = RoundedCornerShape(28.dp)
val NavBarShape = RoundedCornerShape(28.dp)
val OtpBoxShape = RoundedCornerShape(16.dp)