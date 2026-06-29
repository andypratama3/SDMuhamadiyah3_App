package com.sdm3.parent.feature.nilai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
private val PremiumEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

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
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Analitik Akademik",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Semester $semester".uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // ProductSchool Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Editorial Tab Switcher
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                ) {
                    Row(modifier = Modifier.padding(6.dp)) {
                        tabLabels.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) colorScheme.primary else Color.Transparent)
                                    .clickable { selectedTab = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colorScheme.onPrimary else colorScheme.primary.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        (fadeIn(tween(400, easing = PremiumEasing)) + scaleIn(initialScale = 0.95f)) togetherWith fadeOut(tween(300))
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            // Hero Score Card (ProductSchool Style)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    val glowColor = colorScheme.surfaceTint.copy(alpha = 0.4f)
                    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).alpha(0.15f)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor, Color.Transparent),
                                center = Offset(size.width * 0.9f, 0f),
                                radius = size.width
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RATA-RATA KOMPETENSI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$avgScore",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = " PREDIKAT A ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    label = "TERTINGGI",
                    value = "${subjects.maxOf { it.score }}",
                    color = StatusSuccess
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Outlined.TrendingDown,
                    label = "TERENDAH",
                    value = "${subjects.minOf { it.score }}",
                    color = StatusWarning
                )
            }
        }

        item {
            SectionHeader(
                title = "Mata Pelajaran",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(subjects) { subject ->
            SubjectCard(
                subject = subject,
                onClick = { onDetailMapel?.invoke(subject.id) }
            )
        }

        item { Spacer(Modifier.height(100.dp)) }
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

    Sdm3Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = colorScheme.primary.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AutoStories,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = scoreColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = " ${subject.predicate} ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = "${subject.score}",
                style = MaterialTheme.typography.headlineSmall,
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
    Sdm3Card(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun FormatifTabContent(studentId: String) {
    val colorScheme = MaterialTheme.colorScheme
    val tpData = listOf(
        TPItem("TP 1.1", "Menjelaskan operasi hitung bilangan cacah", 85),
        TPItem("TP 1.2", "Menyelesaikan soal cerita penjumlahan", 90),
        TPItem("TP 2.1", "Mengidentifikasi sifat-sifat bangun datar", 78),
        TPItem("TP 2.2", "Menghitung keliling bangun datar", 72),
        TPItem("TP 3.1", "Menyelesaikan masalah kontekstual", 88)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            Sdm3Card {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = colorScheme.primary.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Evaluasi berkelanjutan berdasarkan Tujuan Pembelajaran (TP) yang dicapai setiap pekan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.primary.copy(alpha = 0.6f),
                        lineHeight = 20.sp
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
            Sdm3Card {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = colorScheme.primary.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tp.code,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tp.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${tp.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tpColor
                    )
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun ProjekTabContent(studentId: String) {
    val colorScheme = MaterialTheme.colorScheme
    val proyek = listOf(
        ProjekItem("Gaya Hidup Berkelanjutan", "Daur ulang limbah rumah tangga", 88, "A"),
        ProjekItem("Kearifan Lokal", "Pameran budaya Kalimantan Timur", 92, "A"),
        ProjekItem("Bangunlah Jiwa Raganya", "Kampanye anti-bullying", 85, "B+")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            SectionHeader(
                title = "Projek Profil Pancasila (P5)",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(proyek) { proyekItem ->
            Sdm3Card {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proyekItem.tema,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = StatusSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = " ${proyekItem.nilai} (${proyekItem.predikat}) ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = StatusSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = proyekItem.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

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

data class SubjectGrade(
    val name: String,
    val score: Int,
    val predicate: String,
    val description: String,
    val id: String
)

data class TPItem(val code: String, val description: String, val score: Int)

data class ProjekItem(val tema: String, val deskripsi: String, val nilai: Int, val predikat: String)
