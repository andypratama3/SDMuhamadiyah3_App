package com.sdm3.parent.feature.pembayaran.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String
)

private val paymentMethods = listOf(
    PaymentMethod("va_bca", "Virtual Account BCA", Icons.Default.AccountBalance, "Transfer ke rekening BCA"),
    PaymentMethod("va_bni", "Virtual Account BNI", Icons.Default.AccountBalance, "Transfer ke rekening BNI"),
    PaymentMethod("va_bsi", "Virtual Account BSI", Icons.Default.AccountBalance, "Transfer ke rekening BSI"),
    PaymentMethod("qris", "QRIS", Icons.Default.QrCodeScanner, "Scan QR dengan e-wallet"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihMetodeBayarScreen(
    studentFeeId: String,
    onBack: () -> Unit,
    onLanjutkan: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedMethod by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pilih Metode Pembayaran",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
        ) {
            // Summary card
            Sdm3ElevatedCard(padding = Spacing.xl) {
                Text("SPP Juli 2026", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("SPP", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                        Text("Rp300.000", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Iuran Gedung", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                        Text("Rp40.000", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Admin", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                        Text("Rp10.000", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Rp350.000",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))
            Text("Pilih Metode Pembayaran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Spacing.sm))

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(paymentMethods) { method ->
                        val isSelected = selectedMethod == method.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method.id },
                            shape = CardShape,
                            color = if (isSelected) colorScheme.primaryContainer.copy(alpha = 0.5f) else colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.lg),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = SDM3Shapes.small,
                                    color = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(method.icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(Spacing.md))
                                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                    Text(method.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = colorScheme.onSurface)
                                    Text(method.description, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                                }
                                RadioButton(selected = isSelected, onClick = { selectedMethod = method.id })
                            }
                        }
                    }
                }

                Sdm3Button(
                    text = "Lanjutkan Pembayaran",
                    onClick = { selectedMethod?.let { onLanjutkan("pay_$it") } },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(vertical = Spacing.md),
                    enabled = selectedMethod != null,
                    containerColor = colorScheme.primary
                )
            }
        }
    }
}
