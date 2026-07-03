package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.platform.PlatformActions
import com.sdm3.parent.feature.pembayaran.ProsesPembayaranViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

sealed class ProsesPembayaranUiState {
    data object Loading : ProsesPembayaranUiState()
    data object Empty : ProsesPembayaranUiState()
    data class Error(val message: String = "Silakan coba kembali.") : ProsesPembayaranUiState()
    data object Success : ProsesPembayaranUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProsesPembayaranScreen(
    paymentId: String,
    onBack: () -> Unit,
    onPembayaranBerhasil: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel: ProsesPembayaranViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.ProsesPembayaranUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: ProsesPembayaranUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> ProsesPembayaranUiState.Loading
            s.errorMessage != null -> ProsesPembayaranUiState.Error(s.errorMessage)
            s.isEmpty -> ProsesPembayaranUiState.Empty
            else -> ProsesPembayaranUiState.Success
        }
    }

    if (!isPreview) {
        LaunchedEffect(paymentId) {
            viewModel.loadPaymentInstructions(paymentId)
        }
        LaunchedEffect(vmState.status) {
            if (vmState.status == com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.SUCCESS) {
                onPembayaranBerhasil()
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Instruksi Bayar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "PENDING TRANSFER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = colorScheme.primary)
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
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            when (val state = uiState) {
                is ProsesPembayaranUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(20.dp))
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
                                .height(200.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                is ProsesPembayaranUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Instruksi",
                        message = "Instruksi pembayaran tidak tersedia.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier.padding(padding)
                    )
                }

                is ProsesPembayaranUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Instruksi",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.padding(padding),
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadPaymentInstructions(paymentId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is ProsesPembayaranUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 24.dp) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = colorScheme.primary.copy(alpha = 0.05f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.AccountBalance, contentDescription = null, modifier = Modifier.size(32.dp), tint = colorScheme.primary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "NOMOR VIRTUAL ACCOUNT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = colorScheme.primary.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = vmState.vaNumber,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Sdm3Button(
                                    text = "Salin Nomor VA",
                                    onClick = { PlatformActions.copyToClipboard(vmState.vaNumber, "Nomor VA") },
                                    icon = Icons.Default.ContentCopy,
                                    containerColor = colorScheme.primary,
                                    contentColor = Color.White,
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                )
                            }
                        }

                        Sdm3Card(padding = 20.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL PEMBAYARAN",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = colorScheme.primary.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = formatCurrency(vmState.grossAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                }
                                Surface(
                                    color = StatusWarning.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = " ${vmState.status.name.replace('_', ' ')} ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = StatusWarning,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        SectionHeader(title = "Langkah Pembayaran", modifier = Modifier.padding(top = 8.dp))

                        val steps = listOf(
                            "Buka aplikasi mobile banking Anda",
                            "Pilih menu Transfer > Virtual Account",
                            "Masukkan nomor ${vmState.vaNumber}",
                            "Konfirmasi rincian dan nominal tagihan",
                            "Masukkan PIN transaksi Anda",
                            "Simpan resi sebagai bukti otentik"
                        )

                        Sdm3Card(padding = 20.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                steps.forEachIndexed { index, step ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary,
                                            modifier = Modifier.width(24.dp)
                                        )
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colorScheme.primary.copy(alpha = 0.7f),
                                            lineHeight = 24.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Sdm3Button(
                            text = if (vmState.isLoading) "Memverifikasi..." else "Konfirmasi Sudah Bayar",
                            onClick = { viewModel.pollStatus() },
                            isLoading = vmState.isLoading,
                            containerColor = colorScheme.secondary,
                            contentColor = colorScheme.primary,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Number): String {
    val s = amount.toLong().toString()
    val sb = StringBuilder()
    for (i in s.indices) {
        if (i > 0 && (s.length - i) % 3 == 0) sb.append('.')
        sb.append(s[i])
    }
    return "Rp$sb"
}

@Preview
@Composable
private fun ProsesPembayaranScreenPreview() {
    SDM3Theme {
        ProsesPembayaranScreen(
            paymentId = "",
            onBack = {},
            onPembayaranBerhasil = {}
        )
    }
}
