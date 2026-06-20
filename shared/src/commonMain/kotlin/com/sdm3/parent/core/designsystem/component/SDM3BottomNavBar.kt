package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.SecondaryContainer
import com.sdm3.parent.core.navigation.SDM3BottomTab

data class BottomNavItem(
    val label: String,
    val tab: SDM3BottomTab,
    val emoji: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Beranda", SDM3BottomTab.Beranda, "\uD83C\uDFE0"),
    BottomNavItem("Nilai", SDM3BottomTab.Nilai, "\u2B50"),
    BottomNavItem("Bayar", SDM3BottomTab.Bayar, "\uD83D\uDCB3"),
    BottomNavItem("Rapor", SDM3BottomTab.Rapor, "\uD83D\uDCC4"),
    BottomNavItem("Profil", SDM3BottomTab.Profil, "\uD83D\uDC64")
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
                    Text(
                        text = item.emoji,
                        style = MaterialTheme.typography.titleMedium
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
