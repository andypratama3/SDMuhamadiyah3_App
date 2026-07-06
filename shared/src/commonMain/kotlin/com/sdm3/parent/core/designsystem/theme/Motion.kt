package com.sdm3.parent.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing

object Sdm3Motion {
    val easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    const val durationInstant = 80
    const val durationFast = 150
    const val durationDefault = 250
    const val durationSlow = 400
    const val staggerStep = 40
}
