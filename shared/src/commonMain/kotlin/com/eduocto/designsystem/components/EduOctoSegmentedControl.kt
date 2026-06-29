package com.eduocto.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EduOctoTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EduOctoTheme.shapes.radiusMedium))
            .background(colors.surfaceVariant),
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(EduOctoTheme.shapes.radiusMedium))
                    .background(if (isSelected) colors.surface else colors.surfaceVariant)
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = EduOctoTheme.typography.labelLarge,
                    color = if (isSelected) colors.primary else colors.onSurfaceMuted,
                )
            }
        }
    }
}

@Preview
@Composable
private fun EduOctoSegmentedControlPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoSegmentedControl(options = listOf("Minggu", "Bulan", "Tahun"), selectedIndex = 0, onOptionSelected = {}, modifier = Modifier.padding(EduOctoTheme.spacing.md))
    }
}
