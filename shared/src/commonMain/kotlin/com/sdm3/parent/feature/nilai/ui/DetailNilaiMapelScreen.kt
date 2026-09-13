package com.sdm3.parent.feature.nilai.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

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
import com.sdm3.parent.core.designsystem.theme.Spacing
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.feature.nilai.DetailNilaiMapelUiState
import com.sdm3.parent.feature.nilai.DetailNilaiMapelViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNilaiMapelScreen(
    studentId: String,
    subjectId: String,
    semester: String,
    onBack: () -> Unit,
    viewModel: DetailNilaiMapelViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val uiState by if (isPreview) {
        remember { mutableStateOf(DetailNilaiMapelUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    val errorMessage = uiState.errorMessage

    val screenState = remember(uiState.isLoading, uiState.isEmpty, errorMessage, isPreview) {
        if (isPreview) ScreenUiState.Success
        else resolveScreenState(uiState.isLoading, uiState.isEmpty, errorMessage)
    }

    if (!isPreview) {
        LaunchedEffect(studentId, subjectId) {
            viewModel.loadComponents(studentId, subjectId)
        }
    }

    ScreenScaffold(
        title = uiState.subjectName.ifEmpty { "Mata Pelajaran" },
        subtitle = "ANALISIS KOMPETENSI",
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            when (screenState) {
                ScreenUiState.Loading -> {
                    DetailNilaiMapelShimmer(modifier = Modifier.fillMaxSize().padding(padding))
                }
                ScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Data Penilaian",
                        message = "Data penilaian untuk mata pelajaran ini belum tersedia.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier.fillMaxSize().padding(padding)
                    )
                }
                is ScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data Penilaian",
                        message = screenState.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.fillMaxSize().padding(padding),
                        primaryAction = if (!isPreview) {
                            {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.refresh() }
                                )
                            }
                        } else null
                    )
                }
                ScreenUiState.Success -> {
                    val components = uiState.components
                    val subjectName = uiState.subjectName.ifEmpty { "Mata Pelajaran" }

                    val componentGroups = components.groupBy { it.componentType }
                    val sumatifAvg = componentGroups["sumatif"]?.let { group -> group.mapNotNull { it.score }.takeIf { it.isNotEmpty() }?.average() } ?: 0.0
                    val formatifAvg = componentGroups["formatif"]?.let { group -> group.mapNotNull { it.score }.takeIf { it.isNotEmpty() }?.average() } ?: 0.0
                    val projekAvg = componentGroups["projek"]?.let { group -> group.mapNotNull { it.score }.takeIf { it.isNotEmpty() }?.average() } ?: 0.0

                    // Skor akhir dihitung dari komponen agregat (baris ringkasan),
                    // BUKAN dari baris rincian TP — agar TP tidak ikut dihitung ganda.
                    val aggregateScores = components.filter { it.tpName == null }.mapNotNull { it.score }
                    val fallbackScores = components.mapNotNull { it.score }
                    val scoreBasis = aggregateScores.ifEmpty { fallbackScores }
                    val finalScore = if (scoreBasis.isNotEmpty()) scoreBasis.average().toInt() else 0

                    val tpList = components.filter { it.tpName != null }
                    val heroContent = heroContentColor()
                    val statusSuccess = statusSuccessColor()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        item {
                            // Score Hero Card (Centered and Refined)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    val glowColor = heroContent.copy(alpha = 0.3f)
                                    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp).alpha(0.4f)) {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(glowColor.copy(alpha = 0.15f), Color.Transparent),
                                                center = Offset(size.width * 0.9f, 0f),
                                                radius = size.width * 1.2f
                                            )
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "SKOR AKHIR",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 2.sp,
                                            color = heroContent.copy(alpha = 0.4f)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "$finalScore",
                                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                                            fontWeight = FontWeight.Black,
                                            color = heroContent,
                                            letterSpacing = (-2).sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        val (predicateLabel, predicateColor) = com.sdm3.parent.core.util.predicateForScore(finalScore)
                                        Surface(
                                            color = predicateColor,
                                            shape = RoundedCornerShape(99.dp)
                                        ) {
                                            Text(
                                                text = " $predicateLabel ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = colorScheme.onPrimary,
                                                letterSpacing = 1.sp,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Parameter Penilaian",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (componentGroups.containsKey("sumatif")) {
                            item { KomponenBar("Sumatif", sumatifAvg.toFloat(), 100f, colorScheme.primary) }
                        }
                        if (componentGroups.containsKey("formatif")) {
                            item { KomponenBar("Formatif", formatifAvg.toFloat(), 100f, colorScheme.primary) }
                        }
                        if (componentGroups.containsKey("projek")) {
                            item { KomponenBar("Projek", projekAvg.toFloat(), 100f, statusSuccess) }
                        }

                        if (tpList.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Ketercapaian TP",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            items(tpList) { tp ->
                                var expanded by remember { mutableStateOf(false) }
                                val tpScore = tp.score?.toInt() ?: 0
                                val tpColor = com.sdm3.parent.core.util.scoreColor(tpScore)

                                Sdm3Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded = !expanded },
                                    padding = 16.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = colorScheme.primary.copy(alpha = 0.05f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "TP ${tp.tpNumber ?: ""}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.primary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = tp.tpName ?: "",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = "${tpScore}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = tpColor
                                            )
                                        }
                                        if (expanded) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            HorizontalDivider(color = colorScheme.outline)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            val tpNote = when {
                                                tpScore >= 90 -> "Ananda telah menguasai TP ini dengan sangat baik. Pertahankan prestasi ini."
                                                tpScore >= 80 -> "Ananda telah menguasai TP ini dengan baik. Pertahankan pemahamannya."
                                                tpScore >= 70 -> "Ananda telah mencapai standar minimal pada TP ini. Terus tingkatkan pemahaman."
                                                else -> "Ananda perlu bimbingan tambahan pada TP ini. Disarankan untuk belajar lebih giat."
                                            }
                                            Text(
                                                text = tpNote,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ProductSchoolTheme.colors.onSurfaceMuted,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                            val catatanGuru = components.firstOrNull { it.catatan != null }?.catatan
                            if (catatanGuru != null) {
                                item {
                                    SectionHeader(
                                        title = "Anotasi Akademik",
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Sdm3Card(padding = 20.dp) {
                                        Column {
                                            Icon(
                                                Icons.Outlined.FormatQuote,
                                                contentDescription = "Catatan guru",
                                                tint = colorScheme.secondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = catatanGuru,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = colorScheme.primary,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 26.sp
                                            )
                                        }
                                    }
                                }
                            }

                        item { Spacer(modifier = Modifier.height(Spacing.bottomNavSafeArea)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailNilaiMapelShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
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
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        items(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        item {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        items(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        item {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
        item { Spacer(Modifier.height(Spacing.bottomNavSafeArea)) }
    }
}

@Composable
private fun KomponenBar(komponen: String, nilai: Float, max: Float, warna: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(padding = 16.dp) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(komponen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Text("${nilai.toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = warna)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (nilai / max).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = warna,
                trackColor = warna.copy(alpha = 0.1f)
            )
        }
    }
}

@Preview
@Composable
private fun DetailNilaiMapelScreenPreview() {
    SDM3Theme {
        DetailNilaiMapelScreen(
            studentId = "",
            subjectId = "",
            semester = "ganjil",
            onBack = {}
        )
    }
}
