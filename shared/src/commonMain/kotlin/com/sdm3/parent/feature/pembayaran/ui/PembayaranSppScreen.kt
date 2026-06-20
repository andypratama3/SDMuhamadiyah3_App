package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.component.statusColorForPayment
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite
import com.sdm3.parent.core.designsystem.theme.TertiaryFixed

private val filterOptions = listOf("Semua", "Lunas", "Menunggu")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranSppScreen(
    studentId: String,
    onBayarSekarang: (String) -> Unit,
    onDetailBukti: (String) -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = TertiaryFixed)
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SPP JULI 2026",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Rp350.000",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            StatusChip(text = "Belum Dibayar", color = StatusDanger)
                        }
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = "Jatuh tempo: 15 Juli 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        androidx.compose.material3.Button(
                            onClick = { onBayarSekarang("fee_123") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Secondary)
                        ) {
                            Text("Bayar Sekarang", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Riwayat Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(filterOptions.size) { index ->
                        FilterChip(
                            selected = selectedFilter == index,
                            onClick = { selectedFilter = index },
                            label = { Text(filterOptions[index]) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Secondary,
                                selectedLabelColor = SurfaceWhite
                            )
                        )
                    }
                }
            }

            val history = listOf(
                PaymentHistory(title = "SPP Juni 2026", date = "12 Jun 2026", amount = 350000, status = "success"),
                PaymentHistory(title = "SPP Mei 2026", date = "10 Mei 2026", amount = 350000, status = "success"),
                PaymentHistory(title = "SPP April 2026", date = "5 Apr 2026", amount = 350000, status = "success"),
                PaymentHistory(title = "SPP Maret 2026", date = "8 Mar 2026", amount = 350000, status = "success"),
                PaymentHistory(title = "SPP Februari 2026", date = "3 Feb 2026", amount = 350000, status = "success")
            )

            items(history) { payment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetailBukti(payment.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (payment.status == "success") StatusSuccess.copy(alpha = 0.1f)
                                    else StatusDanger.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (payment.status == "success") {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Lunas",
                                    modifier = Modifier.size(16.dp),
                                    tint = StatusSuccess
                                )
                            } else {
                                Icon(
                                    Icons.Default.HourglassEmpty,
                                    contentDescription = "Pending",
                                    modifier = Modifier.size(16.dp),
                                    tint = StatusWarning
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = payment.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = payment.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rp${payment.amount}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            StatusChip(
                                text = if (payment.status == "success") "Lunas" else "Menunggu",
                                color = statusColorForPayment(payment.status)
                            )
                        }
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
