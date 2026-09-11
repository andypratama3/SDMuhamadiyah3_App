package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme

@Composable
fun Sdm3Dialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    body: @Composable () -> Unit,
    dismissLabel: String = "Batal",
    onDismiss: (() -> Unit)? = null,
    confirmEnabled: Boolean = true,
    destructive: Boolean = false,
) {
    val colors = ProductSchoolTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        containerColor = colors.liquidGlassSurface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
        },
        text = { body() },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled,
                shape = RoundedCornerShape(12.dp),
                colors = if (destructive) {
                    ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                } else {
                    ButtonDefaults.buttonColors()
                },
            ) {
                Text(
                    text = confirmLabel,
                    color = if (destructive) colorScheme.onError else Color.Unspecified,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss ?: onDismissRequest) {
                Text(
                    text = dismissLabel,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                )
            }
        },
    )
}