package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = EduOctoTheme.colors
    val content = @Composable {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.onSurface,
                uncheckedTrackColor = colors.surfaceVariant,
                disabledCheckedThumbColor = colors.onSurfaceMuted,
                disabledCheckedTrackColor = colors.onSurface.copy(alpha = 0.12f),
                disabledUncheckedThumbColor = colors.onSurfaceMuted,
                disabledUncheckedTrackColor = colors.onSurface.copy(alpha = 0.08f),
            ),
        )
    }
    if (label != null) {
        Row(modifier = modifier.padding(vertical = EduOctoTheme.spacing.xs), verticalAlignment = Alignment.CenterVertically) {
            content()
            Spacer(Modifier.width(EduOctoTheme.spacing.sm))
            Text(label, style = EduOctoTheme.typography.bodyMedium, color = if (enabled) colors.onSurface else colors.onSurfaceMuted)
        }
    } else {
        content()
    }
}

@Preview
@Composable
private fun EduOctoSwitchPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoSwitch(checked = true, onCheckedChange = {}, label = "Kirim notifikasi")
    }
}
