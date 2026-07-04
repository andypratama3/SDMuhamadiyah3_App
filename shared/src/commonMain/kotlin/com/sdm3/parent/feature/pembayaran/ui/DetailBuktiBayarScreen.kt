package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarUiState
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.platform.PlatformActions
import org.koin.compose.viewmodel.koinViewModel

sealed class DetailBayarUiState {
    data object Loading : DetailBayarUiState()
    data object Empty : DetailBayarUiState()
    data class Error(val message: String = "Silakan coba kembali.") : DetailBayarUiState()
    data object Success : DetailBayarUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBuktiBayarScreen(
    paymentId: String,
    onBack: () -> Unit,
    viewModel: DetailBuktiBayarViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember { mutableStateOf(DetailBuktiBayarUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    if (!isPreview) {
        LaunchedEffect(paymentId) {
            viewModel.loadPaymentDetail(paymentId)
        }
    }

    val uiState: DetailBayarUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> DetailBayarUiState.Loading
            s.errorMessage != null -> DetailBayarUiState.Error(s.errorMessage)
            s.isEmpty || s.payment == null -> DetailBayarUiState.Empty
            else -> DetailBayarUiState.Success
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Kwitansi Digital",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = colorScheme.primary
                    )
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
        val glowColor = colorScheme.primaryContainer
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        radius = size.width * 1.5f
                    )
                )
            }

        when (val state = uiState) {
            is DetailBayarUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.xl)
                ) {
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }
            }

            is DetailBayarUiState.Empty -> {
                Sdm3EmptyState(
                    title = "Tidak Ada Bukti",
                    message = "Data pembayaran tidak ditemukan.",
                    style = EmptyStateStyle.Neutral,
                    modifier = Modifier.padding(padding),
                    action = {
                        Sdm3Button(
                            text = "Kembali",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }

            is DetailBayarUiState.Error -> {
                Sdm3ErrorState(
                    title = "Gagal Memuat Bukti",
                    message = state.message,
                    style = ErrorStateStyle.Generic,
                    modifier = Modifier.padding(padding),
                    primaryAction = {
                        Sdm3Button(
                            text = "Coba Lagi",
                            onClick = { viewModel.loadPaymentDetail(paymentId) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    secondaryAction = {
                        Sdm3OutlinedButton(
                            text = "Kembali",
                            onClick = onBack,
                            contentColor = colorScheme.primary
                        )
                    }
                )
            }

            is DetailBayarUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.xl)
                ) {
                    vmState.payment?.let { payment ->
                        val statusLower = payment.status.lowercase()
                        val isPaid = statusLower in listOf("success", "settlement", "lunas", "paid", "capture", "completed")
                        val isFailed = statusLower in listOf("failed", "failure", "expire", "expired", "deny", "cancel", "cancelled", "refunded")
                        val statusBadge = when {
                            isPaid -> "TRANSAKSI BERHASIL"
                            isFailed -> "TRANSAKSI GAGAL"
                            else -> "MENUNGGU PEMBAYARAN"
                        }
                        val statusHeadline = when {
                            isPaid -> "Lunas & Terverifikasi"
                            isFailed -> "Pembayaran Gagal"
                            else -> "Menunggu Pembayaran"
                        }
                        val statusColor = when {
                            isPaid -> StatusSuccess
                            isFailed -> colorScheme.error
                            else -> StatusWarning
                        }
                        val statusIcon = when {
                            isPaid -> Icons.Outlined.CheckCircle
                            isFailed -> Icons.Outlined.Cancel
                            else -> Icons.Outlined.HourglassEmpty
                        }
                        // School Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SD MUHAMMADIYAH 3 SAMARINDA",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "KWITANSI DIGITAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary.copy(alpha = 0.4f),
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Sdm3Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.lg),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(72.dp),
                                    shape = CircleShape,
                                    color = statusColor.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            statusIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = statusColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(Spacing.lg))
                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = " $statusBadge ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = statusHeadline,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        Text(
                            text = "Rincian Pembayaran",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))

                        Sdm3Card {
                            ReceiptRow("Nomor Referensi", payment.orderId.ifBlank { payment.id }.uppercase())
                            ReceiptRow("Waktu Bayar", com.sdm3.parent.core.util.formatTanggalWaktu(payment.paidAt ?: payment.createdAt))
                            ReceiptRow("Metode", com.sdm3.parent.core.util.formatPaymentMethod(payment.paymentType))

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.md),
                                color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Bayar",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = com.sdm3.parent.core.util.formatRupiah(payment.grossAmount ?: 0.0),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        Sdm3Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val studentName = payment.studentName ?: payment.paymentTitle?.name ?: "Santri"
                                val initials = com.sdm3.parent.core.util.nameInitials(studentName)
                                Surface(
                                    modifier = Modifier.size(52.dp),
                                    shape = CircleShape,
                                    color = colorScheme.primary.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = initials,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(Spacing.md))
                                Column {
                                    Text(
                                        text = payment.studentName ?: payment.paymentTitle?.name ?: "Santri",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                    Text(
                                        text = buildString {
                                            if (!payment.studentClass.isNullOrBlank()) {
                                                append(com.sdm3.parent.core.util.formatClassName(payment.studentClass))
                                            }
                                            if (!payment.studentNisn.isNullOrBlank()) {
                                                if (isNotEmpty()) append(" · ")
                                                append("NISN: ${payment.studentNisn}")
                                            }
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xxl))

                        Sdm3Button(
                            text = "Download Kwitansi PDF",
                            onClick = {
                                payment.paymentUrl?.let { PlatformActions.openUrl(it) }
                            },
                            icon = Icons.Outlined.FileDownload,
                            enabled = payment.paymentUrl != null
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Sdm3OutlinedButton(
                            text = "Bagikan Bukti",
                            onClick = {
                                val shareText = buildString {
                                    appendLine("Bukti Pembayaran SD Muhammadiyah 3 Samarinda")
                                    appendLine("Ref: ${payment.orderId.ifBlank { payment.id }.uppercase()}")
                                    appendLine("Total: ${com.sdm3.parent.core.util.formatRupiah(payment.grossAmount ?: 0.0)}")
                                    appendLine("Metode: ${com.sdm3.parent.core.util.formatPaymentMethod(payment.paymentType)}")
                                    (payment.paidAt ?: payment.createdAt)?.let {
                                        appendLine("Waktu: ${com.sdm3.parent.core.util.formatTanggalWaktu(it)}")
                                    }
                                }
                                PlatformActions.shareText(shareText.trim(), "Bukti Pembayaran")
                            },
                            icon = Icons.Outlined.Share,
                            contentColor = colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun DetailBuktiBayarScreenPreview() {
    SDM3Theme {
        DetailBuktiBayarScreen(
            paymentId = "test",
            onBack = {}
        )
    }
}
