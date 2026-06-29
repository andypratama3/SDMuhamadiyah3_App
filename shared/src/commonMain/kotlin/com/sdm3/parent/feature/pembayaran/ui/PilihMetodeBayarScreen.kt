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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String
)

private val paymentMethods = listOf(
    PaymentMethod("va_bca", "Virtual Account BCA", Icons.Default.AccountBalance, "Konfirmasi instan & otomatis"),
    PaymentMethod("va_bni", "Virtual Account BNI", Icons.Default.AccountBalance, "Tersedia 24/7 untuk transfer"),
    PaymentMethod("va_bsi", "Virtual Account BSI", Icons.Default.AccountBalance, "Bank Syariah Indonesia resmi"),
    PaymentMethod("qris", "QRIS Universal", Icons.Default.QrCodeScanner, "Scan via GoPay, OVO, Dana, LinkAja"),
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
            // Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(0f, size.height),
                        radius = size.width
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Summary Glass Card
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
                            text = "SPP Juli 2026",
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
                                text = "Rp350.000",
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
                        items(paymentMethods) { method ->
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
                                                method.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = if (isSelected) Color.White else colorScheme.primary
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
                                        Text(
                                            method.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
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

                    // Island Button Architecture
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shadowElevation = 8.dp
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Sdm3Button(
                                text = "Lanjutkan Pembayaran",
                                onClick = { selectedMethod?.let { onLanjutkan("pay_$it") } },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                enabled = selectedMethod != null
                            )
                        }
                    }
                }
            }
        }
    }
}

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
