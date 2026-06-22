package com.sdm3.parent.feature.nilai

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import org.koin.compose.viewmodel.koinViewModel

private val tabLabels = listOf("Sumatif", "Formatif", "Projek")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiRaporScreen(
    studentId: String,
    semester: String,
    onBack: (() -> Unit)? = null,
    onDetailMapel: ((subjectId: String) -> Unit)? = null,
    viewModel: NilaiRaporViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(studentId) {
        viewModel.loadGrades(studentId, semester)
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nilai & Rapor",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Semester $semester",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(100),
                color = colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(100))
                                .background(if (isSelected) colorScheme.surface else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tabContent"
            ) { targetTab ->
                when (targetTab) {
                    0 -> SumatifTabContent(studentId, semester, onDetailMapel)
                    1 -> FormatifTabContent(studentId)
                    2 -> ProjekTabContent(studentId)
                }
            }
        }
    }
}

@Composable
private fun SumatifTabContent(
    studentId: String,
    semester: String,
    onDetailMapel: ((subjectId: String) -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.primary,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rata-rata Nilai",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                    Text(
                        text = "$avgScore",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                    Text(
                        text = "Semester $semester",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Daftar Mata Pelajaran",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(top = Spacing.md)
            )
        }

        items(subjects) { subject ->
            val scoreColor = when {
                subject.score >= 90 -> StatusSuccess
                subject.score >= 75 -> StatusWarning
                else -> StatusDanger
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailMapel?.invoke(subject.id) },
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        StatusChip(
                            text = "${subject.predicate} - ${subject.description}",
                            color = scoreColor
                        )
                    }
                    Text(
                        text = "${subject.score}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.xxxl)) }
    }
}

@Composable
private fun FormatifTabContent(studentId: String) {
    val colorScheme = MaterialTheme.colorScheme
    val tpData = listOf(
        TPItem("3.1", "Menjelaskan operasi hitung bilangan cacah", 85),
        TPItem("3.2", "Menyelesaikan soal cerita penjumlahan", 90),
        TPItem("3.3", "Mengidentifikasi sifat-sifat bangun datar", 78),
        TPItem("3.4", "Menghitung keliling bangun datar", 72),
        TPItem("3.5", "Menyelesaikan masalah kontekstual", 88)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = colorScheme.secondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "Nilai Formatif mengukur ketercapaian per Tujuan Pembelajaran (TP) sebagai evaluasi harian berkelanjutan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        items(tpData) { tp ->
            val tpColor = when {
                tp.score >= 90 -> StatusSuccess
                tp.score >= 75 -> StatusWarning
                else -> StatusDanger
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = colorScheme.secondary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tp.code,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.secondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tp.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "${tp.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tpColor
                    )
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.xxxl)) }
    }
}

@Composable
private fun ProjekTabContent(studentId: String) {
    val colorScheme = MaterialTheme.colorScheme
    val proyek = listOf(
        ProjekItem("Gaya Hidup Berkelanjutan", "Membuat poster daur ulang dari limbah rumah tangga", 88, "A"),
        ProjekItem("Kearifan Lokal", "Pameran budaya daerah Kalimantan Timur", 92, "A"),
        ProjekItem("Bangunlah Jiwa Raganya", "Senam pagi bersama dan kampanye anti-bullying", 85, "B+")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        item {
            Text(
                text = "Projek Penguatan Profil Pelajar Pancasila (P5)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        }
        items(proyek) { proyekItem ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proyekItem.tema,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        StatusChip(
                            text = "${proyekItem.nilai} (${proyekItem.predikat})",
                            color = StatusSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = proyekItem.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.xxxl)) }
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
