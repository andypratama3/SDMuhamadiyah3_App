package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

data class NavRailItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
)

@Composable
fun EduOctoNavRail(
    items: List<NavRailItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = EduOctoTheme.colors.surface,
    ) {
        if (header != null) {
            header()
            Spacer(Modifier.height(EduOctoTheme.spacing.md))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEachIndexed { index, item ->
                NavigationRailItem(
                    selected = index == selectedIndex,
                    onClick = { onItemSelected(index) },
                    icon = {
                        if (item.badgeCount > 0) {
                            androidx.compose.foundation.layout.Box {
                                Icon(item.icon, contentDescription = item.label)
                                EduOctoBadge(count = item.badgeCount, modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd))
                            }
                        } else {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    },
                    label = { Text(item.label, style = EduOctoTheme.typography.labelSmall) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = EduOctoTheme.colors.primary,
                        unselectedIconColor = EduOctoTheme.colors.onSurfaceMuted,
                        selectedTextColor = EduOctoTheme.colors.primary,
                        unselectedTextColor = EduOctoTheme.colors.onSurfaceMuted,
                        indicatorColor = EduOctoTheme.colors.primary.copy(alpha = 0.12f),
                    ),
                )
            }
        }
    }
}
