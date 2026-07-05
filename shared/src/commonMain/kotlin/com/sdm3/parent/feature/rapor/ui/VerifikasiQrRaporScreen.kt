package com.sdm3.parent.feature.rapor.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCode2
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.rapor.VerifikasiQrRaporUiState
import com.sdm3.parent.feature.rapor.VerifikasiQrRaporViewModel
import com.sdm3.parent.platform.PlatformActions
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

sealed class VerifikasiQrUiState {
    data object Loading : VerifikasiQrUiState()
    data object Empty : VerifikasiQrUiState()
    data class Error(val message: String) : VerifikasiQrUiState()
    data class Success(
        val qrInput: String = "",
        val isLoading: Boolean = false,
        val showResult: Boolean = false,
        val errorMessage: String? = null,
        val verifyResult: VerifyResultData? = null
    ) : VerifikasiQrUiState()
}

data class VerifyResultData(
    val valid: Boolean,
    val studentName: String? = null,
    val nisn: String? = null,
    val message: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiQrRaporScreen(
    raporId: String,
    viewModel: VerifikasiQrRaporViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val state by if (isPreview) {
        remember { mutableStateOf(VerifikasiQrRaporUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(raporId) { viewModel.init(raporId) }
    }

    val scope = rememberCoroutineScope()
    val qrScanSupported = remember { !isPreview && PlatformActions.isQrScanSupported() }
    val launchQrScan: () -> Unit = {
        if (!isPreview && qrScanSupported) {
            scope.launch {
                val code = PlatformActions.scanQrCode()
                if (!code.isNullOrBlank()) {
                    viewModel.updateQrInput(code)
                }
            }
        }
    }

    val uiState: VerifikasiQrUiState = with(state) {
        when {
            isLoading && !showResult -> VerifikasiQrUiState.Loading
            errorMessage != null && !showResult -> VerifikasiQrUiState.Error(errorMessage)
            isEmpty -> VerifikasiQrUiState.Empty
            else -> VerifikasiQrUiState.Success(
                qrInput = qrInput,
                isLoading = isLoading,
                showResult = showResult,
                errorMessage = errorMessage,
                verifyResult = verifyResult?.let { result ->
                    VerifyResultData(
                        valid = result.valid,
                        studentName = result.studentName,
                        nisn = result.nisn,
                        message = result.message
                    )
                }
            )
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Verifikasi Dokumen",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "OTENTIKASI QR CODE",
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
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width * 0.5f, 0f),
                        radius = size.width
                    )
                )
            }

            when (val currentState = uiState) {
                is VerifikasiQrUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerEffect()
                        )
                    }
                }

                is VerifikasiQrUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Verifikasi",
                        message = "Silakan scan QR Code pada rapor cetak atau masukkan kode otentikasi secara manual.",
                        style = EmptyStateStyle.Informative,
                        icon = Icons.Outlined.QrCode2,
                        action = {
                            Sdm3Button(
                                text = "Mulai Verifikasi",
                                onClick = launchQrScan,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                            )
                        }
                    )
                }

                is VerifikasiQrUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Verifikasi Gagal",
                        message = currentState.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.reset() },
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

                is VerifikasiQrUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 24.dp) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(220.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(colorScheme.primary.copy(alpha = 0.03f))
                                        .border(2.dp, colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                                        .then(if (qrScanSupported) Modifier.clickable(onClick = launchQrScan) else Modifier),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = colorScheme.primary.copy(alpha = 0.2f)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (qrScanSupported) "Scan QR Code Resmi\ndi Rapor Cetak"
                                                else "Pindai kamera belum tersedia.\nMasukkan kode secara manual di bawah.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.primary.copy(alpha = 0.4f),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        SectionHeader(title = "Input Manual", modifier = Modifier.padding(top = 8.dp))

                        Sdm3TextField(
                            value = currentState.qrInput,
                            onValueChange = { if (!isPreview) viewModel.updateQrInput(it) },
                            label = "KODE OTENTIKASI",
                            placeholder = "Masukkan string QR...",
                            leadingIcon = Icons.Outlined.QrCode2
                        )

                        Sdm3Button(
                            text = "Validasi Dokumen",
                            onClick = { viewModel.verify() },
                            isLoading = currentState.isLoading,
                            enabled = !currentState.isLoading && currentState.qrInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )

                        AnimatedVisibility(visible = currentState.showResult) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                currentState.errorMessage?.let { error ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = colorScheme.error.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(20.dp), tint = colorScheme.error)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(error, style = MaterialTheme.typography.bodySmall, color = colorScheme.error, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                val result = currentState.verifyResult
                                if (result != null) {
                                    Sdm3Card(padding = 24.dp) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    modifier = Modifier.size(56.dp),
                                                    shape = CircleShape,
                                                    color = (if (result.valid) statusSuccess else colorScheme.error).copy(alpha = 0.1f)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = if (result.valid) Icons.Default.Verified else Icons.Default.NewReleases,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(32.dp),
                                                            tint = if (result.valid) statusSuccess else colorScheme.error
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Column {
                                                    Text(
                                                        text = if (result.valid) "DOKUMEN VALID" else "DOKUMEN TIDAK SAH",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Black,
                                                        letterSpacing = 1.sp,
                                                        color = if (result.valid) statusSuccess else colorScheme.error
                                                    )
                                                    Text(
                                                        text = if (result.valid) "Terautentikasi" else "Gagal Verifikasi",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.primary
                                                    )
                                                }
                                            }

                                            result.message?.takeIf { it.isNotBlank() }?.let { msg ->
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = msg,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = colorScheme.primary.copy(alpha = 0.7f),
                                                    lineHeight = 20.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(24.dp))
                                            SectionHeader(title = "Detail Siswa", modifier = Modifier.alpha(0.5f))
                                            Spacer(modifier = Modifier.height(12.dp))

                                            result.studentName?.let { VerifInfoRow("NAMA LENGKAP", it) }
                                            HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 10.dp))
                                            result.nisn?.let { VerifInfoRow("NOMOR INDUK", it) }
                                        }
                                    }

                                    Sdm3OutlinedButton(
                                        text = "Ulangi Verifikasi",
                                        onClick = { viewModel.reset() },
                                        icon = Icons.Default.Refresh,
                                        contentColor = colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifInfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
    }
}

@Preview
@Composable
private fun VerifikasiQrRaporScreenPreview() {
    SDM3Theme {
        VerifikasiQrRaporScreen(raporId = "", onBack = {})
    }
}
