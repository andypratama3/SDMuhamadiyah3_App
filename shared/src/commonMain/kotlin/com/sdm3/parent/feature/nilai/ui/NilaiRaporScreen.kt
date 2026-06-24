package com.sdm3.parent.feature.nilai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.nilai.NilaiRaporViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.tooling.preview.Preview

private val tabLabels = listOf("Sumatif", "Formatif", "Projek")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiRaporScreen(
    studentId: String,
    semester: String,
    onBack: (() -> Unit)? = null,
    onDetailMapel: ((subjectId: String) -> Unit)? = null,
    viewModel: NilaiRaporViewModel? = if (LocalInspectionMode.current) null else koinViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme

    if (!LocalInspectionMode.current) {
        LaunchedEffect(studentId) {
            viewModel?.loadGrades(studentId, semester)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primary.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Analytics, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(Modifier.width(Spacing.md))
                        Column {
                            Text(
                                text = "Nilai & Rapor",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Semester $semester",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
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
                shape = ChipShape,
                color = colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(ChipShape)
                                .background(if (isSelected) colorScheme.surface else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(250))
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
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rata-rata Nilai",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "$avgScore",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                    Text(
                        text = "Dari ${subjects.size} mata pelajaran",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    label = "Tertinggi",
                    value = "${subjects.maxOf { it.score }}",
                    color = StatusSuccess
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Outlined.TrendingDown,
                    label = "Terendah",
                    value = "${subjects.minOf { it.score }}",
                    color = StatusWarning
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CheckCircle,
                    label = "Mapel",
                    value = "${subjects.size}",
                    color = colorScheme.primary
                )
            }
        }

        item {
            SectionHeader(
                title = "Daftar Mata Pelajaran",
                modifier = Modifier.padding(top = Spacing.sm)
            )
        }

        items(subjects) { subject ->
            SubjectCard(
                subject = subject,
                onClick = { onDetailMapel?.invoke(subject.id) }
            )
        }

        item { Spacer(Modifier.height(Spacing.xxxl)) }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectGrade,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scoreColor = when {
        subject.score >= 90 -> StatusSuccess
        subject.score >= 75 -> StatusWarning
        else -> StatusDanger
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = SDM3Shapes.small,
                color = scoreColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.School,
                        contentDescription = null,
                        tint = scoreColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                StatusChip(
                    text = "${subject.predicate} - ${subject.description}",
                    color = scoreColor
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "${subject.score}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
        }
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = SDM3Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.md)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = SDM3Shapes.extraSmall,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
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
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = SDM3Shapes.small,
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
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        item {
            SectionHeader(
                title = "Projek Penguatan Profil Pelajar Pancasila (P5)",
                modifier = Modifier.padding(top = Spacing.sm)
            )
        }
        items(proyek) { proyekItem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = SDM3Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proyekItem.tema,
                            style = MaterialTheme.typography.titleSmall,
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

@Preview
@Composable
private fun NilaiRaporScreenPreview() {
    SDM3Theme {
        NilaiRaporScreen(
            studentId = "",
            semester = "ganjil",
            onBack = {},
            onDetailMapel = {}
        )
    }
}
