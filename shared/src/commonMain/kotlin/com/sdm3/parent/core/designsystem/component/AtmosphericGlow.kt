package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sdm3.parent.core.designsystem.theme.LocalProductSchoolColors

/**
 * High-performance alternative to Canvas radial gradients.
 * Uses Box with background brush instead of expensive Canvas drawing.
 */
@Composable
fun AtmosphericGlow(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopEnd,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    alpha: Float = 0.15f
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(-1f),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = 0.8f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Even more performant simplified glow using solid colors
 */
@Composable
fun SimpleGlow(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color)
    )
}