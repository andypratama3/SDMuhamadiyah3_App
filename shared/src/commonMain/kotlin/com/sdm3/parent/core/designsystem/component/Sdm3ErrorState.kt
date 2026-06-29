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
import androidx.compose.material.icons.outlined.ReportGmailerrorred
import androidx.compose.material.icons.outlined.WifiOff
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
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

sealed class ErrorStateStyle {
    data object Network : ErrorStateStyle()
    data object Server : ErrorStateStyle()
    data object Generic : ErrorStateStyle()
}

@Composable
fun Sdm3ErrorState(
    modifier: Modifier = Modifier,
    title: String = "Terjadi Kesalahan",
    message: String = "Silakan coba kembali.",
    style: ErrorStateStyle = ErrorStateStyle.Generic,
    icon: ImageVector? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null,
) {
    val resolvedIcon: ImageVector = icon ?: when (style) {
        ErrorStateStyle.Network -> Icons.Outlined.WifiOff
        ErrorStateStyle.Server -> Icons.Outlined.ReportGmailerrorred
        ErrorStateStyle.Generic -> Icons.Outlined.ReportGmailerrorred
    }
    val iconTint: Color = when (style) {
        ErrorStateStyle.Network -> ProductSchoolTheme.colors.danger
        ErrorStateStyle.Server -> ProductSchoolTheme.colors.warning
        ErrorStateStyle.Generic -> ProductSchoolTheme.colors.danger
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
                    .size(88.dp)
                    .background(
                        color = iconTint.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(22.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = resolvedIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(40.dp),
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
            Spacer(Modifier.height(ProductSchoolTheme.spacing.sm))
            Text(
                text = message,
                style = ProductSchoolTheme.typography.bodyMedium,
                color = ProductSchoolTheme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = ProductSchoolTheme.spacing.md),
            )
            if (primaryAction != null) {
                Spacer(Modifier.height(ProductSchoolTheme.spacing.lg))
                primaryAction()
            }
            if (secondaryAction != null) {
                Spacer(Modifier.height(ProductSchoolTheme.spacing.sm))
                secondaryAction()
            }
        }
    }
}

@Preview
@Composable
private fun Sdm3ErrorStatePreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            Sdm3ErrorState(
                style = ErrorStateStyle.Network,
                title = "Gangguan Jaringan",
                message = "Periksa koneksi internet Anda dan coba kembali.",
                modifier = Modifier.height(350.dp),
                primaryAction = {
                    Sdm3Button(
                        text = "Coba Lagi",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        }
    }
}
