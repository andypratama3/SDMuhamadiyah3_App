package com.sdm3.parent.feature.kehadiran.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.kehadiran.KehadiranSiswaUiState
import com.sdm3.parent.feature.kehadiran.KehadiranSiswaViewModel
import org.koin.compose.viewmodel.koinViewModel

private val PremiumEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KehadiranSiswaScreen(
    studentId: String,
    onBack: () -> Unit,
    viewModel: KehadiranSiswaViewModel? = if (LocalInspectionMode.current) null else koinViewModel()
) {
    val isPreview = viewModel == null
    val uiState by if (isPreview) {
        remember { mutableStateOf(KehadiranSiswaUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    if (!isPreview) {
        LaunchedEffect(studentId) {
            viewModel.loadAttendances(studentId)
            viewModel.loadSummary(studentId)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Presensi Siswa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "MONITORING REAL-TIME",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Atmospheric Background Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TodayAttendanceCard()
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = "HADIR",
                            count = "18",
                            color = StatusSuccess,
                            icon = Icons.Outlined.CheckCircle
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = "SAKIT",
                            count = "1",
                            color = StatusWarning,
                            icon = Icons.Outlined.MedicalServices
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = "IZIN",
                            count = "2",
                            color = colorScheme.primary,
                            icon = Icons.Outlined.EventAvailable
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = "ALPA",
                            count = "0",
                            color = colorScheme.error,
                            icon = Icons.Outlined.Cancel
                        )
                    }
                }

                item {
                    SectionHeader(
                        title = "Kalender Presensi",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Sdm3Card(padding = 20.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Oktober 2026",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = colorScheme.primary.copy(alpha = 0.05f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf("MIN", "SEN", "SEL", "RAB", "KAM", "JUM", "SAB").forEach {
                                    Text(
                                        text = it,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = colorScheme.primary.copy(alpha = 0.3f),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val days = (1..30).toList()
                            val rows = days.chunked(7)
                            rows.forEach { row ->
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    row.forEach { day ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val isToday = day == 12
                                            if (isToday) {
                                                Surface(
                                                    modifier = Modifier.fillMaxSize(0.85f),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = colorScheme.primary,
                                                    border = BorderStroke(1.dp, colorScheme.primary)
                                                ) {
                                                    DayContent(day, isToday, colorScheme, true)
                                                }
                                            } else {
                                                DayContent(day, false, colorScheme)
                                            }
                                        }
                                    }
                                    if (row.size < 7) {
                                        repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                listOf("Hadir" to StatusSuccess, "Sakit" to StatusWarning, "Alpa" to colorScheme.error).forEach { (label, color) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Log Aktivitas",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item { AttendanceLogRow("Senin, 12 Okt", "Hadir", "Tepat waktu di sekolah.", "06:58", StatusSuccess, Icons.Outlined.CheckCircle) }
                item { AttendanceLogRow("Jumat, 9 Okt", "Sakit", "Demam, istirahat di rumah.", "07:15", StatusWarning, Icons.Outlined.MedicalServices) }
                item { AttendanceLogRow("Kamis, 8 Okt", "Hadir", "Hadir sesuai jadwal.", "07:02", StatusSuccess, Icons.Outlined.CheckCircle) }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun DayContent(day: Int, isToday: Boolean, colorScheme: ColorScheme, onPrimary: Boolean = false) {
    val presentDays = listOf(1, 2, 5, 6, 7, 8, 9, 12, 13, 14, 15)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "$day",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday) FontWeight.Black else FontWeight.Bold,
            color = if (onPrimary) Color.White else if (day % 7 == 0) colorScheme.error else colorScheme.primary
        )
        if (presentDays.contains(day)) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(if (onPrimary) Color.White else StatusSuccess))
        }
    }
}

@Composable
private fun TodayAttendanceCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val glowColor = colorScheme.surfaceTint.copy(alpha = 0.4f)
            Canvas(modifier = Modifier.fillMaxWidth().height(140.dp).alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        center = Offset(size.width * 0.9f, 0f),
                        radius = size.width
                    )
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Fingerprint,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "STATUS HARI INI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Terverifikasi Hadir",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pukul 06:58 \u00B7 Gerbang Utama",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(modifier: Modifier = Modifier, label: String, count: String, color: Color, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
                Text(
                    text = count,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = colorScheme.primary.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AttendanceLogRow(date: String, status: String, note: String, time: String, color: Color, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(date, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            status.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(note, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Text(time, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary.copy(alpha = 0.3f))
        }
    }
}

@Preview
@Composable
private fun KehadiranSiswaScreenPreview() {
    SDM3Theme {
        KehadiranSiswaScreen(
            studentId = "",
            onBack = {}
        )
    }
}
