package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.sdm3.parent.feature.pembayaran.PembayaranBerhasilViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranBerhasilScreen(
    paymentId: String,
    onLihatBukti: () -> Unit,
    onKembali: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val viewModel: PembayaranBerhasilViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.PembayaranBerhasilUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
    }
    if (!isPreview) {
        LaunchedEffect(paymentId) {
            viewModel.loadTransaction(paymentId)
        }
    }

    ScreenScaffold(
        title = "",
        onBack = onKembali,
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
                            .paymentHorizontalPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(56.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(56.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .shimmerEffect()
                        )
                    }
                }

                is ScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Data",
                        message = "Data pembayaran tidak tersedia.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding(),
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadTransaction(paymentId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is ScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Konfirmasi Gagal",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding(),
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadTransaction(paymentId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        secondaryAction = {
                            Sdm3OutlinedButton(
                                text = "Kembali",
                                onClick = onKembali,
                                contentColor = colorScheme.primary
                            )
                        }
                    )
                }

                is ScreenUiState.Success -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val useCompactTitle = maxHeight < 700.dp

                        Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                                    .safeDrawingPadding()
                                    .verticalScroll(rememberScrollState())
                                    .paymentHorizontalPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(Spacing.xxl))

                                Surface(
                                    modifier = Modifier.size(130.dp),
                                    shape = RoundedCornerShape(36.dp),
                                    color = colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    border = BorderStroke(3.dp, colorScheme.outline),
                                    shadowElevation = 14.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircle,
                                            contentDescription = "Pembayaran berhasil",
                                            modifier = Modifier.size(70.dp),
                                            tint = statusSuccess
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(Spacing.xxxl))

                                Text(
                                    text = "Konfirmasi Berhasil",
                                    style = if (useCompactTitle) {
                                        MaterialTheme.typography.titleLarge
                                    } else {
                                        MaterialTheme.typography.displaySmall
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = if (useCompactTitle) (-0.5).sp else (-1).sp
                                )

                                Spacer(modifier = Modifier.height(Spacing.sm))

                                Text(
                                    text = "Dana telah terotentikasi oleh sistem sekolah dan masuk ke rekapitulasi pembayaran.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 26.sp,
                                    modifier = Modifier.padding(horizontal = Spacing.md)
                                )

                                Spacer(modifier = Modifier.height(Spacing.xxxxl))

                                 Sdm3GlassCard(padding = Spacing.xl) {
                                     Column {
                                         if (vmState.paymentTitle.isNotBlank()) {
                                            SuccessRow("Item Akademik", vmState.paymentTitle.uppercase())
                                            HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                        }
                                         SuccessRow("Total Transaksi", com.sdm3.parent.core.util.formatRupiah(vmState.amount))
                                        if (vmState.paymentMethod.isNotBlank()) {
                                            HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                            SuccessRow("Metode Bayar", com.sdm3.parent.core.util.formatPaymentMethod(vmState.paymentMethod))
                                        }
                                        if (vmState.paidAt.isNotBlank()) {
                                            HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                            SuccessRow("Waktu Bayar", com.sdm3.parent.core.util.formatTanggalWaktu(vmState.paidAt))
                                        }
                                        HorizontalDivider(color = colorScheme.outline, modifier = Modifier.padding(vertical = Spacing.sm))
                                        val statusLower = vmState.status.lowercase()
                                        val statusLabel = when {
                                            statusLower in listOf("settlement", "success", "capture", "paid", "lunas", "completed") -> "LUNAS & VERIF"
                                            statusLower in listOf("failed", "failure", "expire", "expired", "deny", "cancel", "cancelled", "refunded") -> "GAGAL"
                                            statusLower.isBlank() -> if (vmState.amount > 0L) "LUNAS & VERIF" else "PENDING"
                                            else -> "MENUNGGU"
                                        }
                                        SuccessRow("Status Audit", statusLabel)
                                    }
                                }

                                Spacer(modifier = Modifier.height(Spacing.xxxxl))

                                Sdm3Button(
                                    text = "Lihat E-Kwitansi",
                                    onClick = onLihatBukti,
                                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                    containerColor = colorScheme.secondary,
                                    contentColor = colorScheme.onSecondary,
                                    modifier = Modifier.fillMaxWidth().height(56.dp)
                                )

                                Spacer(modifier = Modifier.height(Spacing.md))

                                Sdm3OutlinedButton(
                                    text = "Kembali",
                                    onClick = onKembali,
                                    contentColor = colorScheme.primary
                                )

                                Spacer(modifier = Modifier.paymentBottomSafePadding())
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun SuccessRow(label: String, value: String) =
    Sdm3InfoRow(label = label, value = value)

@Preview
@Composable
private fun PembayaranBerhasilScreenPreview() {
    SDM3Theme {
        PembayaranBerhasilScreen(
            paymentId = "test",
            onLihatBukti = {},
            onKembali = {}
        )
    }
}
