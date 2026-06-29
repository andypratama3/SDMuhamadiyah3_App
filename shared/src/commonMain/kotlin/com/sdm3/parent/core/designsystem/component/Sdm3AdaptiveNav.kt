package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.navigation.SDM3BottomTab

enum class WindowWidthSizeClass { Compact, Medium, Expanded }

data class BottomNavItem(
    val label: String,
    val tab: SDM3BottomTab,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

internal val navItems = listOf(
    BottomNavItem("Home", SDM3BottomTab.Beranda, Icons.Outlined.Home),
    BottomNavItem("Nilai", SDM3BottomTab.Nilai, Icons.Outlined.CalendarMonth),
    BottomNavItem("Bayar", SDM3BottomTab.Bayar, Icons.Outlined.QrCodeScanner),
    BottomNavItem("Rapor", SDM3BottomTab.Rapor, Icons.Outlined.School),
    BottomNavItem("Profil", SDM3BottomTab.Profil, Icons.Outlined.Menu)
)

@Composable
fun Sdm3AdaptiveLayout(
    selectedTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit,
    showNav: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    BoxWithConstraints {
        val windowWidthSizeClass = when {
            maxWidth < 600.dp -> WindowWidthSizeClass.Compact
            maxWidth < 840.dp -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        }

        if (!showNav) {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { padding -> content(padding) }
            return@BoxWithConstraints
        }

        when (windowWidthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                Scaffold(
                    bottomBar = {
                        SDM3BottomNavBar(
                            currentTab = selectedTab,
                            onTabSelected = onTabSelected
                        )
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { padding -> content(padding) }
            }

            WindowWidthSizeClass.Medium -> {
                Row(Modifier.fillMaxSize()) {
                    Sdm3NavRail(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected
                    )
                    Scaffold(
                        modifier = Modifier.weight(1f),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { padding -> content(padding) }
                }
            }

            WindowWidthSizeClass.Expanded -> {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet {
                            Sdm3DrawerContent(
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected
                            )
                        }
                    }
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { padding -> content(padding) }
                }
            }
        }
    }
}

@Composable
private fun Sdm3NavRail(
    selectedTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp),
        color = Color.White.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        NavigationRail(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            containerColor = Color.Transparent,
            header = {
                Spacer(Modifier.height(8.dp))
            }
        ) {
            Spacer(Modifier.height(48.dp))
            navItems.forEach { item ->
                val selected = selectedTab == item.tab
                val contentColor by animateColorAsState(
                    targetValue = if (selected) Color.White else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    animationSpec = tween(400)
                )
                val backgroundColor by animateColorAsState(
                    targetValue = if (selected) Color(0xFF2E7D32) else Color.Transparent,
                    animationSpec = tween(400)
                )

                NavigationRailItem(
                    selected = selected,
                    onClick = { onTabSelected(item.tab) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(26.dp),
                                tint = contentColor
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.sp
                            ),
                            color = if (selected) Color(0xFF2E7D32) else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color(0xFF2E7D32),
                        selectedTextColor = Color(0xFF2E7D32),
                        unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(colorScheme.outlineVariant.copy(alpha = 0.3f))
    )
}

@Composable
private fun Sdm3DrawerContent(
    selectedTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = "SD Muhammadiyah 3",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = "Portal Orang Tua",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
        )

        Spacer(Modifier.height(32.dp))

        navItems.forEach { item ->
            val selected = selectedTab == item.tab
            val backgroundColor by animateColorAsState(
                targetValue = if (selected) Color(0xFF2E7D32).copy(alpha = 0.1f) else Color.Transparent,
                animationSpec = tween(400)
            )

            NavigationDrawerItem(
                selected = selected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Color(0xFF2E7D32) else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) Color(0xFF2E7D32) else colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = backgroundColor,
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
