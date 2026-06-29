package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme
import com.eduocto.designsystem.components.StatusPillColor.Success
import com.eduocto.designsystem.components.StatusPillColor.Danger

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
    trendUp: Boolean? = null,
    loading: Boolean = false,
) {
    if (loading) {
        EduOctoSkeletonCard(modifier = modifier)
        return
    }
    val colors = EduOctoTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, style = EduOctoTheme.typography.metadata, color = colors.onSurfaceMuted)
        Spacer(Modifier.height(4.dp))
        Row {
            Text(value, style = EduOctoTheme.typography.displayMedium, color = colors.onSurface)
            if (trend != null && trendUp != null) {
                Spacer(Modifier.padding(start = 8.dp))
                Text(
                    trend,
                    style = EduOctoTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = if (trendUp) colors.success else colors.danger,
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatTilePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)) {
            StatTile(label = "Total Siswa", value = "432")
            StatTile(label = "Kehadiran Hari Ini", value = "98%", trend = "+2%", trendUp = true)
            StatTile(label = "SPP Tertunggak", value = "Rp 4.5jt", trend = "↑12%", trendUp = false)
        }
    }
}
