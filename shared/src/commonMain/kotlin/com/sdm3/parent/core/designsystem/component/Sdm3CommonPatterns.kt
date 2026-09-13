package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme

/**
 * Inline error banner shown inside forms (login, OTP, etc.).
 * Eliminates 4+ identical error box implementations.
 */
@Composable
fun Sdm3ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colorScheme.error),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = "Error",
                tint = colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 * Category badge pill (uppercase text in rounded surface).
 * Eliminates duplicate badge pill implementations with proper WCAG AAA color contrast.
 */
@Composable
fun Sdm3CategoryBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = " ${text.trim()} ",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Glass icon container for logos on auth screens.
 * Eliminates 3 identical glass container implementations.
 */
@Composable
fun Sdm3GlassIconContainer(
    size: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme.colors
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape((size.value * 0.255).dp),
        color = colors.liquidGlassSurface,
        border = BorderStroke(2.dp, colors.liquidGlassBorder),
        shadowElevation = 8.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * Icon-in-surface badge used for list items, notifications, settings, etc.
 * Eliminates 6+ identical icon container implementations.
 */
@Composable
fun Sdm3IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconTint,
            )
        }
    }
}

/**
 * Info row with label on left and value on right.
 * Used in payment receipts, detail screens, etc.
 */
@Composable
fun Sdm3InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: Color = ProductSchoolTheme.colors.onSurfaceMuted,
    valueColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = labelColor,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}
