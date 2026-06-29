package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = EduOctoTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.primary,
                unselectedColor = colors.onSurfaceMuted,
                disabledSelectedColor = colors.onSurface.copy(alpha = 0.3f),
                disabledUnselectedColor = colors.onSurface.copy(alpha = 0.2f),
            ),
        )
        Spacer(Modifier.width(EduOctoTheme.spacing.xs))
        Text(label, style = EduOctoTheme.typography.bodyMedium, color = if (enabled) colors.onSurface else colors.onSurfaceMuted)
    }
}

@Preview
@Composable
private fun EduOctoRadioButtonPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoRadioButton(selected = true, onClick = {}, label = "Laki-laki")
    }
}
