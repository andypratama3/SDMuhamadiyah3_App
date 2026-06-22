package com.sdm3.parent.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.SectionHeader
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.CardShape
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.navigation.SDM3Route

private data class QuickNavData(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: (String) -> SDM3Route
)

private data class SubjectScore(
    val mapel: String,
    val score: Int,
    val predicate: String
)

@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme

    val quickNavItems = listOf(
        QuickNavData("Nilai", Icons.Outlined.School, colorScheme.primary) {
            SDM3Route.NilaiRapor(it, "ganjil")
        },
        QuickNavData("Kehadiran", Icons.Outlined.EventAvailable, StatusWarning) {
            SDM3Route.KehadiranSiswa(it)
        },
        QuickNavData("Rapor", Icons.Outlined.Description, colorScheme.primary) {
            SDM3Route.HalamanRapor(it)
        },
        QuickNavData("Info Anak", Icons.Outlined.ChildCare, StatusSuccess) {
            SDM3Route.DetailInfoAnak(it)
        }
    )

    val recentScores = listOf(
        SubjectScore("Matematika", 92, "A"),
        SubjectScore("B. Indonesia", 88, "B+"),
        SubjectScore("IPA", 95, "A"),
        SubjectScore("Pend. Agama", 90, "A")
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            HomeHeader(
                studentId = studentId,
                navController = navController
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item { Spacer(modifier = Modifier.height(Spacing.xs)) }

            item { StudentCard(studentId = studentId) }

            item { ActivePaymentCard(studentId = studentId, navController = navController) }

            item {
                SectionHeader(
                    title = "Layanan Akademik",
                    modifier = Modifier.padding(top = Spacing.sm)
                )
            }

            item {
                val rows = quickNavItems.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            rowItems.forEach { nav ->
                                AcademicServiceCard(
                                    modifier = Modifier.weight(1f),
                                    nav = nav,
                                    studentId = studentId,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Prestasi Terkini",
                    actionLabel = "Lihat Semua",
                    onActionClick = { navController.navigate(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    items(recentScores) { subject ->
                        GradeScoreCard(subject = subject)
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Pengumuman Sekolah",
                    actionLabel = "Semua",
                    onActionClick = { navController.navigate(SDM3Route.PengumumanSekolah) }
                )
            }

            item { AnnouncementCard(navController = navController) }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Composable
private fun HomeHeader(
    studentId: String,
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(18.dp),
            color = colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "AF",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selamat Pagi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = "Orang Tua Wali",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                text = "Ahmad Fathan · 4-A",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { navController.navigate(SDM3Route.Notifikasi) }) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentCard(studentId: String) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AF",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ahmad Fathan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Kelas 4-A (Ibnu Sina)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = "TA 2025/2026 · NIS: 0012345678",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.SwapHoriz,
                        contentDescription = "Ganti Siswa",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivePaymentCard(
    studentId: String,
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0E7A39),
                                Color(0xFF138848)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(Spacing.xl)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Belum Dibayar",
                            style = MaterialTheme.typography.labelMedium,
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
                Text(
                    text = "Jatuh tempo: 15 Juli 2026",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
                Sdm3Button(
                    text = "Bayar Sekarang",
                    onClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) },
                    icon = Icons.Outlined.CreditCard
                )
            }
        }
    }
}

@Composable
private fun AcademicServiceCard(
    modifier: Modifier = Modifier,
    nav: QuickNavData,
    studentId: String,
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.clickable { navController.navigate(nav.route(studentId)) },
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = nav.color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        nav.icon,
                        contentDescription = null,
                        tint = nav.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = nav.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GradeScoreCard(subject: SubjectScore) {
    val colorScheme = MaterialTheme.colorScheme
    val scoreColor = when {
        subject.score >= 90 -> StatusSuccess
        subject.score >= 75 -> StatusWarning
        else -> StatusDanger
    }

    Surface(
        modifier = Modifier.width(140.dp),
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.lg)
        ) {
            Text(
                text = "${subject.score}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = subject.mapel,
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            StatusChip(text = subject.predicate, color = scoreColor)
        }
    }
}

@Composable
private fun AnnouncementCard(navController: NavHostController) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(SDM3Route.PengumumanSekolah) },
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Campaign,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Libur Akhir Semester",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Libur semester ganjil dimulai 20 Des 2025 – 3 Jan 2026.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
