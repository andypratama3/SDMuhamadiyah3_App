package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.infoanak.KegiatanProgramViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.viewmodel.koinViewModel

sealed class KegiatanProgramUiState {
    data object Loading : KegiatanProgramUiState()
    data object Empty : KegiatanProgramUiState()
    data class Error(val message: String) : KegiatanProgramUiState()
    data object Success : KegiatanProgramUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanProgramScreen(
    studentId: String,
    onBack: () -> Unit,
    viewModel: KegiatanProgramViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.infoanak.KegiatanProgramUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadActivities(studentId)
        }
    }

    val uiState: KegiatanProgramUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> KegiatanProgramUiState.Loading
            s.errorMessage != null -> KegiatanProgramUiState.Error(s.errorMessage)
            s.isEmpty -> KegiatanProgramUiState.Empty
            else -> KegiatanProgramUiState.Success
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ekstrakurikuler", "Program Unggulan")

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Aktivitas & Bakat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "PENGEMBANGAN DIRI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(0f, size.height * 0.5f),
                        radius = size.width
                    )
                )
            }

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                ) {
                    Row(modifier = Modifier.padding(6.dp)) {
                        tabs.forEachIndexed { index, label ->
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

                when (val state = uiState) {
                    is KegiatanProgramUiState.Loading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .shimmerEffect()
                                )
                            }
                            repeat(4) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .shimmerEffect()
                                    )
                                }
                            }
                        }
                    }

                    is KegiatanProgramUiState.Empty -> {
                        Sdm3EmptyState(
                            title = "Belum Ada Aktivitas",
                            message = "Belum terdapat data kegiatan untuk siswa ini.",
                            style = EmptyStateStyle.Neutral,
                            action = {
                                Sdm3Button(
                                    text = "Muat Ulang",
                                    onClick = { viewModel.loadActivities(studentId) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )
                            }
                        )
                    }

                    is KegiatanProgramUiState.Error -> {
                        Sdm3ErrorState(
                            title = "Gagal Memuat Data",
                            message = state.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.loadActivities(studentId) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )
                            },
                            secondaryAction = {
                                Sdm3OutlinedButton(
                                    text = "Kembali",
                                    onClick = onBack,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )
                            }
                        )
                    }

                    is KegiatanProgramUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = "Potensi & Pencapaian",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary,
                                        letterSpacing = (-1).sp
                                    )
                                    Text(
                                        text = "Eksplorasi minat dan rekapitulasi progres program unggulan institusi.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        lineHeight = 26.sp
                                    )
                                }
                            }

                            if (selectedTab == 0) {
                                val ekskulList = vmState.extracurriculars

                                if (ekskulList.isEmpty()) {
                                    item {
                                        Sdm3EmptyState(
                                            title = "Belum Ada Ekstrakurikuler",
                                            message = "Siswa belum terdaftar dalam kegiatan ekstrakurikuler.",
                                            style = EmptyStateStyle.Neutral,
                                            modifier = Modifier.padding(vertical = 24.dp)
                                        )
                                    }
                                } else {
                                    items(ekskulList) { ekskul ->
                                        Sdm3Card(padding = 16.dp) {
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
                                                            imageVector = Icons.Outlined.AutoStories,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(24.dp),
                                                            tint = colorScheme.primary
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = ekskul.name,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorScheme.primary
                                                        )
                                                    }
                                                    if (ekskul.teacherName != null) {
                                                        Text(
                                                            text = ekskul.teacherName,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorScheme.primary.copy(alpha = 0.4f)
                                                        )
                                                    }
                                                    if (ekskul.description != null) {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            text = "\"${ekskul.description}\"",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                                            lineHeight = 20.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                val programList = vmState.academicPrograms

                                if (programList.isEmpty()) {
                                    item {
                                        Sdm3EmptyState(
                                            title = "Belum Ada Program Unggulan",
                                            message = "Data program unggulan belum tersedia.",
                                            style = EmptyStateStyle.Neutral,
                                            modifier = Modifier.padding(vertical = 24.dp)
                                        )
                                    }
                                } else {
                                    items(programList) { program ->
                                        Sdm3Card(padding = 20.dp) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        modifier = Modifier.size(48.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                Icons.AutoMirrored.Filled.MenuBook,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(24.dp),
                                                                tint = colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Column {
                                                        Text(
                                                            text = program.name,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorScheme.primary
                                                        )
                                                        Text(
                                                            text = program.coach,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorScheme.primary.copy(alpha = 0.4f)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(20.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = program.subtitle.uppercase(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Black,
                                                        color = colorScheme.primary.copy(alpha = 0.5f),
                                                        letterSpacing = 1.sp
                                                    )
                                                    Text(
                                                        text = "${program.progress}/${program.target}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Black,
                                                        color = colorScheme.primary
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                val progressFraction = if (program.target > 0) {
                                                    (program.progress.toFloat() / program.target.toFloat()).coerceIn(0f, 1f)
                                                } else 0f
                                                LinearProgressIndicator(
                                                    progress = { progressFraction },
                                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                                                    color = colorScheme.secondary,
                                                    trackColor = colorScheme.secondary.copy(alpha = 0.1f)
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))
                                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f))
                                                Spacer(modifier = Modifier.height(12.dp))

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Outlined.Update, contentDescription = null, tint = colorScheme.primary.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Aktivitas Terakhir: ${program.lastActivity}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.primary.copy(alpha = 0.4f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(100.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun KegiatanProgramScreenPreview() {
    SDM3Theme {
        KegiatanProgramScreen(
            studentId = "",
            onBack = {}
        )
    }
}
