package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduOctoDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = EduOctoTheme.colors
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.outline,
        errorBorderColor = colors.danger,
        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurfaceMuted,
        errorLabelColor = colors.danger,
        cursorColor = colors.primary,
        disabledBorderColor = colors.outline.copy(alpha = 0.4f),
        disabledLabelColor = colors.onSurfaceMuted.copy(alpha = 0.5f),
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = enabled)
                .fillMaxWidth(),
            colors = textFieldColors,
            shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium),
            enabled = enabled,
            isError = error != null,
            supportingText = if (error != null) {{ Text(error, color = colors.danger) }} else null,
            singleLine = true,
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EduOctoDropdownPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoDropdown(
            label = "Pilih Kelas",
            options = listOf("5A", "5B", "6A"),
            selectedOption = null,
            onOptionSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
