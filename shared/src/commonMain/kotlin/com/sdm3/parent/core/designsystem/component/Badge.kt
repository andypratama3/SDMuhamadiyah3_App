package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Badge angka notifikasi (merah pada icon notifikasi, dll).
 * Menampilkan angka dengan background danger color.
 */
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99
) {
    if (count <= 0) return
    
    val displayText = if (count > maxCount) "${maxCount}+" else count.toString()
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = colorScheme.error,
                    shape = CircleShape
                )
                .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onError,
                fontSize = 10.sp
            )
        }
    }
}
