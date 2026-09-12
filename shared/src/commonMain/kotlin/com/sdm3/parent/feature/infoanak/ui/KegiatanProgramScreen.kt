package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.infoanak.KegiatanProgramViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.viewmodel.koinViewModel

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

    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
    }

    val colorScheme = MaterialTheme.colorScheme
    val liquidSurface = liquidGlassSurfaceColor()
    val liquidBorder = liquidGlassBorderColor()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ekstrakurikuler", "Program Unggulan")

    ScreenScaffold(
        title = "Aktivitas & Bakat",
        subtitle = "PENGEMBANGAN DIRI",
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground(
                color = colorScheme.secondaryContainer,
                modifier = Modifier
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.md),
                    color = liquidSurface,
                    border = BorderStroke(1.dp, liquidBorder)
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        tabs.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(Spacing.sm))
                                    .background(if (isSelected) colorScheme.primary else Color.Transparent)
                                    .clickable { selectedTab = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colorScheme.onPrimary else ProductSchoolTheme.colors.onSurfaceMuted
                                )
                            }
                        }
                    }
                }

                when (val state = uiState) {
                    is ScreenUiState.Loading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(Spacing.sm))
                                        .shimmerEffect()
                                )
                            }
                            repeat(4) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(Spacing.md))
                                            .shimmerEffect()
                                    )
                                }
                            }
                        }
                    }

                    is ScreenUiState.Empty -> {
                        Sdm3EmptyState(
                            title = "Belum Ada Aktivitas",
                            message = "Belum terdapat data kegiatan untuk siswa ini.",
                            style = EmptyStateStyle.Neutral,
                            action = {
                                Sdm3Button(
                                    text = "Muat Ulang",
                                    onClick = { viewModel.loadActivities(studentId) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)
                                )
                            }
                        )
                    }

                    is ScreenUiState.Error -> {
                        Sdm3ErrorState(
                            title = "Gagal Memuat Data",
                            message = state.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.loadActivities(studentId) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)
                                )
                            },
                            secondaryAction = {
                                Sdm3OutlinedButton(
                                    text = "Kembali",
                                    onClick = onBack,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)
                                )
                            }
                        )
                    }

                    is ScreenUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = if (selectedTab == 0) "Kegiatan Ekstrakurikuler" else "Potensi & Pencapaian",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary,
                                        letterSpacing = (-0.5).sp,
                                    )
                                    Text(
                                        text = if (selectedTab == 0) {
                                            "Daftar kegiatan pengembangan bakat dan minat di luar jam pelajaran."
                                        } else {
                                            "Rekapitulasi progres program unggulan institusi."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                        lineHeight = 22.sp,
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
                                            modifier = Modifier.padding(vertical = Spacing.xl)
                                        )
                                    }
                                } else {
                                    items(ekskulList) { ekskul ->
                                        Sdm3Card(padding = Spacing.md) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Sdm3IconBadge(
                                                    icon = Icons.Outlined.AutoStories,
                                                    size = 52.dp,
                                                    iconTint = colorScheme.primary,
                                                    backgroundColor = colorScheme.primary.copy(alpha = 0.05f),
                                                    borderColor = colorScheme.primary.copy(alpha = 0.1f),
                                                )
                                                Spacer(modifier = Modifier.width(Spacing.md))
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
                                                            color = colorScheme.primary,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.weight(1f),
                                                        )
                                                    }
                                                    if (ekskul.teacherName != null) {
                                                        Text(
                                                            text = ekskul.teacherName,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ProductSchoolTheme.colors.onSurfaceMuted
                                                        )
                                                    }
                                                    if (ekskul.description != null) {
                                                        Spacer(modifier = Modifier.height(Spacing.xs))
                                                        Text(
                                                            text = "\"${ekskul.description}\"",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = ProductSchoolTheme.colors.onSurfaceMuted,
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
                                            modifier = Modifier.padding(vertical = Spacing.xl)
                                        )
                                    }
                                } else {
                                    items(programList) { program ->
                                        Sdm3Card(padding = Spacing.lg) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Sdm3IconBadge(
                                                        icon = Icons.AutoMirrored.Filled.MenuBook,
                                                        size = 48.dp,
                                                        iconTint = colorScheme.primary,
                                                        backgroundColor = colorScheme.primary.copy(alpha = 0.05f),
                                                    )
                                                    Spacer(modifier = Modifier.width(Spacing.md))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = program.name,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorScheme.primary,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                        Text(
                                                            text = program.coach,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ProductSchoolTheme.colors.onSurfaceMuted,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(Spacing.md))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = program.subtitle,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                                        modifier = Modifier.weight(1f),
                                                        maxLines = 3,
                                                        overflow = TextOverflow.Ellipsis,
                                                    )
                                                    Spacer(modifier = Modifier.width(Spacing.sm))
                                                    Surface(
                                                        shape = RoundedCornerShape(Spacing.xs),
                                                        color = colorScheme.primary.copy(alpha = 0.06f),
                                                    ) {
                                                        Text(
                                                            text = "${program.progress}/${program.target}",
                                                            style = MaterialTheme.typography.labelLarge,
                                                            fontWeight = FontWeight.Black,
                                                            color = colorScheme.primary,
                                                            modifier = Modifier.padding(
                                                                horizontal = 10.dp,
                                                                vertical = 6.dp,
                                                            ),
                                                            maxLines = 1,
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(Spacing.sm))

                                                val progressFraction = if (program.target > 0) {
                                                    (program.progress.toFloat() / program.target.toFloat()).coerceIn(0f, 1f)
                                                } else 0f
                                                LinearProgressIndicator(
                                                    progress = { progressFraction },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(8.dp)
                                                        .clip(CircleShape),
                                                    color = colorScheme.secondary,
                                                    trackColor = colorScheme.secondary.copy(alpha = 0.1f),
                                                )

                                                Spacer(modifier = Modifier.height(Spacing.sm))
                                                HorizontalDivider(color = colorScheme.outline)
                                                Spacer(modifier = Modifier.height(10.dp))

                                                Row(verticalAlignment = Alignment.Top) {
                                                    Icon(
                                                        Icons.Outlined.Update,
                                                        contentDescription = "Aktivitas terakhir",
                                                        tint = colorScheme.primary.copy(alpha = 0.3f),
                                                        modifier = Modifier.size(16.dp),
                                                    )
                                                    Spacer(modifier = Modifier.width(Spacing.xs))
                                                    Text(
                                                        text = program.lastActivity,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                                        maxLines = 3,
                                                        overflow = TextOverflow.Ellipsis,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(Spacing.xxl)) }
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
