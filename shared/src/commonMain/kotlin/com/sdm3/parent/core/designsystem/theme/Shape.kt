package com.sdm3.parent.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// EduOcto Shape System
val SDM3Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // sm
    small = RoundedCornerShape(8.dp),       // DEFAULT
    medium = RoundedCornerShape(12.dp),      // md
    large = RoundedCornerShape(16.dp),       // lg (1rem) - Primary container radius
    extraLarge = RoundedCornerShape(24.dp)   // xl
)

// Convenience aliases
val SmallShape = SDM3Shapes.small
val MediumShape = SDM3Shapes.medium
val LargeShape = SDM3Shapes.large

// EduOcto Specific Components
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(16.dp) // Interactive elements use same 16px radius
val InputShape = RoundedCornerShape(16.dp)
val ChipShape = RoundedCornerShape(999.dp) // Pill shape for metadata
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val DialogShape = RoundedCornerShape(24.dp)
val NavBarShape = RoundedCornerShape(999.dp) // Quick Action Bar icons or floating nav
val OtpBoxShape = RoundedCornerShape(12.dp)
