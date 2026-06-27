package com.sdm3.parent.feature.nilai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = subjectName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "ANALISIS KOMPETENSI",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.1f),
                        radius = size.width
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item {
                    // Score Hero Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            val glowColor = colorScheme.surfaceTint.copy(alpha = 0.4f)
                            Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).alpha(0.15f)) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(glowColor, Color.Transparent),
                                        center = Offset(size.width * 0.9f, 0f),
                                        radius = size.width
                                    )
                                )
                            }
                            
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SKOR AKHIR",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$finalScore",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = " PREDIKAT $predicate ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Parameter Penilaian",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item { KomponenBar("Sumatif (40%)", 90f, 100f, colorScheme.primary) }
                item { KomponenBar("Formatif (40%)", 88f, 100f, colorScheme.secondary) }
                item { KomponenBar("Projek (20%)", 95f, 100f, StatusSuccess) }

                item {
                    SectionHeader(
                        title = "Ketercapaian TP",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val tpList = listOf(
                    TPDetail("3.1", "Operasi hitung bilangan cacah", 95),
                    TPDetail("3.2", "Soal cerita penjumlahan", 90),
                    TPDetail("3.3", "Sifat-sifat bangun datar", 78),
                    TPDetail("3.4", "Keliling bangun datar", 85)
                )

                items(tpList) { tp ->
                    var expanded by remember { mutableStateOf(false) }
                    val tpColor = when {
                        tp.score >= 90 -> StatusSuccess
                        tp.score >= 75 -> StatusWarning
                        else -> colorScheme.error
                    }

                    Sdm3Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        padding = 16.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = colorScheme.primary.copy(alpha = 0.05f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = tp.code,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tp.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "${tp.score}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = tpColor
                                )
                            }
                            if (expanded) {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Ananda telah menguasai kompetensi dasar pada TP ini dengan hasil yang melampaui standar minimal.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Anotasi Akademik",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Sdm3Card(padding = 20.dp) {
                        Column {
                            Icon(
                                Icons.Outlined.FormatQuote,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Ananda menunjukkan ketekunan yang luar biasa. Fokus utama selanjutnya adalah penerapan konsep matematika dalam skenario dunia nyata.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 26.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "\u2014 Ibu Siti Rahmawati, S.Pd.",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun KomponenBar(komponen: String, nilai: Float, max: Float, warna: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(padding = 16.dp) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(komponen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Text("${nilai.toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = warna)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (nilai / max).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = warna,
                trackColor = warna.copy(alpha = 0.1f)
            )
        }
    }
}

data class TPDetail(val code: String, val description: String, val score: Int)

@Preview
@Composable
private fun DetailNilaiMapelScreenPreview() {
    SDM3Theme {
        DetailNilaiMapelScreen(
            studentId = "",
            subjectId = "",
            semester = "ganjil",
            onBack = {}
        )
    }
}
