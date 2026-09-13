package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.sdm3.parent.core.designsystem.theme.Sdm3Motion

/**
 * Adds a press scale animation to interactive elements.
 * When pressed, scales down to 0.96f for premium tactile feedback.
 */
fun Modifier.pressEffect(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(
            durationMillis = Sdm3Motion.durationFast,
            easing = Sdm3Motion.easing
        ),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
