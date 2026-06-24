package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.InputShape
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing

@Composable
fun Sdm3TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val isPreview = LocalInspectionMode.current

    // Smart Gesture: Vibration on error
    if (!isPreview) {
        LaunchedEffect(isError) {
            if (isError) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) colorScheme.error else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 0.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = InputShape,
            leadingIcon = leadingIcon?.let {
                {
                    Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    if (onTrailingIconClick != null) {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            },
            isError = isError,
            visualTransformation = visualTransformation
                ?: androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = colorScheme.primary,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedLeadingIconColor = colorScheme.primary,
                unfocusedLeadingIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                errorBorderColor = colorScheme.error,
                errorContainerColor = colorScheme.error.copy(alpha = 0.05f),
                disabledBorderColor = colorScheme.outline.copy(alpha = 0.1f),
                disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun Sdm3TextFieldPreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Sdm3TextField(value = "", onValueChange = {}, label = "Email", leadingIcon = Icons.Outlined.Email)
            Sdm3TextField(value = "Input error", onValueChange = {}, label = "Password", isError = true, errorMessage = "Password salah")
        }
    }
}
