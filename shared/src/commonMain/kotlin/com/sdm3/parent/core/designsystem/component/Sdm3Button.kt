package com.sdm3.parent.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.ButtonShape
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing

@Composable
fun Sdm3Button(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    // EduOcto Primary Action: Gold Container with Navy Text
    val finalContainerColor = if (containerColor != Color.Unspecified) containerColor else MaterialTheme.colorScheme.secondary
    val finalContentColor = if (contentColor != Color.Unspecified) contentColor else MaterialTheme.colorScheme.primary

    val isPreview = LocalInspectionMode.current
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isPreview) 0.96f else 1f,
        animationSpec = tween(150),
        label = "buttonScale",
        finishedListener = { _ -> if (!isPreview) isPressed = false }
    )

    Button(
        onClick = {
            if (!isPreview) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isPressed = true
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Premium height
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled && !isLoading,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = finalContainerColor,
            contentColor = finalContentColor,
            disabledContainerColor = finalContainerColor.copy(alpha = 0.3f),
            disabledContentColor = finalContentColor.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        contentPadding = PaddingValues(horizontal = if (icon != null) 8.dp else 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = finalContentColor,
                strokeWidth = 3.dp
            )
        } else if (content != null) {
            content()
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                )
                
                if (icon != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    // EduOcto Island Icon Architecture
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(finalContentColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = finalContentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Sdm3OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified
) {
    val colorScheme = MaterialTheme.colorScheme
    val finalContentColor = if (contentColor != Color.Unspecified) contentColor else colorScheme.primary
    val haptic = LocalHapticFeedback.current
    
    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = ButtonShape,
        border = BorderStroke(
            1.5.dp,
            finalContentColor.copy(alpha = 0.2f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = finalContentColor
        ),
        contentPadding = PaddingValues(horizontal = if (icon != null) 8.dp else 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            
            if (icon != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(finalContentColor.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp),
                        tint = finalContentColor
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Sdm3ButtonPreview() {
    SDM3Theme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Sdm3Button(text = "Primary Button", onClick = {})
            Sdm3Button(text = "Loading Button", onClick = {}, isLoading = true)
            Sdm3OutlinedButton(text = "Outlined Button", onClick = {})
        }
    }
}
