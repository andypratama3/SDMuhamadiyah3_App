package com.eduocto.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoAvatar(
    text: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = EduOctoTheme.colors.primary.copy(alpha = 0.12f),
    textColor: Color = EduOctoTheme.colors.primary,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = text, tint = textColor, modifier = Modifier.size(size * 0.5f))
        } else if (text != null) {
            Text(
                text.take(2).uppercase(),
                style = EduOctoTheme.typography.labelLarge,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun EduOctoAvatarPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Box(Modifier.padding(16.dp)) {
            EduOctoAvatar(text = "AR")
        }
    }
}
