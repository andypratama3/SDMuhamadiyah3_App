package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.CardShape
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusDanger
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
            TopAppBar(
                title = {
                    Text(
                        "Detail Info",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Bagikan", tint = colorScheme.onSurface)
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .padding(Spacing.lg)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(6.dp),
                    color = StatusDanger
                ) {
                    Text(
                        "Penting",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(Spacing.xl)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.width(Spacing.sm))
                    Text("12 Desember 2023", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)

                    Spacer(Modifier.width(Spacing.lg))

                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = colorScheme.secondary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.width(Spacing.sm))
                    Text("Admin Sekolah", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(
                    text = "Jadwal Ujian Akhir Semester Ganjil 2023/2024",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "Assalamualaikum Warahmatullahi Wabarakatuh.\n\n" +
                        "Diberitahukan kepada seluruh wali murid SD Muhammadiyah 3 Samarinda, " +
                        "bahwa pelaksanaan Ujian Akhir Semester (UAS) Ganjil akan dilaksanakan pada:\n\n" +
                        "Tanggal: 18 - 22 Desember 2023\n" +
                        "Waktu: 07.30 - 11.00 WITA\n\n" +
                        "Mohon agar bapak/ibu dapat mendampingi putra-putrinya belajar di rumah " +
                        "serta memastikan kesehatan ananda tetap terjaga selama masa ujian.\n\n" +
                        "Kartu ujian dapat diambil di tata usaha mulai besok pagi. Demikian informasi ini kami sampaikan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                Text("Lampiran Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                Spacer(Modifier.height(Spacing.md))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Description, contentDescription = null, tint = colorScheme.secondary)
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            "Jadwal_Lengkap_UAS.pdf",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxxl))
            }
        }
    }
}
