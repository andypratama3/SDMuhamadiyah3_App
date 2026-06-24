package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarUiState
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBuktiBayarScreen(
    paymentId: String,
    onBack: () -> Unit,
    viewModel: DetailBuktiBayarViewModel? = if (LocalInspectionMode.current) null else koinViewModel()
) {
    val isPreview = viewModel == null
    val uiState by if (isPreview) {
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

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "E-Kwitansi Resmi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg)
            ) {
                uiState.payment?.let { payment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = SDM3Shapes.large,
                                color = StatusSuccess.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = StatusSuccess
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Spacing.lg))
                            Text(
                                text = "Transaksi Berhasil",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusSuccess
                            )
                            Text(
                                text = "Lunas & Terverifikasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Text(
                        text = "Rincian Pembayaran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    Sdm3Card {
                        ReceiptRow("Nomor Referensi", payment.id)
                        ReceiptRow("Waktu Bayar", payment.createdAt ?: "-")
                        ReceiptRow("Metode", payment.paymentType ?: "-")

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.md),
                            color = colorScheme.outlineVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Bayar",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Rp${payment.grossAmount?.toInt() ?: 0}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = SDM3Shapes.medium,
                                color = colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "AF",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text(
                                    text = "Ahmad Fathan",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = "Kelas 4-A · NISN: 0012345678",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xxl))

                    Sdm3Button(
                        text = "Download Kwitansi PDF",
                        onClick = { },
                        icon = Icons.Outlined.FileDownload,
                        containerColor = colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Sdm3OutlinedButton(
                        text = "Bagikan Bukti",
                        onClick = { },
                        icon = Icons.Outlined.Share
                    )

                    Spacer(modifier = Modifier.height(Spacing.xxxl))
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
        horizontalArrangement = Arrangement.SpaceBetween
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
            color = colorScheme.onSurface
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
