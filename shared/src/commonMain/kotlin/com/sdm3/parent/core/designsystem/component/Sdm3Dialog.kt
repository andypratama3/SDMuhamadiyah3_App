package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.DialogShape
import com.sdm3.parent.core.designsystem.theme.GlassOutline
import com.sdm3.parent.core.designsystem.theme.GlassSurface
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing

@Composable
fun Sdm3Dialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = ProductSchoolTheme.colors.primary,
    confirmLabel: String = "Konfirmasi",
    dismissLabel: String? = "Batal",
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        shape = DialogShape,
        containerColor = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.border(1.dp, GlassOutline, DialogShape),
        icon = if (icon != null) {
            {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = iconTint.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, iconTint.copy(alpha = 0.12f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
        } else null,
        title = if (title != null) {
            {
                Text(
                    text = title,
                    style = ProductSchoolTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = ProductSchoolTheme.colors.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else null,
        text = if (message != null) {
            {
                Text(
                    text = message,
                    style = ProductSchoolTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else null,
        confirmButton = {
            Sdm3Button(
                text = confirmLabel,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            )
        },
        dismissButton = if (dismissLabel != null && onDismiss != null) {
            {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                ) {
                    Text(
                        text = dismissLabel.uppercase(),
                        style = ProductSchoolTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = ProductSchoolTheme.colors.primary.copy(alpha = 0.5f),
                    )
                }
            }
        } else null,
    )
}

@Preview
@Composable
private fun Sdm3DialogPreview() {
    SDM3Theme {
        Sdm3Dialog(
            title = "Hapus Data?",
            message = "Data yang dihapus tidak dapat dikembalikan.",
            confirmLabel = "Hapus",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
