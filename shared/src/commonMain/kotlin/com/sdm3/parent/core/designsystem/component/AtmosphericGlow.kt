package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

/**
 * High-performance atmospheric glow background using radial gradient.
 * Uses graphicsLayer for hardware-accelerated rendering.
 *
 * In dark mode, alpha is automatically capped at 0.15 to remain subtle.
 * Accepts configurable alignment and color for flexible placement.
 */
@Composable
fun AtmosphericGlow(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopEnd,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    alpha: Float = if (isSystemInDarkTheme()) 0.10f else 0.15f,
    animate: Boolean = false
) {
    val effectiveAlpha = if (isSystemInDarkTheme()) alpha.coerceAtMost(0.15f) else alpha
    
    val animatedAlpha by if (animate) {
        val transition = rememberInfiniteTransition(label = "glowPulse")
        transition.animateFloat(
            initialValue = effectiveAlpha * 0.6f,
            targetValue = effectiveAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
    } else {
        androidx.compose.runtime.rememberUpdatedState(effectiveAlpha)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
            .zIndex(-1f),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = 0.8f)
                .graphicsLayer { this.alpha = animatedAlpha }
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * A localized glow intended to be used behind icons or small components.
 */
@Composable
fun LocalizedGlow(
    modifier: Modifier = Modifier,
    color: Color,
    alpha: Float = 0.2f,
    animate: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "localGlow")
    val animatedAlpha by transition.animateFloat(
        initialValue = alpha * 0.5f,
        targetValue = alpha,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = animatedAlpha
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}
