package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(EduOctoTheme.shapes.radiusMedium)
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = EduOctoTheme.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) { content() }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = EduOctoTheme.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) { content() }
    }
}

@Preview
@Composable
private fun EduOctoCardPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Column(Modifier.padding(16.dp)) {
            EduOctoCard {
                Column(Modifier.padding(16.dp)) {
                    androidx.compose.material3.Text("Judul Card", style = EduOctoTheme.typography.titleMedium)
                    androidx.compose.material3.Text("Ini adalah contoh konten card. EduOctoCard menggunakan radiusMedium (16dp) dan elevasi 1dp.", style = EduOctoTheme.typography.bodyMedium, color = EduOctoTheme.colors.onSurfaceMuted)
                }
            }
        }
    }
}
