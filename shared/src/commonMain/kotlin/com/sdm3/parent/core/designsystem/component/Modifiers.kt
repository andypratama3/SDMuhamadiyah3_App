package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.Border

fun Modifier.doubleBezel(
    outerRadius: Dp = 28.dp,
    innerRadius: Dp = 24.dp,
    outerPadding: Dp = 6.dp,
    outerColor: Color = Color.Black.copy(alpha = 0.04f),
    innerColor: Color = Color.White
): Modifier = this
    .background(outerColor, RoundedCornerShape(outerRadius))
    .padding(outerPadding)
    .clip(RoundedCornerShape(innerRadius))
    .background(innerColor)
    .border(
        width = 1.dp,
        color = Border.copy(alpha = 0.5f),
        shape = RoundedCornerShape(innerRadius)
    )

fun Modifier.level1Shadow(
    borderRadius: Dp = 24.dp
): Modifier = this.shadow(
    elevation = 4.dp,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = Color.Black.copy(alpha = 0.04f),
    spotColor = Color.Black.copy(alpha = 0.08f)
)

fun Modifier.level2Shadow(
    borderRadius: Dp = 28.dp
): Modifier = this.shadow(
    elevation = 8.dp,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = Color.Black.copy(alpha = 0.06f),
    spotColor = Color.Black.copy(alpha = 0.12f)
)
