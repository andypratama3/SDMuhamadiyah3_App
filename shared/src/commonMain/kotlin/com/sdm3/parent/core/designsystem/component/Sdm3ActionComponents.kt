package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.statusSuccessColor
import com.sdm3.parent.core.designsystem.theme.statusWarningColor
import com.sdm3.parent.core.designsystem.theme.statusDangerColor
import com.sdm3.parent.core.designsystem.theme.statusInfoColor

/**
 * A floating action button with premium styling and haptic feedback.
 */
@Composable
fun Sdm3FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 56.dp,
    expanded: Boolean = false,
    label: String? = null
) {
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isPreview) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabScale"
    )
    
    val colorScheme = MaterialTheme.colorScheme
    
    if (label != null && expanded) {
        ExtendedFloatingActionButton(
            onClick = {
                if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp)
                )
            },
            text = {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            },
            containerColor = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            )
        )
    } else {
        FloatingActionButton(
            onClick = {
                if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier
                .size(size)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            containerColor = containerColor,
            contentColor = contentColor,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}

/**
 * A chip-based action button for inline actions.
 */
@Composable
fun Sdm3ActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(enabled = enabled, onClick = {
                if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }),
        color = if (selected) contentColor.copy(alpha = 0.15f) else containerColor,
        shape = RoundedCornerShape(999.dp),
        border = if (selected) BorderStroke(1.5.dp, contentColor) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (enabled) contentColor else contentColor.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * A segmented control for switching between multiple options.
 */
@Composable
fun Sdm3SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    
    Surface(
        modifier = modifier,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                val selected = selectedIndex == index
                val backgroundColor by animateColorAsState(
                    targetValue = if (selected) colorScheme.primary else Color.Transparent,
                    animationSpec = tween(300),
                    label = "segmentBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (selected) colorScheme.onPrimary else colorScheme.primary.copy(alpha = 0.7f),
                    animationSpec = tween(300),
                    label = "segmentText"
                )
                
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOptionSelected(index)
                        },
                    color = backgroundColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * A confirmation modal for destructive actions.
 */
@Composable
fun Sdm3ConfirmModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    confirmLabel: String = "Konfirmasi",
    dismissLabel: String = "Batal",
    isDestructive: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(24.dp),
            containerColor = colorScheme.surface,
            icon = {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = if (isDestructive) colorScheme.error.copy(alpha = 0.1f) else colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDestructive) Icons.Outlined.Warning else Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (isDestructive) colorScheme.error else colorScheme.primary
                        )
                    }
                }
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Sdm3Button(
                    text = confirmLabel,
                    onClick = {
                        if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onConfirm()
                    },
                    containerColor = if (isDestructive) colorScheme.error else colorScheme.primary,
                    contentColor = if (isDestructive) colorScheme.onError else colorScheme.onPrimary,
                    modifier = Modifier.widthIn(min = 120.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = dismissLabel,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        )
    }
}

/**
 * A quick action tile for dashboard-style screens.
 */
@Composable
fun Sdm3QuickAction(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badge: String? = null,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    
    Sdm3Card(
        modifier = modifier.clickable {
            if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        padding = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = iconBackgroundColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = iconTint
                        )
                    }
                }
                if (badge != null) {
                    NotificationBadge(
                        count = badge.toIntOrNull() ?: 0,
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = colorScheme.primary.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Sdm3ActionComponentsPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Sdm3ActionChip(
                    label = "Filter",
                    icon = Icons.Outlined.FilterList,
                    onClick = {}
                )
                Sdm3ActionChip(
                    label = "Terbaru",
                    selected = true,
                    onClick = {}
                )
            }
            
            Sdm3SegmentedControl(
                options = listOf("Hari", "Minggu", "Bulan"),
                selectedIndex = 0,
                onOptionSelected = {}
            )
            
            Sdm3QuickAction(
                title = "Pembayaran",
                icon = Icons.Outlined.Payments,
                subtitle = "3 tagihan aktif",
                badge = "3",
                onClick = {}
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Sdm3FloatingActionButton(
                    icon = Icons.Outlined.Add,
                    onClick = {},
                    size = 48.dp
                )
                Sdm3FloatingActionButton(
                    icon = Icons.Outlined.Edit,
                    onClick = {},
                    size = 48.dp,
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
