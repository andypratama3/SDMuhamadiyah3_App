package com.eduocto.designsystem.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.eduocto.designsystem.EduOctoTheme
import androidx.compose.material3.Icon

@Composable
fun EduOctoIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = EduOctoTheme.colors.onSurface,
    containerColor: Color = Color.Transparent,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = tint,
            containerColor = containerColor,
        ),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}
