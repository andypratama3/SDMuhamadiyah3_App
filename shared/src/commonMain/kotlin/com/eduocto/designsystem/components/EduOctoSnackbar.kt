package com.eduocto.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

enum class SnackbarTone { Success, Error, Warning, Info }

@Composable
fun EduOctoSnackbar(
    message: String,
    tone: SnackbarTone = SnackbarTone.Info,
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = EduOctoTheme.colors
    val (bg, fg) = when (tone) {
        SnackbarTone.Success -> colors.success.copy(alpha = 0.12f) to colors.success
        SnackbarTone.Error -> colors.danger.copy(alpha = 0.12f) to colors.danger
        SnackbarTone.Warning -> colors.warning.copy(alpha = 0.12f) to colors.warning
        SnackbarTone.Info -> colors.info.copy(alpha = 0.12f) to colors.info
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = EduOctoTheme.spacing.md)
                .clip(RoundedCornerShape(EduOctoTheme.shapes.radiusMedium))
                .background(bg)
                .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(message, style = EduOctoTheme.typography.bodyMedium, color = fg, modifier = Modifier.weight(1f))
                if (actionLabel != null && onAction != null) {
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onAction) { Text(actionLabel, color = fg) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EduOctoSnackbarPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoSnackbar(message = "Data berhasil disimpan", tone = SnackbarTone.Success)
    }
}
