package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.compose_multiplatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPengumumanScreen(
    announcementId: String,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Detail Informasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "PUBLIKASI RESMI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Bagikan", tint = colorScheme.primary)
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
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Surface(
                        modifier = Modifier
                            .padding(24.dp)
                            .align(Alignment.BottomStart),
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.secondary
                    ) {
                        Text(
                            " INFORMASI PENTING ",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(0.6f)
                    ) {
                        Icon(Icons.Outlined.Event, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("12 Des 2026", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)

                        Spacer(Modifier.width(24.dp))

                        Icon(Icons.Outlined.Person, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Admin Akademik", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Kalender Penilaian Sumatif Akhir Semester Ganjil 2026",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Assalamualaikum Warahmatullahi Wabarakatuh.\n\n" +
                            "Diberitahukan kepada seluruh orang tua siswa SD Muhammadiyah 3 Samarinda, " +
                            "bahwa pelaksanaan Sumatif Akhir Semester (SAS) Ganjil akan dilaksanakan secara serentak.\n\n" +
                            "Periode: 18 - 22 Desember 2026\n" +
                            "Waktu: 07:30 - 11:30 WITA\n\n" +
                            "Kami menghimbau agar bapak/ibu dapat memantau kesiapan belajar ananda di rumah " +
                            "dan memastikan kesehatan tetap terjaga. Kartu ujian digital dapat diakses melalui menu Rapor.\n\n" +
                            "Demikian informasi ini kami sampaikan. Terima kasih.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.primary,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    SectionHeader(title = "Lampiran Digital", modifier = Modifier.padding(bottom = 12.dp))

                    Sdm3Card(padding = 16.dp) {
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
                                    Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Panduan_SAS_2026.pdf",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Text(
                                    "Dokumen PDF \u2022 1.2 MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailPengumumanScreenPreview() {
    SDM3Theme {
        DetailPengumumanScreen(announcementId = "", onBack = {})
    }
}
