package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.glassSurfaceColor
import com.sdm3.parent.core.designsystem.theme.glassBorderColor

@Composable
fun Sdm3ListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    showChevron: Boolean = false,
    backgroundColor: Color = Color.Transparent,
) {
    val alpha = if (enabled) 1f else 0.4f
    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isPreview) 0.98f else 1f,
        animationSpec = tween(100),
        label = "listItemScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (backgroundColor != Color.Transparent) {
                    Modifier.background(backgroundColor, RoundedCornerShape(12.dp))
                } else Modifier
            )
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    )
                } else Modifier
            )
            .padding(
                horizontal = ProductSchoolTheme.spacing.md,
                vertical = ProductSchoolTheme.spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(ProductSchoolTheme.spacing.sm))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ProductSchoolTheme.typography.bodyMedium,
                color = ProductSchoolTheme.colors.onSurface.copy(alpha = alpha),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!supportingText.isNullOrEmpty()) {
                Text(
                    text = supportingText,
                    style = ProductSchoolTheme.typography.metadata,
                    color = ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = alpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = ProductSchoolTheme.typography.bodySmall,
                    color = ProductSchoolTheme.colors.onSurfaceVariant.copy(alpha = alpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(ProductSchoolTheme.spacing.sm))
            trailing()
        }
        if (showChevron && enabled) {
            Spacer(Modifier.width(ProductSchoolTheme.spacing.xs))
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = 0.4f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

/**
 * A divider component with optional label for section separation.
 */
@Composable
fun Sdm3ListDivider(
    modifier: Modifier = Modifier,
    label: String? = null,
    startIndent: androidx.compose.ui.unit.Dp = 0.dp
) {
    if (label != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = ProductSchoolTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .padding(start = startIndent)
                    .background(
                        ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = 0.1f),
                        RoundedCornerShape(1.dp)
                    )
            )
            Text(
                text = label.uppercase(),
                style = ProductSchoolTheme.typography.metadata,
                color = ProductSchoolTheme.colors.onSurfaceMuted,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = ProductSchoolTheme.spacing.md)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = 0.1f),
                        RoundedCornerShape(1.dp)
                    )
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = startIndent)
                .background(
                    ProductSchoolTheme.colors.onSurfaceMuted.copy(alpha = 0.08f),
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

@Preview
@Composable
private fun Sdm3ListItemPreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            Sdm3ListItem(
                title = "John Doe",
                supportingText = "Kelas 6A · NISN 1234567890",
                onClick = {},
                showChevron = true,
            )
            Sdm3ListDivider(label = "Section")
            Sdm3ListItem(
                title = "Disabled Item",
                supportingText = "This item is disabled",
                enabled = false,
            )
            Sdm3ListDivider()
            Sdm3ListItem(
                title = "With Leading",
                supportingText = "Leading slot active",
                leading = {
                    Text("⭐", style = MaterialTheme.typography.titleMedium)
                },
                trailing = {
                    Text(">", style = MaterialTheme.typography.titleMedium)
                },
                onClick = {},
            )
        }
    }
}
