package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.graphicsLayer
import com.sdm3.parent.core.designsystem.theme.CardShape
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

@Composable
fun Sdm3Card(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = ProductSchoolTheme.colors
    val cardBorder = border ?: BorderStroke(
        width = 0.5.dp, // Hairline per standards
        color = colors.liquidGlassBorder,
    )
    val cardModifier = modifier
        .fillMaxWidth()
        .graphicsLayer {
            shadowElevation = 2f
            shape = CardShape
            clip = true
            ambientShadowColor = colors.liquidGlassShadow
            spotShadowColor = colors.liquidGlassShadow
        }

    if (onClick != null) {
        Card(
            modifier = cardModifier,
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = colors.liquidGlassSurface
            ),
            border = cardBorder,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = colors.liquidGlassSurface
            ),
            border = cardBorder,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun Sdm3CardPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            Sdm3Card {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sdm3Card", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}