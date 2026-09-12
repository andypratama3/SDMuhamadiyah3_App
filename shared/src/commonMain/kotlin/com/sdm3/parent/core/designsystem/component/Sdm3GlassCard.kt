package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.CardShape
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

@Composable
fun Sdm3GlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    tint: Color = ProductSchoolTheme.colors.liquidGlassSurface,
    content: @Composable () -> Unit,
) {
    val colors = ProductSchoolTheme.colors
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 4f
                shape = CardShape
                clip = true
                ambientShadowColor = colors.liquidGlassShadow
                spotShadowColor = colors.liquidGlassShadow
            },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = tint,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    colors.liquidGlassBorder,
                    Color.Transparent,
                ),
            ),
        ),
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Preview
@Composable
private fun Sdm3GlassCardPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Sdm3GlassCard(padding = 20.dp) {
                Text("Glass Card", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Premium frosted glass surface with inner highlight border.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
