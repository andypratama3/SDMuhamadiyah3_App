package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoSection(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = EduOctoTheme.typography.titleMedium, color = EduOctoTheme.colors.onSurface, modifier = Modifier.weight(1f))
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) { Text(actionLabel, style = EduOctoTheme.typography.labelLarge, color = EduOctoTheme.colors.primary) }
            }
        }
        content()
    }
}

@Preview
@Composable
private fun EduOctoSectionPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column {
            EduOctoSection(title = "Aktivitas Terbaru", actionLabel = "Lihat Semua", onAction = {}) {
                Text("Content here", modifier = Modifier.padding(horizontal = EduOctoTheme.spacing.md))
            }
        }
    }
}
