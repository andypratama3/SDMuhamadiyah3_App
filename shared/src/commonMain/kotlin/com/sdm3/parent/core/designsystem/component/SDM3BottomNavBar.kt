package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer

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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .graphicsLayer {
                shadowElevation = 8f
                shape = NavBarShape
                clip = true
                ambientShadowColor = Color.Black.copy(alpha = 0.06f)
                spotShadowColor = Color.Black.copy(alpha = 0.1f)
            }
            .background(
                color = colorScheme.surface.copy(alpha = 0.92f),
                shape = NavBarShape
            )
            .drawBehind {
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.5f),
                    cornerRadius = CornerRadius(28.dp.toPx())
                )
            }
            .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentTab == item.tab
            val iconColor by animateColorAsState(
                targetValue = if (selected) colorScheme.primary
                             else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                animationSpec = tween(200),
                label = "iconColor"
            )
            val bgColor by animateColorAsState(
                targetValue = if (selected) colorScheme.primaryContainer
                             else Color.Transparent,
                animationSpec = tween(200),
                label = "bgColor"
            )

            Surface(
                onClick = { onTabSelected(item.tab) },
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                modifier = Modifier.height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = if (selected) Spacing.md else Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp),
                        tint = iconColor
                    )
                    if (selected) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = iconColor
                        )
                    }
                }
            }
        }
    }
}
