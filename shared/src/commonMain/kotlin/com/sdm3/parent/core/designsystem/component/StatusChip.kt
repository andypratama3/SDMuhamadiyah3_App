package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = color
    )
}

fun statusColorForAttendance(status: String): Color = when (status.lowercase()) {
    "hadir", "present" -> StatusSuccess
    "sakit" -> StatusWarning
    "izin", "excused" -> com.sdm3.parent.core.designsystem.theme.Secondary
    "alpa", "absent" -> StatusDanger
    else -> StatusWarning
}

fun statusColorForPayment(status: String): Color = when (status.lowercase()) {
    "lunas", "success" -> StatusSuccess
    "belum_bayar", "pending" -> StatusDanger
    else -> StatusWarning
}
