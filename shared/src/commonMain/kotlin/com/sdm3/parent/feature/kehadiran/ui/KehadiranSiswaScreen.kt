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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import org.koin.compose.viewmodel.koinViewModel

private val PremiumEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

private val todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

private fun pad2(n: Int): String = if (n < 10) "0$n" else "$n"

private fun daysInMonth(month: Int, year: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
    else -> 30
}

sealed class KehadiranScreenUiState {
    data object Loading : KehadiranScreenUiState()
    data object Empty : KehadiranScreenUiState()
    data class Error(val message: String) : KehadiranScreenUiState()
    data object Success : KehadiranScreenUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KehadiranSiswaScreen(
    studentId: String,
    onBack: () -> Unit,
    viewModel: KehadiranSiswaViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val uiState by if (isPreview) {
        remember { mutableStateOf(KehadiranSiswaUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()

    val errorMessage = uiState.errorMessage

    val screenState = remember(uiState.isLoading, uiState.isEmpty, errorMessage, isPreview) {
        if (isPreview) KehadiranScreenUiState.Success
        else when {
            uiState.isLoading && uiState.isEmpty -> KehadiranScreenUiState.Loading
            errorMessage != null -> KehadiranScreenUiState.Error(errorMessage)
            !uiState.isLoading && uiState.isEmpty -> KehadiranScreenUiState.Empty
            else -> KehadiranScreenUiState.Success
        }
    }

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
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width
                    )
                )
            }

            when (screenState) {
                is KehadiranScreenUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { TodayShimmer() }
                        item { SummaryShimmer() }
                        item { CalendarShimmer() }
                        item { Spacer(Modifier.height(8.dp)) }
                        item { LogRowShimmer() }
                        item { LogRowShimmer() }
                        item { LogRowShimmer() }
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
                is KehadiranScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data",
                        message = screenState.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.refresh() }
                            )
                        }
                    )
                }
                is KehadiranScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Data Presensi",
                        message = "Data kehadiran siswa belum tersedia.",
                        style = EmptyStateStyle.Neutral
                    )
                }
                is KehadiranScreenUiState.Success -> {
                    val attendances = uiState.attendances
                    val summary = uiState.summary
                    val month = uiState.selectedMonth
                    val year = uiState.selectedYear

                    val presentCount = summary?.hadir ?: attendances.count { it.status.equals("hadir", true) || it.status.equals("present", true) }
                    val sickCount = summary?.sakit ?: attendances.count { it.status == "sakit" }
                    val izinCount = summary?.izin ?: attendances.count { it.status == "izin" }
                    val alpaCount = summary?.alpa ?: attendances.count { it.status == "alpa" }

                    val attendancesByDay = attendances.mapNotNull { att ->
                        val day = att.date.split("-").last().toIntOrNull()
                        if (day != null) day to att else null
                    }.toMap()

                    // Susun sel kalender dengan offset hari-pertama agar tanggal jatuh
                    // di kolom hari (MIN..SAB) yang benar; sel kosong di awal = null.
                    val totalDays = daysInMonth(month, year)
                    val firstOffset = (LocalDate(year, month, 1).dayOfWeek.ordinal + 1) % 7
                    val cells: List<Int?> = List(firstOffset) { null } + (1..totalDays).toList()
                    val weeks = cells.chunked(7)

                    val todayIso = todayDate.toString()
                    // "Status hari ini" hanya valid bila ada catatan untuk tanggal hari
                    // ini yang sebenarnya — jangan memalsukan "Hadir" sebagai default.
                    val todayAttendance = attendances.firstOrNull { it.date == todayIso }
                    val todayStatusLabel = when (todayAttendance?.status) {
                        "hadir" -> "Terverifikasi Hadir"
                        "sakit" -> "Sedang Sakit"
                        "izin" -> "Izin Tidak Hadir"
                        "alpa" -> "Tanpa Keterangan"
                        else -> "Belum Ada Data Hari Ini"
                    }
                    val todayNotes = todayAttendance?.notes
                    val todayTimeLocation = when {
                        !todayNotes.isNullOrBlank() -> todayNotes
                        todayAttendance != null -> "Tercatat hari ini"
                        else -> "Belum tercatat"
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            TodayAttendanceCard(
                                statusLabel = todayStatusLabel,
                                timeLocation = todayTimeLocation
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    label = "HADIR",
                                    count = "$presentCount",
                                    color = statusSuccess,
                                    icon = Icons.Outlined.CheckCircle
                                )
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    label = "SAKIT",
                                    count = "$sickCount",
                                    color = statusWarning,
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
                                    count = "$izinCount",
                                    color = colorScheme.primary,
                                    icon = Icons.Outlined.EventAvailable
                                )
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    label = "ALPA",
                                    count = "$alpaCount",
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
                                            text = "${monthName(month)} $year",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clickable(enabled = !isPreview) {
                                                        val (prevMonth, prevYear) = if (month <= 1) 12 to (year - 1) else (month - 1) to year
                                                        viewModel.changeMonth(prevMonth, prevYear)
                                                    },
                                                shape = RoundedCornerShape(8.dp),
                                                color = colorScheme.primary.copy(alpha = 0.05f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Bulan sebelumnya", tint = colorScheme.primary, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                            Surface(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clickable(enabled = !isPreview) {
                                                        val (nextMonth, nextYear) = if (month >= 12) 1 to (year + 1) else (month + 1) to year
                                                        viewModel.changeMonth(nextMonth, nextYear)
                                                    },
                                                shape = RoundedCornerShape(8.dp),
                                                color = colorScheme.primary.copy(alpha = 0.05f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Bulan berikutnya", tint = colorScheme.primary, modifier = Modifier.size(18.dp))
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

                                    weeks.forEach { week ->
                                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            week.forEachIndexed { columnIndex, day ->
                                                if (day == null) {
                                                    Spacer(Modifier.weight(1f))
                                                    return@forEachIndexed
                                                }
                                                val att = attendancesByDay[day]
                                                val status = att?.status
                                                val dayIso = "$year-${pad2(month)}-${pad2(day)}"
                                                val isToday = dayIso == todayIso
                                                val isSunday = columnIndex == 0
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .aspectRatio(1f),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isToday) {
                                                        Surface(
                                                            modifier = Modifier.fillMaxSize(0.85f),
                                                            shape = RoundedCornerShape(12.dp),
                                                            color = colorScheme.primary,
                                                            border = BorderStroke(1.dp, colorScheme.primary)
                                                        ) {
                                                            DayContent(day, isToday = true, colorScheme, onPrimary = true, status = status, isSunday = isSunday)
                                                        }
                                                    } else {
                                                        DayContent(day, isToday = false, colorScheme, status = status, isSunday = isSunday)
                                                    }
                                                }
                                            }
                                            if (week.size < 7) {
                                                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        listOf("Hadir" to statusSuccess, "Sakit" to statusWarning, "Alpa" to colorScheme.error).forEach { (label, color) ->
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

                        if (attendances.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Log Aktivitas",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            itemsIndexed(attendances) { _, att ->
                                val logStatus = att.status
                                val (logColor, logIcon) = when (logStatus) {
                                    "hadir" -> statusSuccess to Icons.Outlined.CheckCircle
                                    "sakit" -> statusWarning to Icons.Outlined.MedicalServices
                                    "izin" -> colorScheme.primary to Icons.Outlined.EventAvailable
                                    "alpa" -> colorScheme.error to Icons.Outlined.Cancel
                                    else -> colorScheme.onSurfaceVariant.copy(alpha = 0.3f) to Icons.Outlined.Info
                                }
                                val logNote = att.notes ?: when (logStatus) {
                                    "hadir" -> "Hadir sesuai jadwal."
                                    "sakit" -> "Tidak hadir karena sakit."
                                    "izin" -> "Izin tidak hadir."
                                    "alpa" -> "Tanpa keterangan."
                                    else -> ""
                                }
                                val displayStatus = logStatus.replaceFirstChar { it.uppercase() }
                                AttendanceLogRow(att.date, displayStatus, logNote, "", logColor, logIcon)
                            }
                        }

                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun SummaryShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Sdm3Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Sdm3Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarShimmer() {
    Sdm3Card(padding = 20.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            repeat(5) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun LogRowShimmer() {
    Sdm3Card {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
            }
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun DayContent(day: Int, isToday: Boolean, colorScheme: ColorScheme, onPrimary: Boolean = false, status: String? = null, isSunday: Boolean = false) {
    val heroContent = heroContentColor()
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val dotColor = when (status) {
        "hadir" -> if (onPrimary) heroContent else statusSuccess
        "sakit" -> statusWarning
        "izin" -> colorScheme.primary
        "alpa" -> colorScheme.error
        else -> null
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "$day",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday) FontWeight.Black else FontWeight.Bold,
            color = if (onPrimary) heroContent else if (isSunday) colorScheme.error else colorScheme.primary
        )
        if (dotColor != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(dotColor))
        }
    }
}

private fun monthName(month: Int): String = when (month) {
    1 -> "Januari"
    2 -> "Februari"
    3 -> "Maret"
    4 -> "April"
    5 -> "Mei"
    6 -> "Juni"
    7 -> "Juli"
    8 -> "Agustus"
    9 -> "September"
    10 -> "Oktober"
    11 -> "November"
    12 -> "Desember"
    else -> ""
}

@Composable
private fun TodayAttendanceCard(
    statusLabel: String = "Terverifikasi Hadir",
    timeLocation: String = "Pukul 06:58 \u00B7 Gerbang Utama"
) {
    val colorScheme = MaterialTheme.colorScheme
    val heroContent = heroContentColor()

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
                    color = heroContent.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Fingerprint,
                            contentDescription = null,
                            tint = heroContent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "STATUS HARI INI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = heroContent.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = heroContent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = heroContent.copy(alpha = 0.8f)
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
