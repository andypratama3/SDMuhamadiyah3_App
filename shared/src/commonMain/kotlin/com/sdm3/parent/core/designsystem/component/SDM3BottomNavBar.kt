package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.NavBarShape
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.navigation.SDM3BottomTab

data class BottomNavItem(
    val label: String,
    val tab: SDM3BottomTab,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Beranda", SDM3BottomTab.Beranda, Icons.Outlined.Home),
    BottomNavItem("Nilai", SDM3BottomTab.Nilai, Icons.Outlined.School),
    BottomNavItem("Bayar", SDM3BottomTab.Bayar, Icons.Outlined.CreditCard),
    BottomNavItem("Rapor", SDM3BottomTab.Rapor, Icons.Outlined.Description),
    BottomNavItem("Profil", SDM3BottomTab.Profil, Icons.Outlined.AccountCircle)
)

@Composable
fun SDM3BottomNavBar(
    currentTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // Floating Island Navigation (2026 Trend)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .height(64.dp)
                .graphicsLayer {
                    shadowElevation = 6f
                    shape = RoundedCornerShape(24.dp)
                    clip = true
                    ambientShadowColor = Color.Black.copy(alpha = 0.04f)
                    spotShadowColor = Color.Black.copy(alpha = 0.04f)
                },
            color = colorScheme.surface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentTab == item.tab
                    
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        animationSpec = tween(300)
                    )

                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            .background(
                                if (selected) colorScheme.primary.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(item.tab) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = if (selected) 16.dp else 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp),
                                tint = contentColor
                            )
                            
                            if (selected) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = contentColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
