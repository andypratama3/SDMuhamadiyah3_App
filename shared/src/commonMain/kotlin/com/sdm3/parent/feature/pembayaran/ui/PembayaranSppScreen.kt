package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranSppScreen(
    studentId: String,
    onBack: () -> Unit,
    onBayarSekarang: (String) -> Unit,
    onDetailBukti: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(11.dp),
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Receipt, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column {
                            Text(
                                "Pembayaran SPP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Ahmad Fathan",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            colorScheme.primary,
                                            Primary.copy(alpha = 0.85f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(Spacing.lg)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "Tagihan Aktif",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xxs))
                                    Text(
                                        text = "SPP Juli 2026",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(100),
                                    color = StatusDanger
                                ) {
                                    Text(
                                        text = "Belum Dibayar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "Rp350.000",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Jatuh tempo: 15 Juli 2026",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "SPP",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))

                            LinearProgressIndicator(
                                progress = { 0.5f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = StatusSuccess,
                                trackColor = StatusSuccess.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(Spacing.xxs))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "6 dari 12 bulan terbayar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "50%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusSuccess
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.lg))

                            Sdm3Button(
                                text = "Bayar Sekarang",
                                onClick = { onBayarSekarang("fee_123") },
                                icon = Icons.Outlined.CreditCard
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Riwayat Pembayaran",
                    modifier = Modifier.padding(top = Spacing.md)
                )
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
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = SDM3Shapes.small,
                            color = if (payment.status == "success") StatusSuccess.copy(alpha = 0.1f)
                                   else StatusDanger.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (payment.status == "success") {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Lunas",
                                        modifier = Modifier.size(24.dp),
                                        tint = StatusSuccess
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.HourglassEmpty,
                                        contentDescription = "Pending",
                                        modifier = Modifier.size(24.dp),
                                        tint = StatusWarning
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = payment.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = payment.date,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rp${payment.amount}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            StatusChip(
                                text = if (payment.status == "success") "Lunas" else "Menunggu",
                                color = statusColorForPayment(payment.status)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
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
