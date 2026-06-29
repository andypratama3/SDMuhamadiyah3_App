package com.eduocto.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TypographyScalePreview() {
    EduOctoTheme {
        val typography = EduOctoTheme.typography
        val colors = EduOctoTheme.colors

        Column(
            modifier = Modifier
                .background(colors.background)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("EduOcto Type Scale", style = typography.titleLarge, color = colors.onSurface)
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = colors.outline)
            Spacer(Modifier.height(8.dp))

            TypeScaleRow("displayLarge", "34/41 Bold", typography.displayLarge, colors)
            TypeScaleRow("displayMedium", "28/35 SemiBold", typography.displayMedium, colors)
            TypeScaleRow("titleLarge", "22/28 SemiBold", typography.titleLarge, colors)
            TypeScaleRow("titleMedium", "18/24 SemiBold", typography.titleMedium, colors)
            TypeScaleRow("bodyLarge", "16/24 Regular", typography.bodyLarge, colors)
            TypeScaleRow("bodyMedium", "14/20 Regular", typography.bodyMedium, colors)
            TypeScaleRow("bodySmall", "13/18 Regular", typography.bodySmall, colors)
            TypeScaleRow("labelLarge", "14/20 Medium", typography.labelLarge, colors)
            TypeScaleRow("labelSmall", "12/16 Medium", typography.labelSmall, colors)
            TypeScaleRow("metadata", "12/16 Muted", typography.metadata, colors, color = colors.onSurfaceMuted)
        }
    }
}

@Composable
private fun TypeScaleRow(
    name: String,
    spec: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    colors: EduOctoColors,
    color: Color? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = textStyle,
                color = color ?: colors.onSurface,
            )
        }
        Column(
            modifier = Modifier.width(100.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.End,
        ) {
            Text(
                text = spec,
                style = EduOctoTheme.typography.labelSmall,
                color = colors.onSurfaceMuted,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}
