package com.sdm3.parent.feature.rapor.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRaporScreen(
    studentId: String,
    onBack: () -> Unit,
    onPreviewClick: (String, String) -> Unit,
    onVerifikasiClick: (String) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    var startAnimation by remember { mutableStateOf(isPreview) }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            startAnimation = true
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dokumen Rapor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
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
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.xs)
        ) {
            item {
                Column {
                    Text(
                        text = "Rapor Digital Resmi",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Tahun Ajaran 2025/2026 · Semester Ganjil",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(colorScheme.primary, colorScheme.primary.copy(alpha = 0.8f))
                                    ),
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                                .padding(Spacing.xl)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = colorScheme.onPrimary.copy(alpha = 0.2f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Description, contentDescription = null, tint = colorScheme.onPrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(Spacing.md))
                                    Text(
                                        text = "Sumatif 1",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onPrimary
                                    )
                                }
                                StatusChip(text = "Tersedia", color = StatusSuccess)
                            }
                        }

                        Column(modifier = Modifier.padding(Spacing.xl)) {
                            Text(
                                text = "Diterbitkan pada 15 Desember 2025",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Oleh: Wali Kelas SDM3 Samarinda",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(Spacing.xl))

                            Sdm3Button(
                                text = "Unduh Rapor PDF",
                                onClick = { },
                                icon = Icons.Outlined.FileDownload,
                                containerColor = colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Sdm3OutlinedButton(
                                        text = "Lihat",
                                        onClick = { onPreviewClick("rapor_123", "https://example.com/rapor.pdf") },
                                        icon = Icons.Outlined.Visibility
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    Sdm3OutlinedButton(
                                        text = "Verif",
                                        onClick = { onVerifikasiClick("rapor_123") },
                                        icon = Icons.Outlined.QrCode
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.lg))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = StatusSuccess.copy(alpha = 0.05f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Spacing.md),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = StatusSuccess
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.sm))
                                    Text(
                                        text = "Tanda Tangan Elektronik Terverifikasi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Rapor Sebelumnya",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.md)
                )
            }

            val raporHistory = listOf(
                "Sumatif Tengah Semester / 2025-2026",
                "Sumatif Akhir Semester / 2024-2025",
                "Sumatif 2 / 2024-2025",
                "Sumatif 1 / 2024-2025"
            )

            itemsIndexed(raporHistory) { index, rapor ->
                MotionAnim(visible = startAnimation) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.xl),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.History, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rapor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = "Status: Tersimpan Permanen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.FileDownload, contentDescription = "Unduh", tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Preview
@Composable
private fun HalamanRaporScreenPreview() {
    SDM3Theme {
        HalamanRaporScreen(studentId = "", onBack = {}, onPreviewClick = { _, _ -> }, onVerifikasiClick = {})
    }
}
