package com.sdm3.parent.feature.profil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

data class NotifToggle(
    val label: String,
    val emoji: String,
    val isEnabled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanNotifikasiScreen() {
    var masterToggle by remember { mutableStateOf(true) }
    var nilaiToggle by remember { mutableStateOf(true) }
    var tagihanToggle by remember { mutableStateOf(true) }
    var pengumumanToggle by remember { mutableStateOf(true) }
    var kehadiranToggle by remember { mutableStateOf(true) }
    var raporToggle by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Notifikasi") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    ToggleRow(
                        label = "Notifikasi Push",
                        enabled = true,
                        isOn = masterToggle,
                        onToggle = { masterToggle = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

                    ToggleRow(
                        label = "Nilai Baru",
                        enabled = masterToggle,
                        isOn = nilaiToggle,
                        onToggle = { nilaiToggle = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

                    ToggleRow(
                        label = "Tagihan Baru",
                        enabled = masterToggle,
                        isOn = tagihanToggle,
                        onToggle = { tagihanToggle = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

                    ToggleRow(
                        label = "Pengumuman Sekolah",
                        enabled = masterToggle,
                        isOn = pengumumanToggle,
                        onToggle = { pengumumanToggle = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

                    ToggleRow(
                        label = "Kehadiran",
                        enabled = masterToggle,
                        isOn = kehadiranToggle,
                        onToggle = { kehadiranToggle = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

                    ToggleRow(
                        label = "Rapor Tersedia",
                        enabled = masterToggle,
                        isOn = raporToggle,
                        onToggle = { raporToggle = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Nonaktifkan notifikasi untuk jenis informasi yang tidak ingin Anda terima.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    enabled: Boolean,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else OnSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isOn,
            onCheckedChange = { if (enabled) onToggle(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = Secondary,
                uncheckedThumbColor = SurfaceWhite,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}
