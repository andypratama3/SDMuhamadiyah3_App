package com.eduocto.designsystem.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
)

@Composable
fun EduOctoBottomNavBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = EduOctoTheme.colors.surface,
        tonalElevation = 0.dp,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
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
                colors = NavigationBarItemDefaults.colors(
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
