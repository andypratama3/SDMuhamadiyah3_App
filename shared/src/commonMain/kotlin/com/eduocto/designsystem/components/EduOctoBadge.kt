package com.eduocto.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoBadge(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = EduOctoTheme.colors.danger,
    maxCount: Int = 99,
) {
    val text = if (count > maxCount) "$maxCount+" else count.toString()
    val bgColor = if (count > 0) color else Color.Transparent
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (count > 0) {
            Text(
                text,
                style = EduOctoTheme.typography.labelSmall.copy(color = Color.White),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun EduOctoDotBadge(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    size: Dp = 10.dp,
    color: Color = EduOctoTheme.colors.danger,
) {
    if (visible) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(color),
        )
    }
}

@Preview
@Composable
private fun EduOctoBadgePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Box(Modifier.padding(16.dp)) {
            EduOctoBadge(count = 12)
        }
    }
}
