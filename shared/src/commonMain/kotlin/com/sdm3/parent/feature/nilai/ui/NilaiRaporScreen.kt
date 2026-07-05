package com.sdm3.parent.feature.nilai.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.feature.nilai.FormatifGradeItem
import com.sdm3.parent.feature.nilai.NilaiRaporUiState
import com.sdm3.parent.feature.nilai.NilaiRaporViewModel
import com.sdm3.parent.feature.nilai.ProjekGradeItem
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.tooling.preview.Preview

sealed class NilaiRaporScreenUiState {
    data object Loading : NilaiRaporScreenUiState()
    data object Empty : NilaiRaporScreenUiState()
    data class Error(val message: String) : NilaiRaporScreenUiState()
    data object Success : NilaiRaporScreenUiState()
}

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
        remember { mutableStateOf(NilaiRaporUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val activeSemester = vmState.semester.ifBlank { semester }

    val uiState: NilaiRaporScreenUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> NilaiRaporScreenUiState.Loading
            s.errorMessage != null -> NilaiRaporScreenUiState.Error(s.errorMessage)
            s.isEmpty -> NilaiRaporScreenUiState.Empty
            else -> NilaiRaporScreenUiState.Success
        }
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadInitial(studentId, semester)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Analitik Akademik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = semesterLabelOf(activeSemester).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
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
                                color = if (selected) colorScheme.primary else glassSurfaceColor(),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) colorScheme.primary else colorScheme.primary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.clickable(enabled = !selected) { viewModel.selectSemester(opt) }
                            ) {
                                Text(
                                    text = semesterShortLabel(opt),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) colorScheme.onPrimary else colorScheme.primary.copy(alpha = 0.7f),
                                    letterSpacing = 0.2.sp,
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
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
                        NilaiRaporScreenUiState.Loading -> {
                            SumatifTabShimmer()
                        }
                        NilaiRaporScreenUiState.Empty -> {
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
                        is NilaiRaporScreenUiState.Error -> {
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
                        NilaiRaporScreenUiState.Success -> {
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .shimmerEffect()
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        items(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        item { Spacer(Modifier.height(100.dp)) }
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
    val statusDanger = statusDangerColor()
    val heroContent = heroContentColor()
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val glowColor = colorScheme.surfaceTint.copy(alpha = 0.3f)
                    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp).alpha(0.1f)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glowColor, Color.Transparent),
                                center = Offset(size.width * 0.9f, 0f),
                                radius = size.width
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RATA-RATA KOMPETENSI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = colorScheme.onPrimary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "$avgScore",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                            fontWeight = FontWeight.Black,
                            color = colorScheme.onPrimary,
                            letterSpacing = (-2).sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val (predicateLabel, predicateColor) = when {
                            avgScore >= 90 -> "SANGAT BAIK" to statusSuccess
                            avgScore >= 80 -> "BAIK" to colorScheme.secondary
                            avgScore >= 70 -> "CUKUP" to statusWarning
                            else -> "PERLU BIMBINGAN" to statusDanger
                        }
                        Surface(
                            color = heroContent.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(99.dp),
                            border = BorderStroke(1.dp, predicateColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = " $predicateLabel ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = predicateColor,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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

        item { Spacer(Modifier.height(100.dp)) }
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
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Text(
                        text = "${item.score}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = colorScheme.secondary
                    )
                }
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun ProjekTabContent(items: List<ProjekGradeItem>) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
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
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.nilai}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = statusSuccess
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Predikat ${item.predikat}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
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
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val statusDanger = statusDangerColor()
    val scoreColor = when {
        subject.score >= 90 -> statusSuccess
        subject.score >= 75 -> statusWarning
        else -> statusDanger
    }

    Sdm3Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.primary.copy(alpha = 0.03f),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.05f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AutoStories,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
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
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    letterSpacing = 0.5.sp
                )
            }
            Surface(
                color = scoreColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "${subject.score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = scoreColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
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
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
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
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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


