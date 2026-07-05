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
                    onLanjutkan(paymentId ?: studentFeeId)
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
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(0f, size.height),
                        radius = size.width
                    )
                )
            }

            when (val state = uiState) {
                is PilihMetodeBayarUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        repeat(4) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                is PilihMetodeBayarUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Tidak Ada Metode Pembayaran",
                        message = "Belum tersedia metode pembayaran saat ini.",
                        style = EmptyStateStyle.Neutral,
                        modifier = Modifier.padding(padding)
                    )
                }

                is PilihMetodeBayarUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Metode",
                        message = state.message,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.padding(padding),
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 24.dp) {
                            Column {
                                Text(
                                    text = "RINGKASAN TAGIHAN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp,
                                    color = colorScheme.primary.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = vmState.selectedFee?.paymentTitleName ?: "Tagihan",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f))
                                Spacer(modifier = Modifier.height(12.dp))
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

                        Spacer(modifier = Modifier.height(32.dp))
                        SectionHeader(title = "Metode Pembayaran", modifier = Modifier.padding(bottom = 12.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(vmState.paymentMethods) { method ->
                                    val isSelected = selectedMethod == method.id
                                    Sdm3Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedMethod = method.id },
                                        padding = 16.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(48.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isSelected) colorScheme.primary else colorScheme.primary.copy(alpha = 0.05f)
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
                                            Spacer(modifier = Modifier.width(16.dp))
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

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 24.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = glassSurface,
                                border = BorderStroke(1.dp, glassBorder),
                                shadowElevation = 8.dp
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
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
