package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.NavBarShape
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.navigation.SDM3BottomTab

data class BottomNavItem(
    val label: String,
    val tab: SDM3BottomTab,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Beranda", SDM3BottomTab.Beranda, Icons.Default.Home),
    BottomNavItem("Nilai", SDM3BottomTab.Nilai, Icons.Default.School),
    BottomNavItem("Bayar", SDM3BottomTab.Bayar, Icons.Default.CreditCard),
    BottomNavItem("Rapor", SDM3BottomTab.Rapor, Icons.Default.Description),
    BottomNavItem("Profil", SDM3BottomTab.Profil, Icons.Default.AccountCircle)
)

@Composable
fun SDM3BottomNavBar(
    currentTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Surface(
            shape = NavBarShape,
            color = colorScheme.surface,
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentTab == item.tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(item.tab) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.SemiBold
                                       else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorScheme.primary,
                        selectedTextColor = colorScheme.primary,
                        unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        indicatorColor = colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}
