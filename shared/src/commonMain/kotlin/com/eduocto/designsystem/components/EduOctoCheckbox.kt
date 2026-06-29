package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = EduOctoTheme.colors
    val content = @Composable {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.onSurfaceMuted,
                checkmarkColor = colors.onPrimary,
                disabledCheckedColor = colors.onSurface.copy(alpha = 0.3f),
                disabledUncheckedColor = colors.onSurface.copy(alpha = 0.2f),
            ),
        )
    }
    if (label != null) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            content()
            Spacer(Modifier.width(EduOctoTheme.spacing.xs))
            Text(label, style = EduOctoTheme.typography.bodyMedium, color = if (enabled) colors.onSurface else colors.onSurfaceMuted)
        }
    } else {
        content()
    }
}

@Preview
@Composable
private fun EduOctoCheckboxPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoCheckbox(checked = true, onCheckedChange = {}, label = "Setuju dengan syarat & ketentuan")
    }
}
