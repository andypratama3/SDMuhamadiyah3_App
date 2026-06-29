package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

sealed class SnackbarStyle {
    data object Info : SnackbarStyle()
    data object Success : SnackbarStyle()
    data object Warning : SnackbarStyle()
    data object Danger : SnackbarStyle()
}

@Composable
fun Sdm3Snackbar(
    modifier: Modifier = Modifier,
    message: String,
    style: SnackbarStyle = SnackbarStyle.Info,
    visible: Boolean = true,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val bgColor: Color = when (style) {
        SnackbarStyle.Info -> ProductSchoolTheme.colors.info
        SnackbarStyle.Success -> ProductSchoolTheme.colors.success
        SnackbarStyle.Warning -> ProductSchoolTheme.colors.warning
        SnackbarStyle.Danger -> ProductSchoolTheme.colors.danger
    }
    val textColor = Color.White

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = bgColor,
                    shape = RoundedCornerShape(ProductSchoolTheme.shapes.radiusSmall),
                )
                .padding(start = ProductSchoolTheme.spacing.md, end = ProductSchoolTheme.spacing.sm, top = ProductSchoolTheme.spacing.sm, bottom = ProductSchoolTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = ProductSchoolTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.width(ProductSchoolTheme.spacing.sm))
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        style = ProductSchoolTheme.typography.labelLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Sdm3SnackbarPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Sdm3Snackbar(message = "Data berhasil disimpan", style = SnackbarStyle.Success)
            Sdm3Snackbar(message = "Koneksi terputus", style = SnackbarStyle.Danger, actionLabel = "Coba Lagi", onAction = {})
            Sdm3Snackbar(message = "Sedang dalam pemeliharaan", style = SnackbarStyle.Warning)
            Sdm3Snackbar(message = "Info terbaru tersedia", style = SnackbarStyle.Info)
        }
    }
}
