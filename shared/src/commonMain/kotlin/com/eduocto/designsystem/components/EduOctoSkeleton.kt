package com.eduocto.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme
import com.eduocto.designsystem.EduOctoMotion

@Composable
fun EduOctoSkeleton(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 16.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(EduOctoTheme.shapes.radiusSmall),
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -200f, targetValue = 200f,
        animationSpec = infiniteRepeatable(
            tween(EduOctoMotion.durationSlow * 3, easing = LinearEasing),
            RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    val colors = EduOctoTheme.colors
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.outline.copy(alpha = 0.3f),
                        colors.outline.copy(alpha = 0.6f),
                        colors.outline.copy(alpha = 0.3f),
                    ),
                    start = Offset(translate, 0f),
                    end = Offset(translate + 200f, 0f),
                ),
            ),
    )
}

@Composable
fun EduOctoSkeletonCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EduOctoTheme.spacing.md),
    ) {
        EduOctoSkeleton(height = 20.dp, width = 160.dp)
        Spacer(Modifier.height(8.dp))
        EduOctoSkeleton(height = 14.dp)
        Spacer(Modifier.height(4.dp))
        EduOctoSkeleton(height = 14.dp, width = 240.dp)
    }
}

@Preview
@Composable
private fun EduOctoSkeletonPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column(Modifier.padding(16.dp)) {
            EduOctoSkeletonCard()
            Spacer(Modifier.height(8.dp))
            EduOctoSkeletonCard()
        }
    }
}
