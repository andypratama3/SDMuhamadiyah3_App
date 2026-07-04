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
import com.sdm3.parent.feature.nilai.NilaiRaporUiState
import com.sdm3.parent.feature.nilai.NilaiRaporViewModel
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
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Analitik Akademik",
                            style = MaterialTheme.typography.titleLarge,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(semesters) { opt ->
                            val selected = opt == activeSemester
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (selected) colorScheme.primary else Color.White.copy(alpha = 0.6f),
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
                            SumatifTabContent(vmState.grades, onDetailMapel)
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
                        val predicate = when {
                            avgScore >= 90 -> "A"
                            avgScore >= 80 -> "B"
                            avgScore >= 70 -> "C"
                            avgScore >= 60 -> "D"
                            else -> "E"
                        }
                        Surface(
                            color = colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = " PREDIKAT $predicate ",
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

        if (subjects.isNotEmpty()) {
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


