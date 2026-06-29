package com.eduocto.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(EduOctoTheme.spacing.sm))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = EduOctoTheme.typography.bodyMedium, color = EduOctoTheme.colors.onSurface)
                if (supportingText != null) {
                    Text(supportingText, style = EduOctoTheme.typography.metadata, color = EduOctoTheme.colors.onSurfaceMuted)
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(EduOctoTheme.spacing.sm))
                trailing()
            }
        }
        if (showDivider) HorizontalDivider(color = EduOctoTheme.colors.outline, thickness = 0.5.dp)
    }
}

@Preview
@Composable
private fun EduOctoListItemPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column {
            EduOctoListItem(title = "Budi Santoso", supportingText = "Kelas 5A - NISN: 1234567890")
            EduOctoListItem(title = "Siti Rahmawati", supportingText = "Kelas 5B - NISN: 0987654321")
        }
    }
}
