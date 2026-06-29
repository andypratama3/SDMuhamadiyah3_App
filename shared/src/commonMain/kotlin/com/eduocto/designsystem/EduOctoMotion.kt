package com.eduocto.designsystem

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

object EduOctoMotion {
    val easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    const val durationInstant = 80
    const val durationFast = 150
    const val durationDefault = 250
    const val durationSlow = 400
    const val staggerStep = 40
}

fun eduOctoScreenEnter(): EnterTransition =
    fadeIn(animationSpec = tween(EduOctoMotion.durationSlow, easing = EduOctoMotion.easing)) +
        slideInHorizontally(
            animationSpec = tween(EduOctoMotion.durationSlow, easing = EduOctoMotion.easing),
            initialOffsetX = { it / 8 },
        )

fun eduOctoScreenExit(): ExitTransition =
    fadeOut(animationSpec = tween(EduOctoMotion.durationDefault, easing = EduOctoMotion.easing)) +
        slideOutVertically(
            animationSpec = tween(EduOctoMotion.durationDefault, easing = EduOctoMotion.easing),
            targetOffsetY = { it / 8 },
        )

@Composable
fun Modifier.eduOctoPressScale(isPressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "eduOctoPressScale",
    )
    return this.then(Modifier.graphicsLayer { scaleX = scale; scaleY = scale })
}
