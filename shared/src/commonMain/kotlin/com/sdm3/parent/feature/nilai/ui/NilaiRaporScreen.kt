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

private val tabLabels = listOf("Sumatif", "Formatif", "Projek")
private val PremiumEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

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
    var selectedTab by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme
    val semesterOptions = remember { listOf("ganjil", "genap", "ts1", "as1", "ts2", "at") }
    var showSemesterMenu by remember { mutableStateOf(false) }
    val semesterLabel = remember(semester) {
        when (semester.lowercase()) {
            "ganjil" -> "Semester Ganjil"
            "genap" -> "Semester Genap"
            "ts1" -> "Tengah Semester 1"
            "as1" -> "Akhir Semester 1"
            "ts2" -> "Tengah Semester 2"
            "at" -> "Akhir Tahun"
            else -> "Semester $semester"
        }
    }
    val vmState by if (isPreview) {
        remember { mutableStateOf(NilaiRaporUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

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
            viewModel.loadGrades(studentId, semester)
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
                        Box {
                            Text(
                                text = semesterLabel.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary.copy(alpha = 0.4f),
                                letterSpacing = 1.sp,
                                modifier = Modifier.clickable { showSemesterMenu = true }
                            )
                            DropdownMenu(
                                expanded = showSemesterMenu,
                                onDismissRequest = { showSemesterMenu = false }
                            ) {
                                semesterOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (opt) {
                                                    "ganjil" -> "Semester Ganjil"
                                                    "genap" -> "Semester Genap"
                                                    "ts1" -> "Tengah Semester 1"
                                                    "as1" -> "Akhir Semester 1"
                                                    "ts2" -> "Tengah Semester 2"
                                                    "at" -> "Akhir Tahun"
                                                    else -> opt
                                                },
                                                fontWeight = if (opt == semester) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            showSemesterMenu = false
                                            if (opt != semester) {
                                                viewModel.loadGrades(studentId, opt)
                                            }
                                        }
                                    )
                                }
                            }
                        }
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

                Box(modifier = Modifier.weight(1f)) {
                    when (uiState) {
                        NilaiRaporScreenUiState.Loading -> {
                            AnimatedContent(
                                targetState = selectedTab,
                                transitionSpec = {
                                    (fadeIn(tween(400, easing = PremiumEasing)) + scaleIn(initialScale = 0.95f)) togetherWith fadeOut(tween(300))
                                },
                                label = "tabShimmer"
                            ) { targetTab ->
                                when (targetTab) {
                                    0 -> SumatifTabShimmer()
                                    1 -> FormatifTabShimmer()
                                    2 -> ProjekTabShimmer()
                                }
                            }
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
                            AnimatedContent(
                                targetState = selectedTab,
                                transitionSpec = {
                                    (fadeIn(tween(400, easing = PremiumEasing)) + scaleIn(initialScale = 0.95f)) togetherWith fadeOut(tween(300))
                                },
                                label = "tabContent"
                            ) { targetTab ->
                                when (targetTab) {
                                    0 -> SumatifTabContent(vmState.grades, onDetailMapel)
                                    1 -> FormatifTabContent(vmState.formatifGrades)
                                    2 -> ProjekTabContent(vmState.projekGrades)
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
private fun FormatifTabShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        items(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun ProjekTabShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        items(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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
private fun FormatifTabContent(
    formatifItems: List<FormatifGradeItem>
) {
    val colorScheme = MaterialTheme.colorScheme

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

        items(formatifItems) { tp ->
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
private fun ProjekTabContent(
    projekItems: List<ProjekGradeItem>
) {
    val colorScheme = MaterialTheme.colorScheme

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
        items(projekItems) { proyekItem ->
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


