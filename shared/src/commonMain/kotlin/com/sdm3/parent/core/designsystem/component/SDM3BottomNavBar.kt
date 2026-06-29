package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.navigation.SDM3BottomTab

@Composable
fun SDM3BottomNavBar(
    currentTab: SDM3BottomTab?,
    onTabSelected: (SDM3BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // ProductSchool Floating Glass Navigation
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .graphicsLayer {
                    shadowElevation = 24f
                    shape = RoundedCornerShape(36.dp)
                    clip = true
                    ambientShadowColor = colorScheme.primary.copy(alpha = 0.1f)
                    spotShadowColor = colorScheme.primary.copy(alpha = 0.1f)
                },
            color = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(36.dp),
            border = BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.6f)
            )
        ) {
            // Backdrop Blur Effect (Glassmorphism)
            Box(modifier = Modifier.fillMaxSize().blur(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val selected = currentTab == item.tab
                    
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) Color.White else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        animationSpec = tween(400)
                    )

                    val backgroundColor by animateColorAsState(
                        targetValue = if (selected) Color(0xFF2E7D32) else Color.Transparent, // Using Green for Home as per image
                        animationSpec = tween(400)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(item.tab) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.sp
                            ),
                            color = if (selected) Color(0xFF2E7D32) else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SDM3BottomNavBarPreview() {
    SDM3Theme {
        SDM3BottomNavBar(
            currentTab = SDM3BottomTab.Beranda,
            onTabSelected = {}
        )
    }
}
