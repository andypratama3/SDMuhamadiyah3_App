package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.pembayaran.PembayaranSppViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

sealed class PembayaranSppUiState {
    data object Loading : PembayaranSppUiState()
    data object Empty : PembayaranSppUiState()
    data class Error(val message: String = "Silakan coba kembali.") : PembayaranSppUiState()
    data object Success : PembayaranSppUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranSppScreen(
    studentId: String,
    onBack: () -> Unit,
    onBayarSekarang: (String) -> Unit,
    onDetailBukti: (String) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel: PembayaranSppViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.PembayaranSppUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: PembayaranSppUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> PembayaranSppUiState.Loading
            s.errorMessage != null -> PembayaranSppUiState.Error(s.errorMessage)
            s.isEmpty -> PembayaranSppUiState.Empty
            else -> PembayaranSppUiState.Success
        }
    }
    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadData(studentId)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Administrasi Keuangan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "STATUS PEMBAYARAN SPP",
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
                        center = Offset(size.width, size.height * 0.3f),
                        radius = size.width
                    )
                )
            }

            when (val state = uiState) {
                is PembayaranSppUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shimmerEffect()
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                        }
                        items(4) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                is PembayaranSppUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Riwayat",
                        message = "Belum ada transaksi pembayaran SPP yang tercatat.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier.padding(padding),
                        action = {
                            Sdm3Button(
                                text = "Muat Ulang",
                                onClick = { viewModel.refresh() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is PembayaranSppUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.padding(padding),
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.refresh() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is PembayaranSppUiState.Success -> {
                    val totalFees = vmState.fees.size
                    val paidFees = vmState.fees.count { it.status == "lunas" }
                    val progress = if (totalFees > 0) paidFees.toFloat() / totalFees.toFloat() else 0f
                    val progressPercent = (progress * 100).toInt()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                colors = listOf(colorScheme.primary, colorScheme.inversePrimary.copy(alpha = 0.5f))
                                            )
                                            )
                                            .padding(24.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column {
                                                Text(
                                                    text = "TAGIHAN AKTIF",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Black,
                                                    letterSpacing = 1.sp,
                                                    color = Color.White.copy(alpha = 0.5f)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = vmState.fees.firstOrNull()?.paymentTitleName ?: "Tagihan Aktif",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(999.dp),
                                                color = colorScheme.error
                                            ) {
                                                Text(
                                                    text = " BELUM DIBAYAR ",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Column {
                                                Text(
                                                    text = formatCurrency(vmState.fees.firstOrNull()?.amount ?: 0.0),
                                                    style = MaterialTheme.typography.displaySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = vmState.fees.firstOrNull()?.dueDate?.let { "Jatuh tempo: $it" } ?: "",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.White.copy(alpha = 0.6f)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Sdm3Button(
                                            text = "Bayar Sekarang",
                                            onClick = { vmState.fees.firstOrNull()?.let { onBayarSekarang(it.id) } },
                                            icon = Icons.Outlined.CreditCard,
                                            containerColor = colorScheme.secondary,
                                            contentColor = colorScheme.primary,
                                            modifier = Modifier.fillMaxWidth().height(54.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Progress Tahunan",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Sdm3Card(padding = 20.dp) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Kelancaran Pembayaran",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Text(
                                            text = "${progressPercent}%",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusSuccess
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                        color = StatusSuccess,
                                        trackColor = StatusSuccess.copy(alpha = 0.1f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "$paidFees dari $totalFees tagihan telah diselesaikan.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Riwayat Transaksi",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        val history = vmState.payments.map { payment ->
                            PaymentHistory(
                                id = payment.id,
                                title = payment.orderId,
                                date = payment.paidAt ?: payment.createdAt ?: "",
                                amount = payment.grossAmount?.toInt() ?: 0,
                                status = payment.status
                            )
                        }

                        items(history) { payment ->
                            Sdm3Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDetailBukti(payment.id) },
                                padding = 16.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (payment.status == "success") Icons.Outlined.Verified else Icons.Outlined.History,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = if (payment.status == "success") StatusSuccess else colorScheme.primary.copy(alpha = 0.4f)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = payment.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Text(
                                            text = payment.date,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Rp${payment.amount / 1000}k",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = StatusSuccess.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = " LUNAS ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = StatusSuccess,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

data class PaymentHistory(
    val id: String = "pay_${kotlin.random.Random.nextInt()}",
    val title: String,
    val date: String,
    val amount: Int,
    val status: String
)

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
private fun PembayaranSppScreenPreview() {
    SDM3Theme {
        PembayaranSppScreen(
            studentId = "",
            onBack = {},
            onBayarSekarang = {},
            onDetailBukti = {}
        )
    }
}
