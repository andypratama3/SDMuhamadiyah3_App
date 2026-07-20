package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.statusSuccessColor
import com.sdm3.parent.core.designsystem.theme.statusWarningColor
import com.sdm3.parent.core.designsystem.theme.statusDangerColor

/**
 * A premium progress bar with animated fill and gradient support.
 */
@Composable
fun Sdm3ProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    useGradient: Boolean = false,
    animated: Boolean = true,
    showPercentage: Boolean = false,
    label: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )
    
    Column(modifier = modifier) {
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .then(
                        if (useGradient) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colorScheme.primary,
                                        colorScheme.secondary
                                    )
                                )
                            )
                        } else {
                            Modifier.background(progressColor)
                        }
                    )
            )
        }
    }
}

/**
 * A circular progress indicator with percentage display.
 */
@Composable
fun Sdm3CircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    showPercentage: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Progress arc
        androidx.compose.material3.CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(size),
            color = progressColor,
            strokeWidth = strokeWidth,
            trackColor = backgroundColor
        )
        
        if (showPercentage) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = colorScheme.primary
            )
        }
    }
}

/**
 * Progress indicator with semantic color based on value.
 */
@Composable
fun Sdm3SemanticProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val successColor = statusSuccessColor()
    val warningColor = statusWarningColor()
    val dangerColor = statusDangerColor()
    
    val progressColor = when {
        progress >= 0.75f -> successColor
        progress >= 0.5f -> colorScheme.primary
        progress >= 0.25f -> warningColor
        else -> dangerColor
    }
    
    Sdm3ProgressIndicator(
        progress = progress,
        modifier = modifier,
        progressColor = progressColor,
        showPercentage = showPercentage,
        label = label
    )
}

@Preview
@Composable
private fun Sdm3ProgressIndicatorPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Sdm3ProgressIndicator(
                progress = 0.75f,
                label = "Progress",
                showPercentage = true
            )
            
            Sdm3ProgressIndicator(
                progress = 0.5f,
                useGradient = true,
                height = 12.dp
            )
            
            Sdm3SemanticProgress(
                progress = 0.85f,
                label = "Kehadiran"
            )
            
            Sdm3SemanticProgress(
                progress = 0.4f,
                label = "Nilai"
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Sdm3CircularProgress(progress = 0.75f, size = 60.dp)
                Sdm3CircularProgress(progress = 0.5f, size = 60.dp)
                Sdm3CircularProgress(progress = 0.25f, size = 60.dp)
            }
        }
    }
}
