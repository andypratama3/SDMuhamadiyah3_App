package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

data class QuickNav(
    val title: String,
    val emoji: String,
    val color: Color
)

private val quickNavItems = listOf(
    QuickNav("Nilai", "📊", Secondary),
    QuickNav("Rapor", "📄", Primary),
    QuickNav("Kehadiran", "📅", StatusWarning),
    QuickNav("Kesehatan", "💊", SchoolGreenDark)
)

data class InfoItem(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoAnakScreen(studentId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Anak") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("←")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Text("↗️")
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AF",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Text(
                            text = "Ahmad Fathan",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Kelas 4-A (Ibnu Sina)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary
                        )

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            StatusChip(text = "NISN: 0012345678", color = Primary)
                            StatusChip(text = "Aktif", color = StatusSuccess)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        val infoItems = listOf(
                            InfoItem("Tempat Lahir", "Samarinda"),
                            InfoItem("Tanggal Lahir", "15 Januari 2015"),
                            InfoItem("Jenis Kelamin", "Laki-laki"),
                            InfoItem("Wali Kelas", "Ibu Siti Rahmawati, S.Pd."),
                            InfoItem("ID Pelajar", "SDM3-2025-00123")
                        )

                        infoItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Navigasi Cepat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        quickNavItems.take(2).forEach { nav ->
                            NavItemCard(nav = nav, modifier = Modifier.weight(1f))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        quickNavItems.drop(2).forEach { nav ->
                            NavItemCard(nav = nav, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Primary,
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(Spacing.md)
                    ) {
                        Text(
                            text = "Ringkasan Akademik",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SurfaceWhite
                        )
                    }
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "Fase B — Merdeka Belajar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Rata-rata Nilai",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = "86",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                                StatusChip(text = "A — SANGAT BAIK", color = StatusSuccess)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Mata Pelajaran",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = "8 Mapel",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        StatusChip(text = "Naik Kelas", color = StatusSuccess)
                    }
                }
            }
        }
    }
}

@Composable
private fun NavItemCard(nav: QuickNav, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = nav.color)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(nav.emoji, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = nav.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = nav.color
            )
        }
    }
}
