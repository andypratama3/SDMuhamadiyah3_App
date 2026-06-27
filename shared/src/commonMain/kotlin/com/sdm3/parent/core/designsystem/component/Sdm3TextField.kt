package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing

@Composable
fun Sdm3TextField(
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
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val isPreview = LocalInspectionMode.current

    if (!isPreview) {
        LaunchedEffect(isError) {
            if (isError) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = if (isError) colorScheme.error else colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )

        // EduOcto Glassmorphic Input Architecture
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp), // Mutlak 16px radius
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isError) colorScheme.error else colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    if (onTrailingIconClick != null) {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(it, contentDescription = null, modifier = Modifier.size(22.dp))
                        }
                    } else {
                        Icon(it, contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                }
            },
            isError = isError,
            visualTransformation = visualTransformation ?: androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = colorScheme.primary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                cursorColor = colorScheme.primary,
                unfocusedContainerColor = Color.White.copy(alpha = 0.5f), // Semi-transparent white
                focusedContainerColor = Color.White.copy(alpha = 0.8f),
                errorBorderColor = colorScheme.error,
                errorContainerColor = colorScheme.error.copy(alpha = 0.05f),
                disabledBorderColor = Color.Transparent,
                disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }
    }
}

@Preview
@Composable
fun Sdm3TextFieldPreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Sdm3TextField(value = "", onValueChange = {}, label = "Email Address", placeholder = "name@school.com", leadingIcon = Icons.Outlined.Email)
            Sdm3TextField(value = "Invalid Password", onValueChange = {}, label = "Security Key", isError = true, errorMessage = "Authentication failed")
        }
    }
}
