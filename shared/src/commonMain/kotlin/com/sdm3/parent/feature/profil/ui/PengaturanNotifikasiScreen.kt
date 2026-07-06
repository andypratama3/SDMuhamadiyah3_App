package com.sdm3.parent.feature.profil.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.component.EmptyStateStyle
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.feature.profil.PengaturanNotifikasiUiState
import com.sdm3.parent.feature.profil.PengaturanNotifikasiViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed class PengaturanNotifUiState {
    data object Loading : PengaturanNotifUiState()
    data object Empty : PengaturanNotifUiState()
    data class Error(val message: String) : PengaturanNotifUiState()
    data object Success : PengaturanNotifUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanNotifikasiScreen(
    onBack: () -> Unit,
    viewModel: PengaturanNotifikasiViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember { mutableStateOf(PengaturanNotifikasiUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    var masterToggle by remember { mutableStateOf(true) }
    var nilaiToggle by remember { mutableStateOf(true) }
    var tagihanToggle by remember { mutableStateOf(true) }
    var pengumumanToggle by remember { mutableStateOf(true) }
    var kehadiranToggle by remember { mutableStateOf(true) }
    var raporToggle by remember { mutableStateOf(false) }

    LaunchedEffect(vmState) {
        if (!isPreview) {
            val s = vmState.settings
            masterToggle = s.pushEnabled
            nilaiToggle = s.nilaiNotif
            tagihanToggle = s.tagihanNotif
            pengumumanToggle = s.pengumumanNotif
            kehadiranToggle = s.kehadiranNotif
            raporToggle = s.raporNotif
        }
    }

    val errorMessage = vmState.errorMessage

    val screenState = remember(vmState.isLoading, vmState.isEmpty, errorMessage, isPreview) {
        if (isPreview) PengaturanNotifUiState.Success
        else when {
            vmState.isLoading && vmState.isEmpty -> PengaturanNotifUiState.Loading
            errorMessage != null -> PengaturanNotifUiState.Error(errorMessage)
            !vmState.isLoading && vmState.isEmpty -> PengaturanNotifUiState.Empty
            else -> PengaturanNotifUiState.Success
        }
    }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            viewModel.loadSettings()
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Konfigurasi Notifikasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "PREFERENSI NOTIFIKASI",
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
                            Icons.AutoMirrored.Filled.ArrowBack,
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
            // Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            when (screenState) {
                is PengaturanNotifUiState.Loading -> {
                    ShimmerPengaturanNotif()
                }
                is PengaturanNotifUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3EmptyState(
                            title = "Tidak Ada Pengaturan",
                            message = "Pengaturan notifikasi belum tersedia.",
                            style = EmptyStateStyle.Neutral
                        )
                    }
                }
                is PengaturanNotifUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3ErrorState(
                            title = "Gagal Memuat",
                            message = screenState.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.loadSettings() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }
                }
                is PengaturanNotifUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        SectionHeader(title = "KONTROL UTAMA", modifier = Modifier.alpha(0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 12.dp) {
                            ToggleRow(label = "Aktifkan Seluruh Notifikasi", enabled = true, isOn = masterToggle, onToggle = { masterToggle = it; if (!isPreview) viewModel.togglePush() }, isMaster = true)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader(title = "KATEGORI INFORMASI", modifier = Modifier.alpha(0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 8.dp) {
                            Column {
                                ToggleRow(label = "Pembaruan Nilai Akademik", enabled = masterToggle, isOn = nilaiToggle, onToggle = { nilaiToggle = it; if (!isPreview) viewModel.toggleNilai() })
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                                ToggleRow(label = "Tagihan & Administrasi", enabled = masterToggle, isOn = tagihanToggle, onToggle = { tagihanToggle = it; if (!isPreview) viewModel.toggleTagihan() })
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                                ToggleRow(label = "Pengumuman Institusi", enabled = masterToggle, isOn = pengumumanToggle, onToggle = { pengumumanToggle = it; if (!isPreview) viewModel.togglePengumuman() })
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                                ToggleRow(label = "Presensi Real-time", enabled = masterToggle, isOn = kehadiranToggle, onToggle = { kehadiranToggle = it; if (!isPreview) viewModel.toggleKehadiran() })
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))
                                ToggleRow(label = "Ketersediaan Rapor", enabled = masterToggle, isOn = raporToggle, onToggle = { raporToggle = it; if (!isPreview) viewModel.toggleRapor() })
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Surface(
                            color = colorScheme.primary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Preferensi ini akan diterapkan pada seluruh perangkat yang terhubung dengan akun Anda. Pastikan koneksi internet stabil untuk menerima notifikasi tepat waktu.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.padding(16.dp),
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerPengaturanNotif() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.3f).height(14.dp).clip(RoundedCornerShape(4.dp)).alpha(0.5f).shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Sdm3Card(padding = 12.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(0.5f).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                )
                Box(
                    modifier = Modifier.size(48.dp, 28.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).clip(RoundedCornerShape(4.dp)).alpha(0.5f).shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Sdm3Card(padding = 8.dp) {
            repeat(5) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                    Box(
                        modifier = Modifier.size(48.dp, 28.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect()
                    )
                }
                if (index < 4) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colorScheme.primary.copy(alpha = 0.05f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect()
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun ToggleRow(
    label: String,
    enabled: Boolean,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    isMaster: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = if (isMaster) 12.dp else 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isMaster) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isMaster) FontWeight.Bold else FontWeight.Medium,
            color = if (enabled) colorScheme.primary else colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isOn,
            onCheckedChange = { if (enabled) onToggle(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.onPrimary,
                checkedTrackColor = colorScheme.secondary,
                uncheckedThumbColor = colorScheme.primary.copy(alpha = 0.1f),
                uncheckedTrackColor = colorScheme.primary.copy(alpha = 0.05f),
                disabledCheckedTrackColor = colorScheme.secondary.copy(alpha = 0.5f),
                disabledUncheckedTrackColor = colorScheme.primary.copy(alpha = 0.02f)
            )
        )
    }
}

@Preview
@Composable
fun PengaturanNotifikasiScreenPreview() {
    SDM3Theme {
        PengaturanNotifikasiScreen(onBack = {})
    }
}
