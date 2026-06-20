package com.sdm3.parent.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.TertiaryFixed
import com.sdm3.parent.core.navigation.SDM3Route

@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md)
        ) {
            Text(
                text = "Halo, Orang Tua",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Text(
                text = "Pantau Perkembangan Ananda",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = SchoolGreenDark
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TertiaryFixed)
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(
                        text = "TAGIHAN BELUM DIBAYAR",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Rp350.000",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Jatuh tempo: 15 Juli 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Layanan Sekolah",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                MenuCard(
                    title = "Nilai & Rapor",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                )
                MenuCard(
                    title = "Pembayaran SPP",
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                MenuCard(
                    title = "Kehadiran",
                    color = StatusWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(SDM3Route.KehadiranSiswa(studentId)) }
                )
                MenuCard(
                    title = "Info Anak",
                    color = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Nilai Terbaru",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(listOf(
                    "Matematika" to "92",
                    "B. Indonesia" to "88",
                    "IPA" to "95",
                    "Pend. Agama" to "90"
                )) { (mapel, nilai) ->
                    SubjectScoreCard(mapel = mapel, nilai = nilai)
                }
            }
        }
    }
}

@Composable
private fun MenuCard(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
private fun SubjectScoreCard(
    mapel: String,
    nilai: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mapel,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = nilai,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}
