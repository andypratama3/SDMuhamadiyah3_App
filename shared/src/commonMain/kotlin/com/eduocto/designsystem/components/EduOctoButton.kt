package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

enum class EduOctoButtonVariant { Primary, Secondary, Tertiary, Destructive }

@Composable
fun EduOctoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: EduOctoButtonVariant = EduOctoButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    fillMaxWidth: Boolean = false,
) {
    val colors = EduOctoTheme.colors
    val shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium)
    val contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    val m = modifier.then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
    val e = enabled && !isLoading

    val content: @Composable RowScope.() -> Unit = {
        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = if (variant == EduOctoButtonVariant.Primary) Color.White else colors.primary,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
                Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (icon != null) { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
                Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }

    when (variant) {
        EduOctoButtonVariant.Primary -> Button(
            onClick = onClick, modifier = m, enabled = e, shape = shape, contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.White, disabledContainerColor = colors.onSurface.copy(alpha = 0.12f)),
            content = content,
        )
        EduOctoButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick, modifier = m, enabled = e, shape = shape, contentPadding = contentPadding,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
            content = content,
        )
        EduOctoButtonVariant.Tertiary -> TextButton(
            onClick = onClick, modifier = m, enabled = e, shape = shape, contentPadding = contentPadding,
            colors = ButtonDefaults.textButtonColors(contentColor = colors.primary),
            content = content,
        )
        EduOctoButtonVariant.Destructive -> FilledTonalButton(
            onClick = onClick, modifier = m, enabled = e, shape = shape, contentPadding = contentPadding,
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = colors.danger.copy(alpha = 0.12f), contentColor = colors.danger),
            content = content,
        )
    }
}

@Preview
@Composable
private fun EduOctoButtonPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EduOctoButton("Primary", onClick = {})
            EduOctoButton("Secondary", onClick = {}, variant = EduOctoButtonVariant.Secondary)
            EduOctoButton("Tertiary", onClick = {}, variant = EduOctoButtonVariant.Tertiary)
            EduOctoButton("Destructive", onClick = {}, variant = EduOctoButtonVariant.Destructive)
            EduOctoButton("Loading", onClick = {}, isLoading = true)
            EduOctoButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
