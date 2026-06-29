package com.eduocto.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = EduOctoTheme.colors
    val shape = RoundedCornerShape(EduOctoTheme.shapes.radiusPill)
    val bg = if (selected) colors.primary.copy(alpha = 0.12f) else Color.Transparent
    val fg = if (selected) colors.primary else colors.onSurfaceMuted
    val border = if (selected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, colors.outline.copy(alpha = 0.4f))

    Surface(
        onClick = onClick ?: {},
        shape = shape,
        color = bg,
        border = border,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(text, style = EduOctoTheme.typography.labelLarge, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview
@Composable
private fun EduOctoChipPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            EduOctoChip("Semua", selected = true)
            EduOctoChip("Kelas 5A")
            EduOctoChip("Ekstrakurikuler")
        }
    }
}
