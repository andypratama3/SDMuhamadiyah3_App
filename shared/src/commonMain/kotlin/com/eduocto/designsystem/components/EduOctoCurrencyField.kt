package com.eduocto.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoCurrencyField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    prefix: String = "Rp",
) {
    val colors = EduOctoTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }
            onValueChange(digits)
        },
        label = { Text(label) },
        prefix = { Text(prefix, color = if (enabled) colors.onSurface else colors.onSurfaceMuted) },
        modifier = modifier,
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        supportingText = if (error != null) {{ Text(error, color = colors.danger) }} else null,
        shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            errorBorderColor = colors.danger,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurfaceMuted,
            errorLabelColor = colors.danger,
            cursorColor = colors.primary,
            disabledBorderColor = colors.outline.copy(alpha = 0.4f),
            disabledLabelColor = colors.onSurfaceMuted.copy(alpha = 0.5f),
        ),
    )
}

@Preview
@Composable
private fun EduOctoCurrencyFieldPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoCurrencyField(value = "150000", onValueChange = {}, label = "Nominal")
    }
}
