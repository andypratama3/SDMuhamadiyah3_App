package com.sdm3.parent.feature.profil.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanNotifikasiScreen(
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var masterToggle by remember { mutableStateOf(true) }
    var nilaiToggle by remember { mutableStateOf(true) }
    var tagihanToggle by remember { mutableStateOf(true) }
    var pengumumanToggle by remember { mutableStateOf(true) }
    var kehadiranToggle by remember { mutableStateOf(true) }
    var raporToggle by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan Notifikasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    ToggleRow(label = "Notifikasi Push", enabled = true, isOn = masterToggle, onToggle = { masterToggle = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    ToggleRow(label = "Nilai Baru", enabled = masterToggle, isOn = nilaiToggle, onToggle = { nilaiToggle = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    ToggleRow(label = "Tagihan Baru", enabled = masterToggle, isOn = tagihanToggle, onToggle = { tagihanToggle = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    ToggleRow(label = "Pengumuman Sekolah", enabled = masterToggle, isOn = pengumumanToggle, onToggle = { pengumumanToggle = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    ToggleRow(label = "Kehadiran", enabled = masterToggle, isOn = kehadiranToggle, onToggle = { kehadiranToggle = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    ToggleRow(label = "Rapor Tersedia", enabled = masterToggle, isOn = raporToggle, onToggle = { raporToggle = it })
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Nonaktifkan notifikasi untuk jenis informasi yang tidak ingin Anda terima.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
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
    val colorScheme = MaterialTheme.colorScheme
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
            color = if (enabled) colorScheme.onSurface else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isOn,
            onCheckedChange = { if (enabled) onToggle(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.surface,
                checkedTrackColor = colorScheme.secondary,
                uncheckedThumbColor = colorScheme.surface,
                uncheckedTrackColor = colorScheme.outlineVariant
            )
        )
    }
}
