package com.sdm3.parent.feature.nilai.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRaporScreen(studentId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rapor Resmi") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Text(
                    text = "Tahun Ajaran 2025/2026",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = SchoolGreenDark
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(Spacing.md),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Sumatif 1 / 2025-2026",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SurfaceWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusChip(text = "Tersedia", color = StatusSuccess)
                                }
                            }

                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "Disetujui: 15 Desember 2025",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))

                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text("Unduh PDF", fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(Spacing.sm))

                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text("Lihat Online")
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = StatusSuccess
                                )
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = "Dokumen ini telah ditandatangani secara elektronik",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Rapor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val raporHistory = listOf(
                "Sumatif 1 / 2025-2026",
                "Sumatif Tengah Semester / 2025-2026",
                "Sumatif Akhir Semester / 2024-2025",
                "Sumatif 2 / 2024-2025"
            )

            items(raporHistory) { rapor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = rapor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tersedia",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusSuccess
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Unduh rapor")
                        }
                    }
                }
            }
        }
    }
}
