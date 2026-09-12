package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.component.ScreenUiState
import com.sdm3.parent.core.designsystem.component.resolveScreenState
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.feature.pembayaran.PembayaranSppViewModel
import com.sdm3.parent.feature.pembayaran.currentYear
import com.sdm3.parent.feature.pembayaran.isPaid
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PembayaranSppScreen(
    studentId: String,
    onBack: (() -> Unit)? = null,
    onBayarSekarang: (String) -> Unit,
    onDetailBukti: (String) -> Unit,
) {
    val isPreview = LocalInspectionMode.current
    val viewModel: PembayaranSppViewModel = koinViewModel()
    val vmState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.pembayaran.PembayaranSppUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val uiState: ScreenUiState = remember(vmState.isLoading, vmState.errorMessage, vmState.isEmpty) {
        resolveScreenState(vmState.isLoading, vmState.isEmpty, vmState.errorMessage)
    }

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadData(studentId)
        }
    }

    var selectedTab by remember { mutableStateOf(PaymentTab.Tagihan) }
    val currentYear = remember { currentYear() }
    val yearExpansion = remember { mutableStateMapOf<Int, Boolean>() }

    ScreenScaffold(
        title = "Administrasi Keuangan",
        subtitle = "STATUS PEMBAYARAN SPP",
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            when (val state = uiState) {
                is ScreenUiState.Loading -> PaymentLoadingContent(
                    modifier = Modifier.padding(padding),
                )

                is ScreenUiState.Empty -> Sdm3EmptyState(
                    title = "Belum Ada Data",
                    message = "Belum ada tagihan atau riwayat pembayaran SPP yang tercatat.",
                    style = EmptyStateStyle.Neutral,
                    modifier = Modifier.padding(padding),
                    action = {
                        Sdm3Button(
                            text = "Muat Ulang",
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )

                is ScreenUiState.Error -> Sdm3ErrorState(
                    title = "Gagal Memuat Data",
                    message = state.message,
                    style = ErrorStateStyle.Generic,
                    modifier = Modifier.padding(padding),
                    primaryAction = {
                        Sdm3Button(
                            text = "Coba Lagi",
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )

                is ScreenUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        PaymentTabSelector(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            modifier = Modifier.padding(top = Spacing.xs),
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = Spacing.xl,
                                vertical = Spacing.sm,
                            ),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        ) {
                            when (selectedTab) {
                                PaymentTab.Tagihan -> {
                                    item(key = "hero") {
                                        PaymentHeroCard(
                                            activeFee = vmState.activeFee,
                                            studentName = vmState.studentName,
                                            onBayarSekarang = {
                                                vmState.activeFee?.let { onBayarSekarang(it.id) }
                                            },
                                        )
                                    }

                                    if (vmState.progress.total > 0) {
                                        item(key = "progress-header") {
                                            SectionHeader(
                                                title = "Progress Tahunan",
                                                modifier = Modifier.padding(top = Spacing.xs),
                                            )
                                        }
                                        item(key = "progress") {
                                            PaymentProgressCard(progress = vmState.progress)
                                        }
                                    }

                                    if (vmState.feeYearGroups.isEmpty()) {
                                        item(key = "empty-tagihan") {
                                            Sdm3EmptyState(
                                                title = "Belum Ada Tagihan",
                                                message = "Tidak ada tagihan SPP yang perlu ditampilkan.",
                                                style = EmptyStateStyle.Neutral,
                                            )
                                        }
                                    } else {
                                        item(key = "tagihan-header") {
                                            SectionHeader(
                                                title = "Daftar Tagihan",
                                                modifier = Modifier.padding(top = Spacing.xs),
                                            )
                                        }

                                        vmState.feeYearGroups.forEach { yearGroup ->
                                            val yearKey = yearGroup.year
                                            val isExpanded = yearExpansion.getOrPut(yearKey) {
                                                yearKey == currentYear || yearKey == 0
                                            }

                                            item(key = "fee-year-$yearKey") {
                                                PaymentGroupedYearSection(
                                                    year = yearKey,
                                                    itemCount = yearGroup.itemCount,
                                                    expanded = isExpanded,
                                                    onToggle = {
                                                        yearExpansion[yearKey] = !isExpanded
                                                    },
                                                ) {
                                                    yearGroup.months.forEach { monthGroup ->
                                                        PaymentMonthHeader(
                                                            monthName = monthGroup.monthName,
                                                            itemCount = monthGroup.items.size,
                                                        )
                                                        monthGroup.items.forEach { fee ->
                                                            val canPay = !fee.isPaid()
                                                            FeeItemCard(
                                                                fee = fee,
                                                                onClick = if (canPay) {
                                                                    { onBayarSekarang(fee.id) }
                                                                } else {
                                                                    null
                                                                },
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                PaymentTab.Riwayat -> {
                                    if (vmState.paymentYearGroups.isEmpty()) {
                                        item(key = "empty-riwayat") {
                                            Sdm3EmptyState(
                                                title = "Belum Ada Riwayat",
                                                message = "Belum ada transaksi pembayaran SPP yang tercatat.",
                                                style = EmptyStateStyle.Neutral,
                                            )
                                        }
                                    } else {
                                        item(key = "riwayat-header") {
                                            SectionHeader(
                                                title = "Riwayat Transaksi",
                                                modifier = Modifier.padding(top = Spacing.xs),
                                            )
                                        }

                                        vmState.paymentYearGroups.forEach { yearGroup ->
                                            val yearKey = yearGroup.year
                                            val isExpanded = yearExpansion.getOrPut(-yearKey) {
                                                yearKey == currentYear || yearKey == 0
                                            }

                                            item(key = "payment-year-$yearKey") {
                                                PaymentGroupedYearSection(
                                                    year = yearKey,
                                                    itemCount = yearGroup.itemCount,
                                                    expanded = isExpanded,
                                                    onToggle = {
                                                        yearExpansion[-yearKey] = !isExpanded
                                                    },
                                                ) {
                                                    yearGroup.months.forEach { monthGroup ->
                                                        PaymentMonthHeader(
                                                            monthName = monthGroup.monthName,
                                                            itemCount = monthGroup.items.size,
                                                        )
                                                        monthGroup.items.forEach { payment ->
                                                            PaymentHistoryCard(
                                                                payment = payment,
                                                                onClick = { onDetailBukti(payment.id) },
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item(key = "bottom-safe") {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .paymentBottomSafePadding(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentLoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .shimmerEffect(),
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .shimmerEffect(),
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shimmerEffect(),
            )
        }
        items(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shimmerEffect(),
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .paymentBottomSafePadding(),
            )
        }
    }
}

@Preview
@Composable
private fun PembayaranSppScreenPreview() {
    SDM3Theme {
        PembayaranSppScreen(
            studentId = "",
            onBack = {},
            onBayarSekarang = {},
            onDetailBukti = {},
        )
    }
}
