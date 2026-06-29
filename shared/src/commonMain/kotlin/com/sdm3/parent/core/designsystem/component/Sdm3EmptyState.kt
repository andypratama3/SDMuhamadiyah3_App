package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

sealed class EmptyStateStyle {
    data object Neutral : EmptyStateStyle()
    data object Positive : EmptyStateStyle()
    data object Informative : EmptyStateStyle()
}

@Composable
fun Sdm3EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    message: String? = null,
    style: EmptyStateStyle = EmptyStateStyle.Neutral,
    icon: ImageVector? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val resolvedIcon: ImageVector = icon ?: when (style) {
        EmptyStateStyle.Neutral -> Icons.Outlined.FolderOpen
        EmptyStateStyle.Positive -> Icons.Outlined.SentimentSatisfied
        EmptyStateStyle.Informative -> Icons.Outlined.Info
    }
    val iconTint: Color = when (style) {
        EmptyStateStyle.Neutral -> ProductSchoolTheme.colors.onSurfaceMuted
        EmptyStateStyle.Positive -> ProductSchoolTheme.colors.success
        EmptyStateStyle.Informative -> ProductSchoolTheme.colors.info
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(ProductSchoolTheme.spacing.xl),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = iconTint.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(20.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = resolvedIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.height(ProductSchoolTheme.spacing.lg))
            Text(
                text = title,
                style = ProductSchoolTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ProductSchoolTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            if (message != null) {
                Spacer(Modifier.height(ProductSchoolTheme.spacing.sm))
                Text(
                    text = message,
                    style = ProductSchoolTheme.typography.bodyMedium,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = ProductSchoolTheme.spacing.md),
                )
            }
            if (action != null) {
                Spacer(Modifier.height(ProductSchoolTheme.spacing.lg))
                action()
            }
        }
    }
}

@Preview
@Composable
private fun Sdm3EmptyStatePreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            Sdm3EmptyState(
                title = "Belum Ada Data",
                message = "Data siswa belum tersedia. Silakan tambahkan data terlebih dahulu.",
                modifier = Modifier.height(300.dp),
                action = {
                    Sdm3Button(text = "Tambah Data", onClick = {}, modifier = Modifier.fillMaxWidth())
                },
            )
        }
    }
}
