package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

@Composable
fun Sdm3StatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trendIcon: (@Composable () -> Unit)? = null,
    trendText: String? = null,
    trendPositive: Boolean? = null,
    isLoading: Boolean = false,
    containerColor: Color = Color.White,
) {
    if (isLoading) {
        Sdm3StatTileSkeleton(modifier = modifier)
        return
    }

    Sdm3Card(
        modifier = modifier,
        padding = ProductSchoolTheme.spacing.md,
    ) {
        Column {
            Text(
                text = label,
                style = ProductSchoolTheme.typography.metadata,
                color = ProductSchoolTheme.colors.onSurfaceMuted,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(ProductSchoolTheme.spacing.xs))
            Text(
                text = value,
                style = ProductSchoolTheme.typography.displayMedium,
                color = ProductSchoolTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
            )
            if (trendIcon != null || trendText != null) {
                Spacer(Modifier.height(ProductSchoolTheme.spacing.sm))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (trendIcon != null) trendIcon()
                    if (trendText != null) {
                        val trendColor = when (trendPositive) {
                            true -> ProductSchoolTheme.colors.success
                            false -> ProductSchoolTheme.colors.danger
                            null -> ProductSchoolTheme.colors.onSurfaceMuted
                        }
                        Text(
                            text = trendText,
                            style = ProductSchoolTheme.typography.labelSmall,
                            color = trendColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Sdm3StatTileSkeleton(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
        ),
        label = "skeletonAlpha",
    )
    val shimmer = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = alpha),
            Color.LightGray.copy(alpha = alpha * 0.5f),
        ),
        start = Offset.Zero,
        end = Offset(100f, 0f),
    )

    Sdm3Card(
        modifier = modifier,
        padding = ProductSchoolTheme.spacing.md,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmer),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmer),
            )
        }
    }
}

@Preview
@Composable
private fun Sdm3StatTilePreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Sdm3StatTile(
                label = "Total SPP",
                value = "Rp 500.000",
                trendText = "+12% dari bulan lalu",
                trendPositive = true,
            )
            Sdm3StatTile(
                label = "Kehadiran",
                value = "92%",
                trendText = "-3% dari minggu lalu",
                trendPositive = false,
            )
            Sdm3StatTile(
                label = "Memuat...",
                value = "—",
                isLoading = true,
            )
        }
    }
}
