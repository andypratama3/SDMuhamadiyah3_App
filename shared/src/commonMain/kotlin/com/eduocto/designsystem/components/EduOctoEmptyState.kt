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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

enum class EmptyStateTone { Neutral, Positive, Informative }

@Composable
fun EduOctoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    tone: EmptyStateTone = EmptyStateTone.Neutral,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = EduOctoTheme.colors
    val iconColor = when (tone) {
        EmptyStateTone.Positive -> colors.success
        EmptyStateTone.Informative -> colors.info
        EmptyStateTone.Neutral -> colors.onSurfaceMuted
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EduOctoTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = iconColor)
            Spacer(Modifier.height(EduOctoTheme.spacing.md))
        }
        Text(title, style = EduOctoTheme.typography.titleMedium, color = colors.onSurface, textAlign = TextAlign.Center)
        if (description != null) {
            Spacer(Modifier.height(EduOctoTheme.spacing.sm))
            Text(description, style = EduOctoTheme.typography.bodySmall, color = colors.onSurfaceMuted, textAlign = TextAlign.Center)
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(EduOctoTheme.spacing.md))
            EduOctoButton(text = actionLabel, onClick = onAction)
        }
    }
}

@Preview
@Composable
private fun EduOctoEmptyStatePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EduOctoEmptyState(title = "Belum ada siswa terdaftar", description = "Tambahkan siswa baru untuk memulai", actionLabel = "Tambah Siswa", onAction = {})
            EduOctoEmptyState(title = "Semua pembayaran lunas", tone = EmptyStateTone.Positive)
        }
    }
}
