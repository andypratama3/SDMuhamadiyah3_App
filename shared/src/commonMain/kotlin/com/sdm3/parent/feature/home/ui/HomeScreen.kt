package com.sdm3.parent.feature.home.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.navigation.SDM3BottomTab

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
    val isPreview = LocalInspectionMode.current
    var visible by remember { mutableStateOf(isPreview) }
    if (!isPreview) {
        LaunchedEffect(Unit) { visible = true }
    }

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
            HomeHeader(navController = navController)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                MotionAnim(visible = visible) {
                    StudentCard(
                        studentId = studentId,
                        onSwitchChild = { navController.navigate(SDM3Route.PilihAnak) }
                    )
                }
            }

            item {
                MotionAnim(visible = visible) {
                    ActivePaymentCard(
                        studentId = studentId,
                        navController = navController
                    )
                }
            }

            item {
                MotionAnim(visible = visible) {
                    SectionHeader(
                        title = "Layanan Akademik",
                        modifier = Modifier.padding(top = Spacing.sm)
                    )
                }
            }

            item {
                MotionAnim(visible = visible) {
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
            }

            item {
                MotionAnim(visible = visible) {
                    SectionHeader(
                        title = "Prestasi Terkini",
                        actionLabel = "Lihat Semua",
                        onActionClick = { navController.navigate(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                    )
                }
            }

            item {
                MotionAnim(visible = visible) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(end = Spacing.md)
                    ) {
                        items(recentScores) { subject ->
                            GradeScoreCard(subject = subject)
                        }
                    }
                }
            }

            item {
                MotionAnim(visible = visible) {
                    SectionHeader(
                        title = "Pengumuman Sekolah",
                        actionLabel = "Semua",
                        onActionClick = { navController.navigate(SDM3Route.PengumumanSekolah) }
                    )
                }
            }

            item {
                MotionAnim(visible = visible) {
                    AnnouncementCard(navController = navController)
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Composable
private fun HomeHeader(
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Institutional Identity Chip
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.primary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "OT",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    color = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selamat Pagi,",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "Bapak/Ibu Wali",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = colorScheme.onSurface,
                maxLines = 1
            )
        }

        // Precision Notification Island
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { /* Navigate to Notifications */ },
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            modifier = Modifier.size(8.dp),
                            containerColor = StatusDanger
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentCard(
    studentId: String,
    onSwitchChild: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSwitchChild() },
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(62.dp),
                shape = SDM3Shapes.medium,
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Kelas 4-A (Ibnu Sina)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = "TA 2025/2026 \u00B7 NIS: 0012345678",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(11.dp),
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                colorScheme.primary,
                                Primary.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(Spacing.lg)
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
                            color = colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Text(
                            text = "SPP Juli 2026",
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(100),
                        color = colorScheme.onPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Belum Dibayar",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(Spacing.lg)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Rp350.000",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Jatuh tempo: 15 Juli 2026",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    LinearProgressIndicator(
                        progress = { 0.5f },
                        modifier = Modifier.width(60.dp).height(6.dp),
                        color = StatusWarning,
                        trackColor = StatusWarning.copy(alpha = 0.15f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Sdm3Button(
                    text = "Bayar Sekarang",
                    onClick = {
                        navController.navigate(SDM3Route.PilihMetodeBayar("FEE-001"))
                    },
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

    Card(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { navController.navigate(nav.route(studentId)) },
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = SDM3Shapes.small,
                color = nav.color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = nav.icon,
                        contentDescription = null,
                        tint = nav.color,
                        modifier = Modifier.size(26.dp)
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

    Card(
        modifier = Modifier.width(130.dp),
        shape = SDM3Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacing.md)
        ) {
            Text(
                text = "${subject.score}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = subject.mapel,
                style = MaterialTheme.typography.labelMedium,
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(SDM3Route.DetailPengumuman("ANN-001")) },
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = SDM3Shapes.small,
                color = colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Campaign,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
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
                    text = "Libur semester ganjil dimulai 20 Des 2025 \u2013 3 Jan 2026.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Lihat Detail",
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    SDM3Theme {
        HomeScreen(studentId = "", navController = rememberNavController())
    }
}
