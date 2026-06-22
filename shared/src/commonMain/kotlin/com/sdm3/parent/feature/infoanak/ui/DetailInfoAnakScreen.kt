package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoAnakScreen(
    studentId: String,
    onBack: () -> Unit,
    onQuickNavClick: (SDM3Route) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Peserta Didik",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(88.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "AF",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Text(
                            text = "Ahmad Fathan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Kelas 4-A - Ibnu Sina",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            StatusChip(text = "NISN: 0012345678", color = colorScheme.primary)
                            StatusChip(text = "Aktif", color = StatusSuccess)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Biodata Siswa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.md)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        InfoRow("Tempat Lahir", "Samarinda")
                        InfoRow("Tanggal Lahir", "15 Januari 2015")
                        InfoRow("Wali Kelas", "Ibu Siti Rahmawati, S.Pd.")
                        InfoRow("ID Pelajar", "SDM3-2025-00123")
                    }
                }
            }

            item {
                Text(
                    text = "Navigasi Akademik",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.md)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    QuickNavItem(
                        modifier = Modifier.weight(1f),
                        title = "Nilai",
                        icon = Icons.Outlined.Assessment,
                        color = colorScheme.secondary,
                        onClick = { onQuickNavClick(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                    )
                    QuickNavItem(
                        modifier = Modifier.weight(1f),
                        title = "Rapor",
                        icon = Icons.Outlined.Description,
                        color = colorScheme.primary,
                        onClick = { onQuickNavClick(SDM3Route.HalamanRapor(studentId)) }
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    QuickNavItem(
                        modifier = Modifier.weight(1f),
                        title = "Hadir",
                        icon = Icons.Outlined.EventAvailable,
                        color = StatusWarning,
                        onClick = { onQuickNavClick(SDM3Route.KehadiranSiswa(studentId)) }
                    )
                    QuickNavItem(
                        modifier = Modifier.weight(1f),
                        title = "SPP",
                        icon = Icons.Outlined.Payments,
                        color = colorScheme.error,
                        onClick = { onQuickNavClick(SDM3Route.PembayaranSpp(studentId)) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
    }
}

@Composable
private fun QuickNavItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        }
    }
}
