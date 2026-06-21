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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceContainerLow
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite
import com.sdm3.parent.feature.kegiatan.KegiatanProgramViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanProgramScreen(
    studentId: String,
    onBack: () -> Unit = {},
    viewModel: KegiatanProgramViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ekstrakurikuler", "Program Unggulan")

    LaunchedEffect(studentId) {
        viewModel.loadActivities(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kegiatan & Program") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Eksplorasi & Bakat",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = "Pantau perkembangan minat dan program keagamaan ananda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (isSelected) Modifier
                                        .background(Primary, RoundedCornerShape(10.dp))
                                    else Modifier
                                )
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                if (uiState.extracurriculars.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.SportsSoccer,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = OnSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(Spacing.md))
                            Text(
                                "Belum ada data ekstrakurikuler",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                items(uiState.extracurriculars) { ekskul ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Secondary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.SportsSoccer,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = Primary
                                )
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
                                    StatusChip(text = ekskul.schedule ?: "-", color = StatusSuccess)
                                }
                                Text(
                                    text = ekskul.teacherName ?: "Pembina",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = "\"${ekskul.description ?: "Belum ada catatan"}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                val programList = listOf(
                    ProgramItem(
                        "Tahfiz Quran",
                        "Hafalan Juz 30",
                        15, 37,
                        "Ustadz Hafidz",
                        "Surat An-Nas — Selesai"
                    ),
                    ProgramItem(
                        "Sholat Berjamaah",
                        "Kehadiran Jamaah",
                        22, 25,
                        "Pembina Rohis",
                        "Sholat Dzuhur berjamaah"
                    ),
                    ProgramItem(
                        "Bahasa Arab Dasar",
                        "Level 1",
                        85, 100,
                        "Ustadzah Fatimah",
                        "Percakapan sehari-hari"
                    )
                )

                items(programList) { program ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = Primary
                                    )
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
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Text(
                                text = program.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(Spacing.sm))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(12.dp)
                                        .background(Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .background(Primary, RoundedCornerShape(6.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = "${program.progress}/${program.target}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.sm))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(Spacing.sm))

                            Text(
                                text = "Terakhir: ${program.lastActivity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
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
