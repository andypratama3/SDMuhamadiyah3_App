package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ContentCopy

import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.component.ScreenUiState
import com.sdm3.parent.core.designsystem.component.resolveScreenState
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.platform.PlatformActions
import com.sdm3.parent.feature.pembayaran.ProsesPembayaranViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProsesPembayaranScreen(
    paymentId: String,
    onBack: () -> Unit,
    onPembayaranBerhasil: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val statusDanger = statusDangerColor()
    val viewModel: ProsesPembayaranViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.ProsesPembayaranUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
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
        // Polling status otomatis selama masih menunggu pembayaran.
        LaunchedEffect(paymentId) {
            while (true) {
                delay(7000)
                val st = viewModel.uiState.value.status
                if (st == com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.SUCCESS ||
                    st == com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.FAILED
                ) break
                viewModel.pollStatusSilently()
            }
        }
    }

    ScreenScaffold(
        title = "Instruksi Bayar",
        subtitle = when (vmState.status) {
            com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.SUCCESS -> "PEMBAYARAN BERHASIL"
            com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.FAILED -> "PEMBAYARAN GAGAL"
            com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.PROCESSING -> "MEMPROSES"
            else -> "MENUNGGU PEMBAYARAN"
        },
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            when (val state = uiState) {
                is ScreenUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .safeDrawingPadding()
                            .paymentHorizontalPadding()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
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
                        Spacer(modifier = Modifier.height(Spacing.xxxxxl))
                    }
                }

                is ScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Instruksi",
                        message = "Instruksi pembayaran tidak tersedia.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding()
                    )
                }

                is ScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Instruksi",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding(),
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadPaymentInstructions(paymentId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is ScreenUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .safeDrawingPadding()
                            .paymentHorizontalPadding()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Spacer(modifier = Modifier.height(Spacing.sm))

                        Sdm3Card(padding = Spacing.xl) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    border = BorderStroke(2.dp, colorScheme.outline),
                                    shadowElevation = 8.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.AccountBalance, contentDescription = "Bank", modifier = Modifier.size(32.dp), tint = colorScheme.primary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(Spacing.lg))

                                val hasVa = vmState.vaNumber.isNotBlank()
                                val redirect = vmState.redirectUrl
                                Text(
                                    text = when {
                                        hasVa -> "NOMOR VIRTUAL ACCOUNT"
                                        !redirect.isNullOrBlank() -> "HALAMAN PEMBAYARAN"
                                        else -> "MENUNGGU INSTRUKSI"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = ProductSchoolTheme.colors.onSurfaceMuted
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                val vaStyle = when {
                                    vmState.vaNumber.length > 16 -> MaterialTheme.typography.titleMedium
                                    vmState.vaNumber.length > 12 -> MaterialTheme.typography.titleLarge
                                    else -> MaterialTheme.typography.headlineSmall
                                }
                                Text(
                                    text = when {
                                        hasVa -> vmState.vaNumber
                                        !redirect.isNullOrBlank() -> "Selesaikan pembayaran pada halaman yang disediakan"
                                        else -> "Menyiapkan instruksi pembayaran…"
                                    },
                                    style = if (hasVa) vaStyle else MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = if (hasVa) 0.5.sp else 0.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    lineHeight = if (hasVa) 28.sp else 22.sp
                                )

                                Spacer(modifier = Modifier.height(Spacing.lg))

                                if (hasVa) {
                                    Sdm3Button(
                                        text = "Salin Nomor VA",
                                        onClick = { PlatformActions.copyToClipboard(vmState.vaNumber, "Nomor VA") },
                                        icon = Icons.Default.ContentCopy,
                                        containerColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth().height(52.dp)
                                    )
                                } else if (!redirect.isNullOrBlank()) {
                                    Sdm3Button(
                                        text = "Buka Halaman Pembayaran",
                                        onClick = { PlatformActions.openUrl(redirect) },
                                        containerColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth().height(52.dp)
                                    )
                                }
                            }
                        }

                        Sdm3Card(padding = Spacing.lg) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TOTAL PEMBAYARAN",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = ProductSchoolTheme.colors.onSurfaceMuted
                                    )
                                    Text(
                                        text = com.sdm3.parent.core.util.formatRupiah(vmState.grossAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                }
                                val statusColor = when (vmState.status) {
                                    com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.SUCCESS -> statusSuccess
                                    com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.FAILED -> statusDanger
                                    else -> statusWarning
                                }
                                val statusLabel = when (vmState.status) {
                                    com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.SUCCESS -> "BERHASIL"
                                    com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.FAILED -> "GAGAL"
                                    com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.PROCESSING -> "MEMPROSES"
                                    else -> "MENUNGGU BAYAR"
                                }
                                Surface(
                                    color = statusColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = " $statusLabel ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xs)
                                    )
                                }
                            }
                        }

                        SectionHeader(title = "Langkah Pembayaran", modifier = Modifier.padding(top = Spacing.xs))

                        // Instruksi menyesuaikan metode: Virtual Account (transfer m-banking)
                        // vs halaman pembayaran (e-wallet/kartu/gerai via Midtrans).
                        val steps = if (vmState.vaNumber.isNotBlank()) {
                            listOf(
                                "Buka aplikasi mobile banking Anda",
                                "Pilih menu Transfer > Virtual Account",
                                "Masukkan nomor ${vmState.vaNumber}",
                                "Konfirmasi rincian dan nominal tagihan",
                                "Masukkan PIN transaksi Anda",
                                "Simpan resi sebagai bukti otentik"
                            )
                        } else {
                            listOf(
                                "Tekan tombol \"Buka Halaman Pembayaran\" di atas",
                                "Pilih metode pembayaran yang tersedia (e-wallet, kartu, atau gerai)",
                                "Ikuti instruksi pada halaman pembayaran hingga selesai",
                                "Kembali ke aplikasi — status diperbarui otomatis",
                                "Simpan bukti pembayaran dari penyedia"
                            )
                        }

                        Sdm3Card(padding = Spacing.lg) {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                steps.forEachIndexed { index, step ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary,
                                            modifier = Modifier.width(Spacing.xl)
                                        )
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = ProductSchoolTheme.colors.onSurfaceMuted,
                                            lineHeight = 24.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        if (vmState.status == com.sdm3.parent.feature.pembayaran.PaymentProcessStatus.FAILED) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = colorScheme.error.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(Spacing.sm)
                            ) {
                                Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ErrorOutline, contentDescription = "Error", modifier = Modifier.size(Spacing.lg), tint = colorScheme.error)
                                    Spacer(modifier = Modifier.width(Spacing.sm))
                                    Text(
                                        text = "Pembayaran gagal atau kedaluwarsa. Silakan ulangi transaksi.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Sdm3Button(
                                text = "Kembali",
                                onClick = onBack,
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        } else {
                            Sdm3Button(
                                text = if (vmState.isLoading) "Memverifikasi..." else "Konfirmasi Sudah Bayar",
                                onClick = { viewModel.pollStatus() },
                                isLoading = vmState.isLoading,
                                containerColor = colorScheme.secondary,
                                contentColor = colorScheme.onSecondary,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.paymentBottomSafePadding()
                        )
                    }
                }
            }
        }
    }
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
