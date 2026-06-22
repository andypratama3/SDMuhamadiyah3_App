package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.component.statusColorForPayment
import com.sdm3.parent.core.designsystem.theme.*

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
                    Text(
                        "Pembayaran SPP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
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
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF0E7A39),
                                            Color(0xFF138848)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(Spacing.xl)
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
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(100),
                                    color = StatusDanger.copy(alpha = 0.85f)
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

                        Column(modifier = Modifier.padding(Spacing.xl)) {
                            Text(
                                text = "Rp350.000",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            Text(
                                text = "Jatuh tempo: 15 Juli 2026",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            LinearProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                color = StatusSuccess,
                                trackColor = StatusSuccess.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(Spacing.xxs))

                            Text(
                                text = "Terbayar 6 dari 12 bulan",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )

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
                Text(
                    text = "Riwayat Pembayaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetailBukti(payment.id) },
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(16.dp),
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
                                style = MaterialTheme.typography.titleMedium,
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
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
