package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoDivider(
    modifier: Modifier = Modifier,
    label: String? = null,
    thickness: Dp = 1.dp,
) {
    val color = EduOctoTheme.colors.outline.copy(alpha = 0.2f)
    if (label != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = EduOctoTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = thickness, color = color)
            Spacer(Modifier.width(EduOctoTheme.spacing.sm))
            Text(label, style = EduOctoTheme.typography.labelSmall, color = EduOctoTheme.colors.onSurfaceMuted)
            Spacer(Modifier.width(EduOctoTheme.spacing.sm))
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = thickness, color = color)
        }
    } else {
        HorizontalDivider(
            modifier = modifier.fillMaxWidth(),
            thickness = thickness,
            color = color,
        )
    }
}

@Preview
@Composable
private fun EduOctoDividerPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
            Text("Above")
            EduOctoDivider(label = "atau")
            Text("Below")
        }
    }
}
