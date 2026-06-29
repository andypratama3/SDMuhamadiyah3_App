package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
) {
    val colors = EduOctoTheme.colors
    val typography = EduOctoTheme.typography
    val shape = RoundedCornerShape(EduOctoTheme.shapes.radiusSmall)

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = typography.bodyMedium) },
            placeholder = placeholder?.let { { Text(it, style = typography.bodyMedium, color = colors.onSurfaceFaint) } },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = colors.onSurfaceMuted) } },
            trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = if (isError) colors.danger else colors.onSurfaceMuted, modifier = Modifier.then(if (onTrailingIconClick != null) Modifier.padding(4.dp) else Modifier)) } },
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            textStyle = typography.bodyMedium.copy(color = if (enabled) colors.onSurface else colors.onSurfaceFaint),
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                errorBorderColor = colors.danger,
                cursorColor = colors.primary,
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurfaceMuted,
                errorLabelColor = colors.danger,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(errorMessage, style = typography.bodySmall, color = colors.danger)
        }
    }
}

@Preview
@Composable
private fun EduOctoTextFieldPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        var text by remember { mutableStateOf("") }
        Column(Modifier.padding(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            EduOctoTextField(value = text, onValueChange = { text = it }, label = "Nama Lengkap", placeholder = "Masukkan nama")
            EduOctoTextField(value = "Error", onValueChange = {}, label = "NISN", isError = true, errorMessage = "NISN harus 10 digit")
            EduOctoTextField(value = "Disabled", onValueChange = {}, label = "Kelas", enabled = false)
        }
    }
}
