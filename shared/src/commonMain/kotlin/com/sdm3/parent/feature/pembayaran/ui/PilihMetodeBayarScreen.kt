package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

data class PaymentMethod(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String
)

private val paymentMethods = listOf(
    PaymentMethod("va_bca", "Virtual Account BCA", "🏦", "Transfer ke rekening BCA"),
    PaymentMethod("va_bni", "Virtual Account BNI", "🏦", "Transfer ke rekening BNI"),
    PaymentMethod("va_bsi", "Virtual Account BSI", "🏦", "Transfer ke rekening BSI"),
    PaymentMethod("qris", "QRIS", "📱", "Scan QR dengan e-wallet"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihMetodeBayarScreen(
    studentFeeId: String,
    onLanjutkan: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tagihan & Pilih Metode") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(
                        text = "SPP Juli 2026",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SPP", color = OnSurfaceVariant)
                        Text("Rp300.000")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Iuran Gedung", color = OnSurfaceVariant)
                        Text("Rp40.000")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Biaya Admin", color = OnSurfaceVariant)
                        Text("Rp10.000")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text("Rp350.000", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Pilih Metode Pembayaran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(paymentMethods) { method ->
                    val isSelected = selectedMethod == method.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Secondary.copy(alpha = 0.08f) else SurfaceWhite
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
                        onClick = { selectedMethod = method.id }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                method.emoji,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = method.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                            Text(
                                if (isSelected) "⭕" else "○",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { selectedMethod?.let { onLanjutkan("pay_${it}") } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.md),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedMethod != null,
                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
            ) {
                Text("Lanjutkan Pembayaran", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
