package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

data class TabItem(
    val label: String,
    val icon: ImageVector? = null,
)

@Composable
fun EduOctoTabRow(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EduOctoTheme.colors
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = colors.background,
        contentColor = colors.primary,
        edgePadding = EduOctoTheme.spacing.md,
        divider = { Spacer(Modifier.height(0.dp)) },
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                text = {
                    Text(tab.label, style = EduOctoTheme.typography.labelLarge)
                },
                icon = if (tab.icon != null) {
                    { Icon(tab.icon, contentDescription = null) }
                } else null,
                selectedContentColor = colors.primary,
                unselectedContentColor = colors.onSurfaceMuted,
            )
        }
    }
}

@Preview
@Composable
private fun EduOctoTabRowPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoTabRow(
            tabs = listOf(TabItem("Semua"), TabItem("Aktif"), TabItem("Lulus")),
            selectedIndex = 0,
            onTabSelected = {},
        )
    }
}
