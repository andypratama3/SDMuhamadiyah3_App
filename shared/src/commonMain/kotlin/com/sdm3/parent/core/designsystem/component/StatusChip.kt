package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.*

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(ChipShape)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}

fun statusColorForAttendance(status: String): Color = when (status.lowercase()) {
    "hadir", "present" -> StatusSuccess
    "sakit" -> StatusWarning
    "izin", "excused" -> Secondary
    "alpa", "absent" -> StatusDanger
    else -> StatusWarning
}

fun statusColorForPayment(status: String): Color = when (status.lowercase()) {
    "lunas", "success" -> StatusSuccess
    "belum_bayar", "pending" -> StatusDanger
    else -> StatusWarning
}
