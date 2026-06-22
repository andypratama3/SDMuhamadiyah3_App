package com.sdm3.parent.feature.nilai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNilaiMapelScreen(
    studentId: String,
    subjectId: String,
    semester: String,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val subjectName = "Matematika"
    val finalScore = 92
    val predicate = "A"
    val predicateDesc = "Sangat Baik"

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = subjectName,
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(vertical = Spacing.md)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.primary,
                    tonalElevation = 0.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$finalScore",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        StatusChip(text = "$predicate — $predicateDesc", color = colorScheme.onPrimary)
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Semester $semester",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text("Komponen Nilai", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            item { KomponenBar("Sumatif", 90f, 100f, "40%", colorScheme.primary) }
            item { KomponenBar("Formatif", 88f, 100f, "40%", Secondary) }
            item { KomponenBar("Projek", 95f, 100f, "20%", StatusSuccess) }

            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text("Tujuan Pembelajaran (TP)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            val tpList = listOf(
                TPDetail("3.1", "Menjelaskan operasi hitung bilangan cacah", 95),
                TPDetail("3.2", "Menyelesaikan soal cerita penjumlahan", 90),
                TPDetail("3.3", "Mengidentifikasi sifat-sifat bangun datar", 78),
                TPDetail("3.4", "Menghitung keliling bangun datar", 85),
                TPDetail("3.5", "Menyelesaikan masalah kontekstual", 90),
                TPDetail("3.6", "Menganalisis data sederhana", 82)
            )

            items(tpList) { tp ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "TP ${tp.code}", style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                            Text(text = tp.description, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        val tpColor = when {
                            tp.score >= 90 -> StatusSuccess
                            tp.score >= 75 -> StatusWarning
                            else -> StatusDanger
                        }
                        StatusChip(text = "${tp.score}", color = tpColor)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text("Catatan Guru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(Spacing.sm))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "Ananda menunjukkan pemahaman yang baik pada materi bilangan cacah. Perlu ditingkatkan pada soal cerita kontekstual.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "- Ibu Siti Rahmawati, S.Pd.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Composable
private fun KomponenBar(komponen: String, nilai: Float, max: Float, bobot: String, warna: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(komponen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("${nilai.toInt()}/${max.toInt()} (Bobot $bobot)", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            LinearProgressIndicator(
                progress = { (nilai / max).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = warna,
                trackColor = warna.copy(alpha = 0.15f)
            )
        }
    }
}

data class TPDetail(val code: String, val description: String, val score: Int)
