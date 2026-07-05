package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.sdm3.parent.feature.pembayaran.PembayaranBerhasilViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

sealed class PembayaranBerhasilUiState {
    data object Loading : PembayaranBerhasilUiState()
    data object Empty : PembayaranBerhasilUiState()
    data class Error(val message: String = "Silakan coba kembali.") : PembayaranBerhasilUiState()
    data object Success : PembayaranBerhasilUiState()
}

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
    val glassSurface = glassSurfaceColor()
    val glassBorder = glassBorderColor()
    val viewModel: PembayaranBerhasilViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.PembayaranBerhasilUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: PembayaranBerhasilUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> PembayaranBerhasilUiState.Loading
            s.errorMessage != null -> PembayaranBerhasilUiState.Error(s.errorMessage)
            s.isEmpty -> PembayaranBerhasilUiState.Empty
            else -> PembayaranBerhasilUiState.Success
        }
    }
    if (!isPreview) {
        LaunchedEffect(paymentId) {
            viewModel.loadTransaction(paymentId)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is PembayaranBerhasilUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(36.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                    }
                }

                is PembayaranBerhasilUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Data",
                        message = "Data pembayaran tidak tersedia.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier.padding(padding),
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadTransaction(paymentId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is PembayaranBerhasilUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Konfirmasi Gagal",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.padding(padding),
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

                is PembayaranBerhasilUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(statusSuccess, Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 0.4f),
                                    radius = size.width
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(110.dp),
                                shape = RoundedCornerShape(36.dp),
                                color = glassSurface,
                                border = BorderStroke(2.dp, glassBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp),
                                        tint = statusSuccess
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))

                            Text(
                                text = "Konfirmasi Berhasil",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary,
                                textAlign = TextAlign.Center,
                                letterSpacing = (-1).sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Dana telah terotentikasi oleh sistem sekolah dan masuk ke rekapitulasi pembayaran.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(48.dp))

                            Sdm3Card(padding = 24.dp) {
                                Column {
                                    if (vmState.paymentTitle.isNotBlank()) {
                                        SuccessRow("Item Akademik", vmState.paymentTitle.uppercase())
                                        HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                    }
                                    SuccessRow("Total Transaksi", formatCurrency(vmState.amount))
                                    if (vmState.paymentMethod.isNotBlank()) {
                                        HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                        SuccessRow("Metode Bayar", com.sdm3.parent.core.util.formatPaymentMethod(vmState.paymentMethod))
                                    }
                                    if (vmState.paidAt.isNotBlank()) {
                                        HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                                        SuccessRow("Waktu Bayar", com.sdm3.parent.core.util.formatTanggalWaktu(vmState.paidAt))
                                    }
                                    HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
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

                            Spacer(modifier = Modifier.height(48.dp))

                            Sdm3Button(
                                text = "Lihat E-Kwitansi",
                                onClick = onLihatBukti,
                                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                containerColor = colorScheme.secondary,
                                contentColor = colorScheme.primary,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Sdm3OutlinedButton(
                                text = "Kembali Ke Portal",
                                onClick = onKembali,
                                contentColor = colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
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
private fun PembayaranBerhasilScreenPreview() {
    SDM3Theme {
        PembayaranBerhasilScreen(
            paymentId = "test",
            onLihatBukti = {},
            onKembali = {}
        )
    }
}
