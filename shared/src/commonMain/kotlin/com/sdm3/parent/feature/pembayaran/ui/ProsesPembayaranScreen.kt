package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProsesPembayaranScreen(
    paymentId: String,
    onBack: () -> Unit,
    onPembayaranBerhasil: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Instruksi Pembayaran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(36.dp), tint = colorScheme.onSecondaryContainer)
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text(text = "Nomor Virtual Account", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "9887 1234 5678 9012",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Sdm3Button(
                        text = "Salin Nomor VA",
                        onClick = { },
                        icon = Icons.Default.ContentCopy,
                        containerColor = colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "Rp350.000", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)

            Text(text = "Cara Pembayaran", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            val steps = listOf(
                "1. Buka aplikasi mobile banking BCA",
                "2. Pilih menu Transfer",
                "3. Pilih Transfer ke Virtual Account",
                "4. Masukkan nomor VA: 9887 1234 5678 9012",
                "5. Konfirmasi dan masukkan PIN",
                "6. Simpan bukti pembayaran"
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(text = "Scan QRIS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurfaceVariant)

            Surface(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = CardShape,
                color = colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(64.dp), tint = colorScheme.primary)
                }
            }

            Text(
                text = "Scan dengan aplikasi e-wallet atau mobile banking",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Sdm3Button(
                text = "Saya Sudah Bayar",
                onClick = onPembayaranBerhasil,
                containerColor = colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

private val CardShape = com.sdm3.parent.core.designsystem.theme.CardShape
