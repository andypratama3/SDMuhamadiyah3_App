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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.SecondaryContainer
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

private val tabs = listOf("Sumatif", "Formatif", "Projek")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiRaporScreen(
    studentId: String,
    semester: String,
    onBack: (() -> Unit)? = null,
    onDetailMapel: ((subjectId: String) -> Unit)? = null,
    viewModel: NilaiRaporViewModel = org.koin.compose.viewmodel.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(studentId, semester) {
        viewModel.loadGrades(studentId, semester)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nilai & Rapor") },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() }) {
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
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(modifier = Modifier.height(Spacing.sm))

            if (uiState.isLoading && uiState.grades.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (uiState.errorMessage != null && uiState.grades.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = uiState.errorMessage ?: "Terjadi kesalahan", color = Color.Red)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Sdm3Button(text = "Coba Lagi", onClick = { viewModel.refresh() })
                }
            } else {
                when (selectedTab) {
                    0 -> SumatifTab(
                        studentId = studentId,
                        semester = semester,
                        grades = uiState.grades,
                        onDetailMapel = onDetailMapel
                    )
                    1 -> FormatifTab(
                        studentId = studentId,
                        grades = uiState.grades
                    )
                    2 -> ProjekTab(studentId = studentId)
                }
            }
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
    grades: List<com.sdm3.parent.data.remote.dto.GradeDto>,
    onDetailMapel: ((subjectId: String) -> Unit)?
) {
    val avgScore = if (grades.isNotEmpty()) grades.map { it.score }.average().toInt() else 0

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

        if (grades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Belum ada data nilai", color = OnSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(grades) { grade ->
                    SubjectCard(
                        subject = SubjectGrade(
                            name = grade.subjectName,
                            score = grade.score,
                            predicate = grade.predicate ?: "-",
                            description = grade.description ?: "",
                            id = grade.id
                        ),
                        onClick = { onDetailMapel?.invoke(grade.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormatifTab(
    studentId: String,
    grades: List<com.sdm3.parent.data.remote.dto.GradeDto>,
    viewModel: DetailNilaiMapelViewModel = org.koin.compose.viewmodel.koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSubjectId by remember { mutableStateOf(grades.firstOrNull()?.id ?: "") }

    LaunchedEffect(selectedSubjectId) {
        if (selectedSubjectId.isNotEmpty()) {
            viewModel.loadComponents(studentId, selectedSubjectId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.2f)),
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

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(grades) { grade ->
                    FilterChip(
                        selected = selectedSubjectId == grade.id,
                        onClick = { selectedSubjectId = grade.id },
                        label = { Text(grade.subjectName) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        } else {
            items(uiState.components) { tp ->
            items(uiState.components) { tp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                                .background(SecondaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (tp.tpNumber ?: 1).toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Secondary
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tp.tpName ?: "Tujuan Pembelajaran",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        val score = tp.score?.toInt() ?: 0
                        val tpColor = when {
                            score >= 90 -> StatusSuccess
                            score >= 75 -> StatusWarning
                            else -> StatusDanger
                        }
                        StatusChip(
                            text = "$score",
                            color = tpColor
                        )
                    }
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
