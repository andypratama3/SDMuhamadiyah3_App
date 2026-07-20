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
import androidx.compose.material.icons.outlined.Payments
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
import com.sdm3.parent.core.util.formatTanggal
import com.sdm3.parent.platform.PlatformActions
import com.sdm3.parent.feature.pembayaran.PilihMetodeBayarViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalInspectionMode

sealed class PilihMetodeBayarUiState {
    data object Loading : PilihMetodeBayarUiState()
    data object Empty : PilihMetodeBayarUiState()
    data class Error(val message: String = "Silakan coba kembali.") : PilihMetodeBayarUiState()
    data object Success : PilihMetodeBayarUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihMetodeBayarScreen(
    studentFeeId: String,
    onBack: () -> Unit,
    onLanjutkan: (String) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val glassSurface = glassSurfaceColor()
    val glassBorder = glassBorderColor()
    var selectedMethod by remember { mutableStateOf<String?>(null) }
    val viewModel: PilihMetodeBayarViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.PilihMetodeBayarUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: PilihMetodeBayarUiState = remember(vmState) {
        val s = vmState
        when {
            s.isLoading -> PilihMetodeBayarUiState.Loading
            s.errorMessage != null -> PilihMetodeBayarUiState.Error(s.errorMessage)
            s.isEmpty -> PilihMetodeBayarUiState.Empty
            else -> PilihMetodeBayarUiState.Success
        }
    }

    if (!isPreview) {
        LaunchedEffect(studentFeeId) {
            viewModel.init(studentFeeId)
            viewModel.loadFeeAndMethods()
        }
    }

    LaunchedEffect(vmState.snapTokenRequested) {
        if (!isPreview && vmState.snapTokenRequested) {
            val resp = vmState.snapTokenResponse
            val redirect = resp?.redirectUrl?.takeIf { it.isNotBlank() }
            val paymentId = resp?.paymentId?.takeIf { it.isNotBlank() }
            // Tandai sudah dikonsumsi agar tidak memicu navigasi ulang saat back.
            viewModel.consumeSnapToken()
            when {
                redirect != null -> {
                    PlatformActions.openUrl(redirect)
                    if (paymentId != null) {
                        onLanjutkan(paymentId)
                    } else {
                        viewModel.showError("Data pembayaran tidak lengkap dari server. Silakan coba lagi.")
                    }
                }
                paymentId != null -> onLanjutkan(paymentId)
                else -> viewModel.showError("Gagal memulai pembayaran. Silakan coba lagi.")
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
                            text = "Pilih Cara Bayar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "TRANSAKSI AMAN & TERVERIFIKASI",
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
        },
        bottomBar = {
            if (uiState is PilihMetodeBayarUiState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.md)
                        .navigationBarsPadding()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = glassSurface,
                        border = BorderStroke(1.dp, glassBorder),
                        shadowElevation = 8.dp
                    ) {
                        Box(modifier = Modifier.padding(Spacing.xs)) {
                            Sdm3Button(
                                text = "Lanjutkan Pembayaran",
                                onClick = {
                                    selectedMethod?.let { method ->
                                        viewModel.requestSnapToken(studentFeeId, method)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                enabled = selectedMethod != null && !vmState.isLoading,
                                isLoading = vmState.isLoading
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.4f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primary.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.1f),
                        radius = size.width * 1.5f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.9f),
                        radius = size.width * 1.0f
                    )
                )
            }

            when (val state = uiState) {
                is PilihMetodeBayarUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .safeDrawingPadding()
                            .paymentHorizontalPadding()
                    ) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxl))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .clip(RoundedCornerShape(Spacing.xs))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        repeat(4) {
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(Spacing.md))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    }
                }

                is PilihMetodeBayarUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Metode Pembayaran",
                        message = "Belum tersedia metode pembayaran saat ini.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding()
                    )
                }

                is PilihMetodeBayarUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Metode",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier
                            .padding(padding)
                            .safeDrawingPadding(),
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadFeeAndMethods() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                is PilihMetodeBayarUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .safeDrawingPadding()
                            .paymentHorizontalPadding(),
                        contentPadding = PaddingValues(
                            vertical = Spacing.sm
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        item {
                            Sdm3Card(padding = Spacing.xl) {
                                Column {
                                    Text(
                                        text = "RINGKASAN TAGIHAN",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                        color = colorScheme.primary.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.md))
                                    Text(
                                        text = vmState.selectedFee?.paymentTitleName ?: "Tagihan",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                    vmState.selectedFee?.dueDate?.takeIf { it.isNotBlank() }?.let { dueDate ->
                                        Spacer(modifier = Modifier.height(Spacing.xs))
                                        Text(
                                            text = "Jatuh tempo: ${formatTanggal(dueDate)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f))
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "TOTAL BAYAR",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = vmState.selectedFee?.let { formatCurrency(it.amount) } ?: "Rp0",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(Spacing.md))
                            SectionHeader(title = "Metode Pembayaran", modifier = Modifier.padding(bottom = Spacing.xs))
                        }

                        items(vmState.paymentMethods) { method ->
                            val isSelected = selectedMethod == method.id
                            Sdm3Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedMethod = method.id },
                                padding = Spacing.md
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) colorScheme.primary else colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        border = BorderStroke(1.5.dp, if (isSelected) colorScheme.primary else colorScheme.primary.copy(alpha = 0.2f)),
                                        shadowElevation = if (isSelected) 8.dp else 4.dp
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Outlined.Payments,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = if (isSelected) colorScheme.onPrimary else colorScheme.primary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(Spacing.md))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            method.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        if (method.description != null) {
                                            Text(
                                                method.description,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedMethod = method.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = colorScheme.secondary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Number): String = com.sdm3.parent.core.util.formatRupiah(amount)

@Preview
@Composable
private fun PilihMetodeBayarScreenPreview() {
    SDM3Theme {
        PilihMetodeBayarScreen(
            studentFeeId = "",
            onBack = {},
            onLanjutkan = {}
        )
    }
}
