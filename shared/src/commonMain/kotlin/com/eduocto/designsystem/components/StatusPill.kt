package com.eduocto.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

enum class StatusPillColor {
    Success, Warning, Danger, Info, Neutral, Primary,
}

@Composable
fun StatusPill(
    text: String,
    color: StatusPillColor = StatusPillColor.Neutral,
    modifier: Modifier = Modifier,
) {
    val colors = EduOctoTheme.colors
    val (bg, fg) = when (color) {
        StatusPillColor.Success -> colors.success.copy(alpha = 0.12f) to colors.success
        StatusPillColor.Warning -> colors.warning.copy(alpha = 0.12f) to colors.warning
        StatusPillColor.Danger -> colors.danger.copy(alpha = 0.12f) to colors.danger
        StatusPillColor.Info -> colors.info.copy(alpha = 0.12f) to colors.info
        StatusPillColor.Primary -> colors.primary.copy(alpha = 0.12f) to colors.primary
        StatusPillColor.Neutral -> colors.onSurface.copy(alpha = 0.08f) to colors.onSurfaceMuted
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(EduOctoTheme.shapes.radiusPill))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text, style = EduOctoTheme.typography.labelSmall, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Preview
@Composable
private fun StatusPillPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Box(Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                StatusPill("LUNAS", StatusPillColor.Success)
                StatusPill("TERTUNDA", StatusPillColor.Warning)
                StatusPill("TERLAMBAT", StatusPillColor.Danger)
                StatusPill("DRAFT", StatusPillColor.Info)
                StatusPill("AKTIF", StatusPillColor.Primary)
                StatusPill("NONAKTIF", StatusPillColor.Neutral)
            }
        }
    }
}
