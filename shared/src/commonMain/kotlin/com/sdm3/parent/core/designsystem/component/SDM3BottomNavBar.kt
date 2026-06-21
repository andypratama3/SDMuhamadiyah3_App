package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.SecondaryContainer
import com.sdm3.parent.core.navigation.SDM3BottomTab

data class BottomNavItem(
    val label: String,
    val tab: SDM3BottomTab,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Beranda", SDM3BottomTab.Beranda, Icons.Default.Home),
    BottomNavItem("Nilai", SDM3BottomTab.Nilai, Icons.Default.Assessment),
    BottomNavItem("Bayar", SDM3BottomTab.Bayar, Icons.Default.Payments),
    BottomNavItem("Rapor", SDM3BottomTab.Rapor, Icons.Default.Description),
    BottomNavItem("Profil", SDM3BottomTab.Profil, Icons.Default.Person)
)

@Composable
fun SDM3BottomNavBar(
    currentTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
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
                        tint = if (selected) Secondary else OnSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) Secondary else OnSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SecondaryContainer
                )
            )
        }
    }
}
