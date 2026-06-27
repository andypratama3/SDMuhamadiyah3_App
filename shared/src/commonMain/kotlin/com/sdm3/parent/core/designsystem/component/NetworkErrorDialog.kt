package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.*

@Composable
fun NetworkErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
        icon = {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(20.dp),
                color = colorScheme.error.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.WifiOff,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        },
        title = {
            Text(
                "Gangguan Koneksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colorScheme.primary,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                "Sistem gagal menjangkau server institusi. Pastikan otentikasi jaringan Anda aktif dan silakan coba kembali.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Sdm3Button(
                text = "Coba Hubungkan Kembali",
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                containerColor = colorScheme.primary,
                contentColor = Color.White
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    "LANJUTKAN OFFLINE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = colorScheme.primary.copy(alpha = 0.4f)
                )
            }
        }
    )
}

@Preview
@Composable
private fun NetworkErrorDialogPreview() {
    SDM3Theme {
        NetworkErrorDialog(onRetry = {}, onDismiss = {})
    }
}
