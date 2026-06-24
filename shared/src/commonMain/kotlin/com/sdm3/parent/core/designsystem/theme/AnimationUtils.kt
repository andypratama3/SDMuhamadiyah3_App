package com.sdm3.parent.core.designsystem.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalReducedMotion = staticCompositionLocalOf { false }

object MotionDuration {
    const val fast = 150
    const val normal = 300
    const val slow = 600
    const val enter = 500
    const val exit = 300
    const val page = 700
}

@Composable
fun MotionAnim(
    visible: Boolean,
    enter: EnterTransition = fadeIn(tween(MotionDuration.enter)) + slideInVertically(tween(MotionDuration.enter)),
    exit: ExitTransition = fadeOut(tween(MotionDuration.exit)) + slideOutVertically(tween(MotionDuration.exit)),
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val reducedMotion = LocalReducedMotion.current
    AnimatedVisibility(
        visible = visible,
        enter = if (reducedMotion) fadeIn(tween(0)) else enter,
        exit = if (reducedMotion) fadeOut(tween(0)) else exit,
        modifier = modifier,
        content = content
    )
}
