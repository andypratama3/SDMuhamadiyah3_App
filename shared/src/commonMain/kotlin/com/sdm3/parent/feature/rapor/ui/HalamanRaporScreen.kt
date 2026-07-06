package com.sdm3.parent.feature.rapor.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
import com.sdm3.parent.feature.rapor.HalamanRaporViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.viewmodel.koinViewModel

private fun isRaporPublished(status: String?): Boolean =
    status?.lowercase() in listOf("published", "approved", "terbit", "signed", "final")

private fun raporStatusLabel(status: String?): String = when (status?.lowercase()) {
    "published", "approved", "terbit", "signed", "final" -> "TERBIT"
    "generated", "ready", "siap" -> "SIAP UNDUH"
    "draft", "pending", "processing", "queued", "generating" -> "DIPROSES"
    null, "" -> "-"
    else -> status.uppercase()
}

sealed class HalamanRaporUiState {
    data object Loading : HalamanRaporUiState()
    data object Empty : HalamanRaporUiState()
    data class Error(val message: String) : HalamanRaporUiState()
    data object Success : HalamanRaporUiState()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HalamanRaporScreen(
    studentId: String,
    onBack: (() -> Unit)? = null,
    onPreviewClick: (String, String) -> Unit,
    onVerifikasiClick: (String) -> Unit,
    viewModel: HalamanRaporViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.rapor.HalamanRaporUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadRapors(studentId)
        }
    }

    val uiState: HalamanRaporUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> HalamanRaporUiState.Loading
            s.errorMessage != null -> HalamanRaporUiState.Error(s.errorMessage)
            s.isEmpty -> HalamanRaporUiState.Empty
            else -> HalamanRaporUiState.Success
        }
    }

    val allSemesters = remember(vmState.rapors) {
        vmState.rapors.map { it.semesterLabel ?: it.semester }.distinct()
    }
    var selectedSemesterIndex by remember { mutableIntStateOf(0) }
    val filteredRapors = remember(vmState.rapors, selectedSemesterIndex) {
        if (selectedSemesterIndex == 0) vmState.rapors
        else {
            val target = allSemesters.getOrNull(selectedSemesterIndex - 1) ?: ""
            vmState.rapors.filter { (it.semesterLabel ?: it.semester) == target }
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val heroContent = heroContentColor()
    val statusSuccess = statusSuccessColor()

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Arsip Rapor Digital",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "DOKUMEN RESMI NEGARA",
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
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, size.height * 0.1f),
                        radius = size.width
                    )
                )
            }

            when (val state = uiState) {
                is HalamanRaporUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(340.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                        }
                        repeat(4) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .shimmerEffect()
                                )
                            }
                        }
                    }
                }

                is HalamanRaporUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Rapor",
                        message = "Belum terdapat arsip rapor digital untuk siswa ini.",
                        style = EmptyStateStyle.Neutral,
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadRapors(studentId) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                            )
                        }
                    )
                }

                is HalamanRaporUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Rapor",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadRapors(studentId) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                            )
                        },
                        secondaryAction = if (onBack != null) {
                            {
                                Sdm3OutlinedButton(
                                    text = "Kembali",
                                    onClick = onBack,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )
                            }
                        } else null,
                    )
                }

                is HalamanRaporUiState.Success -> {
                    val latestRapor = filteredRapors.firstOrNull()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        item {
                            Column {
                                Surface(
                                    color = colorScheme.secondary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = " TAHUN AJARAN ${latestRapor?.academicYear ?: "-"} ",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = latestRapor?.semesterLabel ?: latestRapor?.semester ?: "Rapor Digital",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    letterSpacing = (-1).sp
                                )
                                if (allSemesters.size > 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = selectedSemesterIndex == 0,
                                            onClick = { selectedSemesterIndex = 0 },
                                            label = { Text("Semua", fontWeight = if (selectedSemesterIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = colorScheme.primary,
                                                selectedLabelColor = colorScheme.onPrimary
                                            )
                                        )
                                        allSemesters.forEachIndexed { index, sem ->
                                            FilterChip(
                                                selected = selectedSemesterIndex == index + 1,
                                                onClick = { selectedSemesterIndex = index + 1 },
                                                label = { Text(sem, fontWeight = if (selectedSemesterIndex == index + 1) FontWeight.Bold else FontWeight.Normal) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = colorScheme.primary,
                                                    selectedLabelColor = colorScheme.onPrimary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    listOf(colorScheme.primary, colorScheme.inversePrimary.copy(alpha = 0.8f))
                                                )
                                            )
                                            .padding(24.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    modifier = Modifier.size(48.dp),
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = heroContent.copy(alpha = 0.15f)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = heroContent, modifier = Modifier.size(24.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = latestRapor?.semesterLabel?.takeIf { it.isNotBlank() }
                                                        ?: "Rapor Semester",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = heroContent
                                                )
                                            }
                                            val statusPublished = isRaporPublished(latestRapor?.status)
                                            Surface(
                                                shape = RoundedCornerShape(999.dp),
                                                color = if (statusPublished) colorScheme.secondary else heroContent.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = " ${raporStatusLabel(latestRapor?.status)} ",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (statusPublished) colorScheme.primary else heroContent,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text(
                                            text = if (latestRapor?.approvedAt != null) "Dipublikasi pada ${com.sdm3.parent.core.util.formatTanggal(latestRapor.approvedAt)}" else "Dokumen resmi negara",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = heroContent.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (latestRapor?.documentNumber != null) "Dokumen: ${latestRapor.documentNumber}" else "SDM3 Samarinda",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = heroContent.copy(alpha = 0.5f),
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Sdm3Button(
                                            text = "Download PDF Dokumen",
                                            onClick = { latestRapor?.let { onPreviewClick(it.id, it.pdfUrl ?: it.generatedPdfUrl ?: "") } },
                                            icon = Icons.Outlined.FileDownload,
                                            containerColor = colorScheme.surface,
                                            contentColor = colorScheme.primary,
                                            modifier = Modifier.fillMaxWidth().height(54.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                Sdm3OutlinedButton(
                                                    text = "Pratinjau",
                                                    onClick = { onPreviewClick(latestRapor?.id ?: "", latestRapor?.pdfUrl ?: latestRapor?.generatedPdfUrl ?: "") },
                                                    icon = Icons.Outlined.Visibility,
                                                    contentColor = heroContent
                                                )
                                            }
                                            Box(modifier = Modifier.weight(1f)) {
                                                Sdm3OutlinedButton(
                                                    text = "Verifikasi",
                                                    onClick = { onVerifikasiClick(latestRapor?.verificationCode ?: latestRapor?.id ?: "") },
                                                    icon = Icons.Outlined.QrCodeScanner,
                                                    contentColor = heroContent
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = heroContent.copy(alpha = 0.08f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Verified,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = statusSuccess
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = "Digital Signature Terautentikasi",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = heroContent,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Arsip Rapor",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        itemsIndexed(filteredRapors) { index, rapor ->
                            Sdm3Card(padding = 16.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.History, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(22.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${rapor.semesterLabel ?: rapor.semester} • ${rapor.academicYear}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Text(
                                            text = "Status: ${raporStatusLabel(rapor.status)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    Surface(
                                        modifier = Modifier.size(36.dp),
                                        shape = CircleShape,
                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                    ) {
                                        IconButton(onClick = { onPreviewClick(rapor.id, rapor.pdfUrl ?: rapor.generatedPdfUrl ?: "") }) {
                                            Icon(Icons.Outlined.FileDownload, contentDescription = "Unduh", tint = colorScheme.primary, modifier = Modifier.size(18.dp))
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

@Preview
@Composable
private fun HalamanRaporScreenPreview() {
    SDM3Theme {
        HalamanRaporScreen(studentId = "", onBack = {}, onPreviewClick = { _, _ -> }, onVerifikasiClick = {})
    }
}
