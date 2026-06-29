package com.eduocto.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme
import com.eduocto.designsystem.EduOctoMotion

@Composable
fun ProgressRing(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = EduOctoTheme.colors.outline.copy(alpha = 0.15f),
    progressColor: Color = EduOctoTheme.colors.primary,
    showLabel: Boolean = false,
) {
    val isIndeterminate = progress == null
    val transition = rememberInfiniteTransition(label = "progressRing")
    val animProgress by if (isIndeterminate) {
        transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "indeterminate")
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(progress) }
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val sweep = if (isIndeterminate) animProgress * 270f + 90f else progress * 360f
            val startAngle = if (isIndeterminate) animProgress * 360f else -90f
            drawArc(trackColor, 0f, 360f, useCenter = false, style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round))
            drawArc(progressColor, startAngle, sweep, useCenter = false, style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round))
        }
        if (showLabel && progress != null) {
            Text("${(progress * 100).toInt()}%", style = EduOctoTheme.typography.labelSmall, color = EduOctoTheme.colors.onSurface)
        }
    }
}

@Preview
@Composable
private fun ProgressRingPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Box(Modifier.padding(16.dp)) {
            ProgressRing(progress = 0.72f, showLabel = true)
        }
    }
}
