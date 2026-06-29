package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari...",
) {
    val colors = EduOctoTheme.colors
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, style = EduOctoTheme.typography.bodyMedium, color = colors.onSurfaceMuted.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Filled.Search, "Cari", tint = colors.onSurfaceMuted) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, "Hapus", tint = colors.onSurfaceMuted)
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline.copy(alpha = 0.4f),
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surfaceVariant,
            cursorColor = colors.primary,
        ),
    )
}

@Preview
@Composable
private fun EduOctoSearchBarPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoSearchBar(query = "", onQueryChange = {}, modifier = Modifier.padding(EduOctoTheme.spacing.md))
    }
}
