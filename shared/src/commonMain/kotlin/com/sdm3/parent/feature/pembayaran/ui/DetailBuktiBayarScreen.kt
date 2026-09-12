package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.AppBranding
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.component.ScreenUiState
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.component.resolveScreenState
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.PaymentTitleDto
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarUiState
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarViewModel
import com.sdm3.parent.platform.PlatformActions
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBuktiBayarScreen(
    paymentId: String,
    onBack: () -> Unit,
    viewModel: DetailBuktiBayarViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmState by if (isPreview) {
        remember {
            mutableStateOf(
                DetailBuktiBayarUiState(
                    payment = PaymentDto(
                        id = "demo-pay-1",
                        orderId = "INV/2024/0001",
                        grossAmount = 250000.0,
                        paymentType = "Bank Transfer",
                        status = "settlement",
                        vaNumber = "9888001122334455",
                        studentName = "Ahmad Zaki",
                        studentClass = "5A",
                        studentNisn = "0123456789",
                        paymentTitle = PaymentTitleDto(id = "fee-1", name = "SPP Bulan Januari"),
                        paidAt = "2024-02-01T09:00:00Z",
                        createdAt = "2024-01-15T08:00:00Z"
                    )
                )
            )
        }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val snackbarHostState = remember { SnackbarHostState() }

    if (!isPreview) {
        LaunchedEffect(paymentId) {
            viewModel.loadPaymentDetail(paymentId)
        }
        LaunchedEffect(vmState.receiptUrlToOpen) {
            vmState.receiptUrlToOpen?.let { url ->
                PlatformActions.openUrl(url)
                viewModel.consumeReceiptUrl()
            }
        }
        LaunchedEffect(vmState.receiptMessage) {
            vmState.receiptMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.consumeReceiptMessage()
            }
        }
    }

    val uiState: ScreenUiState = remember(vmState) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty || vmState.payment == null, vmState.errorMessage)
    }

    ScreenScaffold(
        title = "Kwitansi Digital",
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

            is ScreenUiState.Empty -> {
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

            is ScreenUiState.Error -> {
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

            is ScreenUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .safeDrawingPadding()
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
                            isPaid -> statusSuccess
                            isFailed -> statusDangerColor()
                            else -> statusWarning
                        }
                        val statusIcon = when {
                            isPaid -> Icons.Outlined.CheckCircle
                            isFailed -> Icons.Outlined.Cancel
                            else -> Icons.Outlined.HourglassEmpty
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Receipt Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Sdm3Logo(
                                size = 64.dp,
                                showBackground = false,
                                modifier = Modifier.alpha(0.9f)
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = AppBranding.SCHOOL_NAME.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "PENERIMAAN PEMBAYARAN PENDIDIKAN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ProductSchoolTheme.colors.onSurfaceMuted,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        // Status Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = statusColor.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        statusIcon,
                                        contentDescription = statusHeadline,
                                        modifier = Modifier.size(32.dp),
                                        tint = statusColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Spacing.md))
                            StatusChip(
                                text = statusBadge,
                                color = statusColor,
                                leadingDot = false,
                                modifier = Modifier.padding(vertical = Spacing.xs),
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = statusHeadline,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.primary,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.xxl))

                        // Main Receipt Card
                        Sdm3Card(
                            modifier = Modifier.fillMaxWidth(),
                            padding = Spacing.lg
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Watermark
                                Sdm3Logo(
                                    size = 120.dp,
                                    showBackground = false,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .alpha(0.03f)
                                )

                                Column {
                                    Text(
                                        text = "RINCIAN TRANSAKSI",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.md))

                                    ReceiptRow("Nomor Referensi", payment.orderId.ifBlank { payment.id }.uppercase())
                                    ReceiptRow("Waktu Bayar", com.sdm3.parent.core.util.formatTanggalWaktu(payment.paidAt ?: payment.createdAt))
                                    ReceiptRow("Metode Pembayaran", com.sdm3.parent.core.util.formatPaymentMethod(payment.paymentType))
                                    ReceiptRow("Status Audit", statusHeadline, valueColor = statusColor)

                                    Spacer(modifier = Modifier.height(Spacing.lg))
                                    DashedDivider(color = colorScheme.outline)
                                    Spacer(modifier = Modifier.height(Spacing.lg))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "TOTAL BAYAR",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = ProductSchoolTheme.colors.onSurfaceFaint,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = statusHeadline.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = statusColor,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp,
                                            )
                                        }
                                        BoxWithConstraints(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.BottomEnd
                                        ) {
                                            val amountStyle = when {
                                                maxWidth < 100.dp -> MaterialTheme.typography.titleLarge
                                                maxWidth < 140.dp -> MaterialTheme.typography.headlineSmall
                                                else -> MaterialTheme.typography.headlineMedium
                                            }
                                            Text(
                                                text = com.sdm3.parent.core.util.formatRupiah(payment.grossAmount ?: 0.0),
                                                style = amountStyle,
                                                fontWeight = FontWeight.Black,
                                                color = colorScheme.primary,
                                                letterSpacing = (-1).sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Student Info Card
                        Sdm3Card(
                            modifier = Modifier.fillMaxWidth(),
                            padding = Spacing.md
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val studentName = payment.studentName ?: payment.paymentTitle?.name ?: "Santri"
                                val initials = com.sdm3.parent.core.util.nameInitials(studentName)
                                Surface(
                                    modifier = Modifier.size(52.dp),
                                    shape = CircleShape,
                                    color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    border = BorderStroke(2.dp, colorScheme.primary.copy(alpha = 0.2f)),
                                    shadowElevation = 4.dp
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
                                        text = studentName,
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

                        Spacer(modifier = Modifier.height(Spacing.xxxl))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            Sdm3Button(
                                text = if (vmState.isGeneratingReceipt) "Menyiapkan Kwitansi..." else "Download Kwitansi PDF",
                                onClick = { viewModel.generateReceipt(payment.id) },
                                icon = Icons.Outlined.FileDownload,
                                enabled = isPaid && !vmState.isGeneratingReceipt,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (!isPaid) {
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Kwitansi PDF tersedia setelah pembayaran lunas.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

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
                                contentColor = colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(Spacing.xxxl))
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary,
) = Sdm3InfoRow(label = label, value = value, valueColor = valueColor)

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    thickness: Dp = 1.dp,
) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
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
