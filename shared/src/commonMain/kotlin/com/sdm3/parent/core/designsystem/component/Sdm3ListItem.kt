package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

@Composable
fun Sdm3ListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.4f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(
                horizontal = ProductSchoolTheme.spacing.md,
                vertical = ProductSchoolTheme.spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(ProductSchoolTheme.spacing.sm))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ProductSchoolTheme.typography.bodyMedium,
                color = ProductSchoolTheme.colors.onSurface.copy(alpha = alpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!supportingText.isNullOrEmpty()) {
                Text(
                    text = supportingText,
                    style = ProductSchoolTheme.typography.metadata,
                    color = ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = alpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = ProductSchoolTheme.typography.bodySmall,
                    color = ProductSchoolTheme.colors.onSurfaceVariant.copy(alpha = alpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(ProductSchoolTheme.spacing.sm))
            trailing()
        }
    }
}

@Preview
@Composable
private fun Sdm3ListItemPreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            Sdm3ListItem(
                title = "John Doe",
                supportingText = "Kelas 6A · NISN 1234567890",
                onClick = {},
            )
            Sdm3ListItem(
                title = "Disabled Item",
                supportingText = "This item is disabled",
                enabled = false,
            )
            Sdm3ListItem(
                title = "With Leading",
                supportingText = "Leading slot active",
                leading = {
                    Text("⭐", style = MaterialTheme.typography.titleMedium)
                },
                trailing = {
                    Text(">", style = MaterialTheme.typography.titleMedium)
                },
                onClick = {},
            )
        }
    }
}
