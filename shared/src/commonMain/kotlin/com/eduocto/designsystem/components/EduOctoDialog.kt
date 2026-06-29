package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String = "Konfirmasi",
    dismissLabel: String = "Batal",
    isDestructive: Boolean = false,
    typedConfirmation: Boolean = false,
) {
    val colors = EduOctoTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = EduOctoTheme.typography.titleMedium, color = if (isDestructive) colors.danger else colors.onSurface) },
        text = { Text(text, style = EduOctoTheme.typography.bodyMedium, color = colors.onSurfaceMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = if (isDestructive) colors.danger else colors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel, color = colors.onSurfaceMuted)
            }
        },
        shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun EduOctoDialogPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoDialog(title = "Hapus data siswa?", text = "Data kehadiran 6 bulan terakhir akan ikut terhapus dan tidak dapat dikembalikan.", onConfirm = {}, onDismiss = {}, confirmLabel = "Hapus", isDestructive = true)
    }
}
