package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning

@Composable
fun EduOctoErrorState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    retryLabel: String? = "Coba Lagi",
    onRetry: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EduOctoTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.Warning, contentDescription = null, modifier = Modifier.size(48.dp), tint = EduOctoTheme.colors.danger)
        Spacer(Modifier.height(EduOctoTheme.spacing.md))
        Text(title, style = EduOctoTheme.typography.titleMedium, color = EduOctoTheme.colors.danger, textAlign = TextAlign.Center)
        Spacer(Modifier.height(EduOctoTheme.spacing.sm))
        Text(description, style = EduOctoTheme.typography.bodySmall, color = EduOctoTheme.colors.onSurfaceMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(EduOctoTheme.spacing.md))
        if (retryLabel != null && onRetry != null) {
            EduOctoButton(text = retryLabel, onClick = onRetry)
        }
        if (secondaryLabel != null && onSecondary != null) {
            Spacer(Modifier.height(8.dp))
            EduOctoButton(text = secondaryLabel, onClick = onSecondary, variant = com.eduocto.designsystem.components.EduOctoButtonVariant.Tertiary)
        }
    }
}

@Preview
@Composable
private fun EduOctoErrorStatePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoErrorState(title = "Gagal memuat data", description = "Terjadi kesalahan jaringan. Periksa koneksi Anda dan coba lagi.", onRetry = {})
    }
}
