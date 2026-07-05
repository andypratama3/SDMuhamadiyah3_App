package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.feature.infoanak.DetailInfoAnakViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.viewmodel.koinViewModel

sealed class DetailInfoAnakUiState {
    data object Loading : DetailInfoAnakUiState()
    data object Empty : DetailInfoAnakUiState()
    data class Error(val message: String) : DetailInfoAnakUiState()
    data object Success : DetailInfoAnakUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoAnakScreen(
    studentId: String,
    onBack: () -> Unit,
    onQuickNavClick: (SDM3Route) -> Unit,
    viewModel: DetailInfoAnakViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.infoanak.DetailInfoAnakUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadStudentDetail(studentId)
        }
    }

    val uiState: DetailInfoAnakUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> DetailInfoAnakUiState.Loading
            s.errorMessage != null -> DetailInfoAnakUiState.Error(s.errorMessage)
            s.isEmpty -> DetailInfoAnakUiState.Empty
            else -> DetailInfoAnakUiState.Success
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Profil Siswa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "IDENTITAS RESMI",
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
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.2f),
                        radius = size.width
                    )
                )
            }

            when (val state = uiState) {
                is DetailInfoAnakUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        item {
                            Sdm3Card(padding = 24.dp) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(32.dp))
                                            .shimmerEffect()
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .shimmerEffect()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.4f)
                                            .height(22.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .shimmerEffect()
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(32.dp)
                                                .clip(RoundedCornerShape(999.dp))
                                                .shimmerEffect()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(32.dp)
                                                .clip(RoundedCornerShape(999.dp))
                                                .shimmerEffect()
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
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
                    }
                }

                is DetailInfoAnakUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Data Siswa Tidak Ditemukan",
                        message = "Informasi profil siswa belum tersedia.",
                        style = EmptyStateStyle.Neutral,
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadStudentDetail(studentId) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                            )
                        }
                    )
                }

                is DetailInfoAnakUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Profil",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadStudentDetail(studentId) },
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

                is DetailInfoAnakUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        item {
                            Sdm3Card(padding = 24.dp) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val student = vmState.student
                                    val initials = com.sdm3.parent.core.util.nameInitials(student?.name)

                                    Surface(
                                        modifier = Modifier.size(100.dp),
                                        shape = RoundedCornerShape(32.dp),
                                        color = colorScheme.primary.copy(alpha = 0.05f),
                                        border = BorderStroke(2.dp, colorScheme.surface)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = initials,
                                                style = MaterialTheme.typography.displaySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = student?.name ?: "",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = colorScheme.secondary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(999.dp)
                                    ) {
                                        Text(
                                            text = student?.className?.let { " $it " } ?: "",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp,
                                            color = colorScheme.secondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        StatusChip(text = "NISN: ${student?.nisn ?: ""}", color = colorScheme.primary)
                                        val statusRaw = student?.status?.takeIf { it.isNotBlank() }
                                        val statusIsActive = statusRaw == null ||
                                            statusRaw.equals("aktif", ignoreCase = true) ||
                                            statusRaw.equals("active", ignoreCase = true)
                                        StatusChip(
                                            text = (statusRaw ?: "AKTIF").uppercase(),
                                            color = if (statusIsActive) statusSuccess else statusWarning
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Biodata Institusi",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Sdm3Card(padding = 16.dp) {
                                Column {
                                    val biodata = vmState.student
                                    InfoRow("Tempat Lahir", biodata?.birthPlace ?: "")
                                    HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                    InfoRow("Tanggal Lahir", biodata?.birthDate ?: "")
                                    HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                    InfoRow("Wali Kelas", biodata?.waliKelas ?: "-")
                                    HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                    InfoRow("ID Portal", biodata?.portalId ?: "-")
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Eksplorasi Akademik",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Analitik Nilai",
                                    icon = Icons.Outlined.Assessment,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                                )
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Rapor Digital",
                                    icon = Icons.Outlined.Description,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.HalamanRapor(studentId)) }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Presensi",
                                    icon = Icons.Outlined.EventAvailable,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.KehadiranSiswa(studentId)) }
                                )
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Administrasi",
                                    icon = Icons.Outlined.Payments,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.PembayaranSpp(studentId)) }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.primary.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
    }
}

@Composable
private fun QuickNavItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(
        modifier = modifier.clickable(onClick = onClick),
        padding = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun DetailInfoAnakScreenPreview() {
    SDM3Theme {
        DetailInfoAnakScreen(
            studentId = "",
            onBack = {},
            onQuickNavClick = {}
        )
    }
}
