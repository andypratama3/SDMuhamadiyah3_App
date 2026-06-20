package com.sdm3.parent.feature.nilai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.SecondaryContainer
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.SurfaceContainerLow
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

private val tabs = listOf("Sumatif", "Formatif", "Projek")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiRaporScreen(
    studentId: String,
    semester: String,
    onBack: (() -> Unit)? = null,
    onDetailMapel: ((subjectId: String) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nilai & Rapor") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Text("←")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(modifier = Modifier.height(Spacing.sm))

            when (selectedTab) {
                0 -> SumatifTab(studentId = studentId, semester = semester, onDetailMapel = onDetailMapel)
                1 -> FormatifTab(studentId = studentId)
                2 -> ProjekTab(studentId = studentId)
            }
        }
    }
}

@Composable
private fun TabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        tabs.forEachIndexed { index, label ->
            FilterChip(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                label = {
                    Text(
                        label,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SurfaceWhite,
                    selectedLabelColor = Primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (selectedTab == index) Primary else MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Primary,
                    enabled = true,
                    selected = selectedTab == index
                )
            )
        }
    }
}

@Composable
private fun SumatifTab(
    studentId: String,
    semester: String,
    onDetailMapel: ((subjectId: String) -> Unit)?
) {
    val subjects = listOf(
        SubjectGrade("Matematika", 92, "A", "SANGAT BAIK", "1"),
        SubjectGrade("Bahasa Indonesia", 88, "B+", "BAIK", "2"),
        SubjectGrade("IPA", 95, "A", "SANGAT BAIK", "3"),
        SubjectGrade("IPS", 78, "B", "CUKUP", "4"),
        SubjectGrade("Pend. Agama", 90, "A", "SANGAT BAIK", "5"),
        SubjectGrade("PJOK", 85, "B+", "BAIK", "6"),
        SubjectGrade("Seni Budaya", 82, "B", "BAIK", "7"),
        SubjectGrade("Bahasa Inggris", 76, "B", "CUKUP", "8")
    )

    val avgScore = subjects.map { it.score }.average().toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rata-Rata Nilai",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "$avgScore",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = "Semester $semester",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Daftar Mata Pelajaran",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onDetailMapel?.invoke(subject.id) }
                )
            }
        }
    }
}

@Composable
private fun FormatifTab(studentId: String) {
    val tpData = listOf(
        TPItem("3.1", "Menjelaskan operasi hitung bilangan cacah", 85),
        TPItem("3.2", "Menyelesaikan soal cerita penjumlahan", 90),
        TPItem("3.3", "Mengidentifikasi sifat-sifat bangun datar", 78),
        TPItem("3.4", "Menghitung keliling bangun datar", 72),
        TPItem("3.5", "Menyelesaikan masalah kontekstual", 88)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SecondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Nilai Formatif — Penilaian harian per Tujuan Pembelajaran (TP)",
                    modifier = Modifier.padding(Spacing.md),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        items(tpData) { tp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tp.code,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tp.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    val tpColor = when {
                        tp.score >= 90 -> StatusSuccess
                        tp.score >= 75 -> StatusWarning
                        else -> StatusDanger
                    }
                    StatusChip(
                        text = "${tp.score}",
                        color = tpColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjekTab(studentId: String) {
    val proyek = listOf(
        ProjekItem("Gaya Hidup Berkelanjutan", "Membuat poster daur ulang", 88, "A"),
        ProjekItem("Kearifan Lokal", "Pameran budaya daerah", 92, "A"),
        ProjekItem("Bangunlah Jiwa Raganya", "Senam pagi bersama", 85, "B+")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        item {
            Text(
                text = "Projek Penguatan Profil Pelajar Pancasila (P5)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }
        items(proyek) { proyekItem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proyekItem.tema,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        StatusChip(
                            text = "${proyekItem.nilai} (${proyekItem.predikat})",
                            color = StatusSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = proyekItem.deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

data class SubjectGrade(
    val name: String,
    val score: Int,
    val predicate: String,
    val description: String,
    val id: String
)

data class TPItem(val code: String, val description: String, val score: Int)

data class ProjekItem(val tema: String, val deskripsi: String, val nilai: Int, val predikat: String)

@Composable
private fun SubjectCard(
    subject: SubjectGrade,
    onClick: () -> Unit
) {
    val scoreColor = when {
        subject.score >= 90 -> StatusSuccess
        subject.score >= 75 -> StatusWarning
        else -> StatusDanger
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    text = subject.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(
                    text = "${subject.predicate} — ${subject.description}",
                    color = scoreColor
                )
            }
            Text(
                text = "${subject.score}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
        }
    }
}
