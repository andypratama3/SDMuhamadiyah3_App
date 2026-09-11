package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
    }

    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()

    ScreenScaffold(
        title = "Profil Siswa",
        subtitle = "IDENTITAS RESMI",
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            when (val state = uiState) {
                is ScreenUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
                    ) {
                        item {
                            Sdm3Card(padding = Spacing.xl) {
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
                                    Spacer(modifier = Modifier.height(Spacing.xl))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(28.dp)
                                            .clip(RoundedCornerShape(Spacing.xs))
                                            .shimmerEffect()
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.4f)
                                            .height(22.dp)
                                            .clip(RoundedCornerShape(Spacing.xs))
                                            .shimmerEffect()
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.lg))
                                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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
                                    .clip(RoundedCornerShape(Spacing.xs))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(Spacing.md))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(Spacing.xs))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(Spacing.md))
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
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
                        title = "Data Siswa Tidak Ditemukan",
                        message = "Informasi profil siswa belum tersedia.",
                        style = EmptyStateStyle.Neutral,
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadStudentDetail(studentId) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)
                            )
                        }
                    )
                }

                is ScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Profil",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadStudentDetail(studentId) },
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
                    ) {
                        item {
                            Sdm3Card(padding = Spacing.xl) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val student = vmState.student
                                    val initials = com.sdm3.parent.core.util.nameInitials(student?.name)

                                    Surface(
                                        modifier = Modifier.size(110.dp),
                                        shape = RoundedCornerShape(32.dp),
                                        color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        border = BorderStroke(2.dp, colorScheme.primary.copy(alpha = 0.2f)),
                                        shadowElevation = 8.dp
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

                                    Spacer(modifier = Modifier.height(Spacing.xl))

                                    Text(
                                        text = student?.name ?: "",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xs))
                                    student?.className?.let { className ->
                                        Sdm3CategoryBadge(text = className)
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.lg))

                                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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
                                modifier = Modifier.padding(top = Spacing.xs)
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Sdm3Card(padding = Spacing.md) {
                                Column {
                                    val biodata = vmState.student
                                    Sdm3InfoRow("Tempat Lahir", biodata?.birthPlace ?: "")
                                    HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                    Sdm3InfoRow("Tanggal Lahir", biodata?.birthDate ?: "")
                                    HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                    Sdm3InfoRow("Wali Kelas", biodata?.waliKelas ?: "-")
                                    HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                    Sdm3InfoRow("ID Akun Siswa", biodata?.portalId ?: "-")
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Eksplorasi Akademik",
                                modifier = Modifier.padding(top = Spacing.xs)
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Analitik Nilai",
                                    icon = Icons.Outlined.Assessment,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.NilaiRapor(studentId, "")) }
                                )
                                QuickNavItem(
                                    modifier = Modifier.weight(1f),
                                    title = "Rapor Digital",
                                    icon = Icons.Outlined.Description,
                                    color = colorScheme.primary,
                                    onClick = { onQuickNavClick(SDM3Route.HalamanRapor(studentId)) }
                                )
                            }
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
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

                        item { Spacer(modifier = Modifier.height(Spacing.xxl)) }
                    }
                }
            }
        }
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
        padding = Spacing.lg
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Sdm3IconBadge(
                icon = icon,
                size = 52.dp,
                iconSize = 26.dp,
                iconTint = color,
                backgroundColor = color.copy(alpha = 0.12f),
                borderColor = color.copy(alpha = 0.2f),
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
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
