package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanProgramScreen(
    studentId: String,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ekstrakurikuler", "Program Unggulan")

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kegiatan & Program",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(vertical = Spacing.md)
        ) {
            item {
                Text(
                    text = "Eksplorasi & Bakat",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Pantau perkembangan minat dan program keagamaan ananda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.xs),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        tabs.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(
                                        if (isSelected) Modifier.background(colorScheme.primary, RoundedCornerShape(10.dp))
                                        else Modifier
                                    )
                                    .padding(vertical = Spacing.sm),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                val ekskulList = listOf(
                    EkskulItem("Futsal", "Coach Ahmad", "A", "Ananda menunjukkan peningkatan signifikan dalam kerjasama tim."),
                    EkskulItem("Tahfiz Quran", "Ustadz Hafidz", "B+", "Hafalan juz 30: 15 surat telah dikuasai dengan baik."),
                    EkskulItem("Seni Tari", "Ibu Dewi", "A", "Kreatif dan percaya diri saat tampil."),
                    EkskulItem("Pramuka", "Kak Rudi", "B", "Perlu ditingkatkan kedisiplinannya.")
                )

                items(ekskulList) { ekskul ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        color = colorScheme.surface,
                        tonalElevation = 1.dp,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = colorScheme.secondaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.SportsSoccer,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ekskul.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    StatusChip(text = ekskul.grade, color = StatusSuccess)
                                }
                                Text(
                                    text = ekskul.coach,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = "\"${ekskul.note}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                val programList = listOf(
                    ProgramItem("Tahfiz Quran", "Hafalan Juz 30", 15, 37, "Ustadz Hafidz", "Surat An-Nas - Selesai"),
                    ProgramItem("Sholat Berjamaah", "Kehadiran Jamaah", 22, 25, "Pembina Rohis", "Sholat Dzuhur berjamaah"),
                    ProgramItem("Bahasa Arab Dasar", "Level 1", 85, 100, "Ustadzah Fatimah", "Percakapan sehari-hari")
                )

                items(programList) { program ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        color = colorScheme.surface,
                        tonalElevation = 1.dp,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(Spacing.md))
                                Column {
                                    Text(
                                        text = program.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = program.coach,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Text(
                                text = program.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(Spacing.sm))

                            val progressFraction = (program.progress.toFloat() / program.target.toFloat()).coerceIn(0f, 1f)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier.weight(1f).height(12.dp),
                                    color = colorScheme.primary,
                                    trackColor = colorScheme.outlineVariant
                                )
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = "${program.progress}/${program.target}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.sm))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(Spacing.sm))

                            Text(
                                text = "Terakhir: ${program.lastActivity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

data class EkskulItem(val name: String, val coach: String, val grade: String, val note: String)

data class ProgramItem(
    val name: String,
    val subtitle: String,
    val progress: Int,
    val target: Int,
    val coach: String,
    val lastActivity: String
)
