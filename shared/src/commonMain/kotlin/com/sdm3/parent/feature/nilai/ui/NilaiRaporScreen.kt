package com.sdm3.parent.feature.nilai.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

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
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.feature.nilai.FormatifGradeItem
import com.sdm3.parent.feature.nilai.NilaiRaporUiState
import com.sdm3.parent.feature.nilai.NilaiRaporViewModel
import com.sdm3.parent.feature.nilai.ProjekGradeItem
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.tooling.preview.Preview

internal fun semesterLabelOf(semester: String): String = when (semester.lowercase()) {
    "ganjil" -> "Semester Ganjil"
    "genap" -> "Semester Genap"
    "ts1" -> "Tengah Semester 1"
    "as1" -> "Akhir Semester 1"
    "ts2" -> "Tengah Semester 2"
    "at" -> "Akhir Tahun"
    else -> "Semester $semester"
}

internal fun semesterShortLabel(semester: String): String = when (semester.lowercase()) {
    "ganjil" -> "Ganjil"
    "genap" -> "Genap"
    "ts1" -> "TS 1"
    "as1" -> "AS 1"
    "ts2" -> "TS 2"
    "at" -> "Akhir Tahun"
    else -> semester.replaceFirstChar { it.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiRaporScreen(
    studentId: String,
    semester: String,
    onBack: (() -> Unit)? = null,
    onDetailMapel: ((subjectId: String) -> Unit)? = null,
    viewModel: NilaiRaporViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val vmState by if (isPreview) {
        remember {
            mutableStateOf(
                NilaiRaporUiState(
                    grades = listOf(
                        GradeDto(
                            id = "demo-g-1",
                            subjectId = "demo-s-1",
                            subjectName = "Matematika",
                            score = 92.0,
                            predicate = "SANGAT BAIK",
                            narrative = "Penguasaan konsep sangat baik.",
                            semester = "ganjil"
                        ),
                        GradeDto(
                            id = "demo-g-2",
                            subjectId = "demo-s-2",
                            subjectName = "Bahasa Indonesia",
                            score = 84.0,
                            predicate = "BAIK",
                            narrative = "Pemahaman teks baik.",
                            semester = "ganjil"
                        )
                    ),
                    formatifGrades = listOf(
                        FormatifGradeItem(code = "FORM-1", description = "Pengukuran Sudut", score = 88),
                        FormatifGradeItem(code = "FORM-2", description = "Operasi Hitung", score = 95)
                    ),
                    projekGrades = listOf(
                        ProjekGradeItem(tema = "Projek 1", deskripsi = "Rancang Bangun Jembatan", nilai = 78, predikat = "CUKUP"),
                        ProjekGradeItem(tema = "Projek 2", deskripsi = "Pameran Sains", nilai = 90, predikat = "SANGAT BAIK")
                    ),
                    isEmpty = false,
                    semester = "ganjil",
                    availableSemesters = listOf("ganjil", "genap")
                )
            )
        }
    } else {
        viewModel.uiState.collectAsState()
    }
    val activeSemester = vmState.semester.ifBlank { semester }

    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadInitial(studentId, semester)
        }
    }

    ScreenScaffold(
        title = "Analitik Akademik",
        subtitle = semesterLabelOf(activeSemester).uppercase(),
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                val semesters = vmState.availableSemesters
                if (semesters.size > 1) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        items(semesters) { opt ->
                            val selected = opt == activeSemester
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (selected) colorScheme.primary else colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (selected) colorScheme.primary else colorScheme.outline
                                ),
                                modifier = Modifier.clickable(enabled = !selected) { viewModel.selectSemester(opt) }
                            ) {
                                Text(
                                    text = semesterShortLabel(opt),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (selected) colorScheme.onPrimary else colorScheme.primary,
                                    letterSpacing = 0.2.sp,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    val tabs = buildList {
                        add("Sumatif")
                        if (vmState.formatifGrades.isNotEmpty()) add("Formatif")
                        if (vmState.projekGrades.isNotEmpty()) add("Projek")
                    }
                    val selectedTab = vmState.selectedTab.coerceIn(0, (tabs.size - 1).coerceAtLeast(0))

                    when (uiState) {
                        ScreenUiState.Loading -> {
                            SumatifTabShimmer()
                        }
                        ScreenUiState.Empty -> {
                            Sdm3EmptyState(
                                title = "Belum Ada Data Nilai",
                                message = "Data nilai untuk semester ini belum tersedia.",
                                style = EmptyStateStyle.Neutral,
                                action = {
                                    Sdm3Button(
                                        text = "Muat Ulang",
                                        onClick = { viewModel.refresh() }
                                    )
                                }
                            )
                        }
                        is ScreenUiState.Error -> {
                            Sdm3ErrorState(
                                title = "Gagal Memuat Data",
                                message = uiState.message,
                                style = ErrorStateStyle.Generic,
                                primaryAction = {
                                    Sdm3Button(
                                        text = "Coba Lagi",
                                        onClick = { viewModel.refresh() }
                                    )
                                }
                            )
                        }
                        ScreenUiState.Success -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (tabs.size > 1) {
                                    PrimaryScrollableTabRow(
                                        selectedTabIndex = selectedTab,
                                        containerColor = Color.Transparent,
                                        contentColor = colorScheme.primary,
                                        edgePadding = 24.dp,
                                        divider = {},
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        tabs.forEachIndexed { index, label ->
                                            Tab(
                                                selected = selectedTab == index,
                                                onClick = { viewModel.selectTab(index) },
                                                text = {
                                                    Text(
                                                        text = label,
                                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    when (tabs.getOrNull(selectedTab)) {
                                        "Formatif" -> FormatifTabContent(vmState.formatifGrades)
                                        "Projek" -> ProjekTabContent(vmState.projekGrades)
                                        else -> SumatifTabContent(vmState.grades, onDetailMapel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SumatifTabShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .shimmerEffect()
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .shimmerEffect()
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
        }
        items(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shimmerEffect()
            )
        }
        item { Spacer(Modifier.height(Spacing.bottomNavSafeArea)) }
    }
}

@Composable
private fun SumatifTabContent(
    grades: List<GradeDto>,
    onDetailMapel: ((subjectId: String) -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val subjects = grades.map { grade ->
        SubjectGrade(
            name = grade.subjectName,
            score = grade.score?.toInt() ?: 0,
            predicate = grade.predicate ?: "-",
            description = grade.narrative ?: "",
            id = grade.subjectId
        )
    }
    val avgScore = if (subjects.isNotEmpty()) subjects.map { it.score }.average().toInt() else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            // Hero Score Card (Centered content and dynamic colors)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val glowColor = colorScheme.secondary.copy(alpha = 0.3f)
                    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp).alpha(0.15f)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor, Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.15f),
                                radius = size.width * 0.8f
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.padding(vertical = 36.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RATA-RATA KOMPETENSI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.2.sp,
                            color = colorScheme.onPrimary.copy(alpha = 0.45f)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "$avgScore",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 60.sp),
                            fontWeight = FontWeight.Black,
                            color = colorScheme.onPrimary,
                            letterSpacing = (-2.5).sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        val (predicateLabel, predicateColor) = com.sdm3.parent.core.util.predicateForScore(avgScore)
                        Surface(
                            color = predicateColor,
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Text(
                                text = " $predicateLabel ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onPrimary,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
        }

        if (subjects.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatMiniCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Outlined.TrendingUp,
                        label = "TERTINGGI",
                        value = "${subjects.maxOf { it.score }}",
                        color = statusSuccess
                    )
                    StatMiniCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Outlined.TrendingDown,
                        label = "TERENDAH",
                        value = "${subjects.minOf { it.score }}",
                        color = statusWarning
                    )
                }
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

        item { Spacer(Modifier.height(Spacing.bottomNavSafeArea)) }
    }
}

@Composable
private fun FormatifTabContent(items: List<FormatifGradeItem>) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            SectionHeader(title = "Penilaian Formatif", modifier = Modifier.padding(top = 8.dp))
        }
        items(items) { item ->
            Sdm3Card(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.code,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                        if (item.description.isNotBlank()) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = ProductSchoolTheme.colors.onSurfaceMuted
                            )
                        }
                    }
                    Text(
                        text = "${item.score}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = com.sdm3.parent.core.util.scoreColor(item.score)
                    )
                }
            }
        }
        item { Spacer(Modifier.height(Spacing.bottomNavSafeArea)) }
    }
}

@Composable
private fun ProjekTabContent(items: List<ProjekGradeItem>) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            SectionHeader(title = "Projek P5", modifier = Modifier.padding(top = 8.dp))
        }
        items(items) { item ->
            Sdm3Card(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item.tema,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    if (item.deskripsi.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.deskripsi,
                            style = MaterialTheme.typography.bodySmall,
                            color = ProductSchoolTheme.colors.onSurfaceMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.nilai}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = com.sdm3.parent.core.util.scoreColor(item.nilai)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Predikat ${item.predikat}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ProductSchoolTheme.colors.onSurfaceFaint
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(Spacing.bottomNavSafeArea)) }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectGrade,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scoreColor = com.sdm3.parent.core.util.scoreColor(subject.score)

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
                color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                border = BorderStroke(1.5.dp, colorScheme.outline),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AutoStories,
                        contentDescription = "Mata pelajaran",
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
                    color = colorScheme.primary,
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "Predikat ${subject.predicate}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    letterSpacing = 0.5.sp
                )
            }
            Surface(
                color = scoreColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, scoreColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "${subject.score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = scoreColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "Lihat detail",
                tint = colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(18.dp)
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Black,
                color = colorScheme.primary,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = ProductSchoolTheme.colors.onSurfaceFaint
            )
        }
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


