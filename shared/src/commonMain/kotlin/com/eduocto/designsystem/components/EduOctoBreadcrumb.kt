package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoBreadcrumb(
    items: List<String>,
    modifier: Modifier = Modifier,
    separator: String = "/",
) {
    val colors = EduOctoTheme.colors
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.size - 1
            Text(
                item,
                style = EduOctoTheme.typography.bodySmall,
                color = if (isLast) colors.onSurface else colors.onSurfaceMuted,
            )
            if (!isLast) {
                Text(separator, modifier = Modifier.padding(horizontal = 6.dp), color = colors.onSurfaceMuted)
            }
        }
    }
}

@Preview
@Composable
private fun EduOctoBreadcrumbPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoBreadcrumb(items = listOf("Beranda", "Akademik", "Nilai"), modifier = Modifier.padding(16.dp))
    }
}
