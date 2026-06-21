package com.sdm3.parent.feature.kehadiran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Icon
import com.sdm3.parent.feature.kehadiran.KehadiranSiswaViewModel
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

data class AttendanceSummary(
    val label: String,
    val count: Int,
    val icon: ImageVector,
    val color: Color
)

private val summaryData = listOf(
    AttendanceSummary("Hadir", 18, Icons.Default.CheckCircle, StatusSuccess),
    AttendanceSummary("Sakit", 1, Icons.Default.MedicalServices, StatusWarning),
    AttendanceSummary("Izin", 0, Icons.Default.DateRange, Secondary),
    AttendanceSummary("Alpa", 0, Icons.Default.Cancel, StatusDanger)
)

private val daysInMonth = listOf(
    null, null, null, null, null, 1, 2,
    3, 4, 5, 6, 7, 8, 9,
    10, 11, 12, 13, 14, 15, 16,
    17, 18, 19, 20, 21, 22, 23,
    24, 25, 26, 27, 28, 29, 30
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KehadiranSiswaScreen(
    studentId: String,
    onBack: () -> Unit = {},
    viewModel: KehadiranSiswaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadAttendances(studentId)
        viewModel.loadSummary(studentId)
    }

    val summaryData = listOf(
        AttendanceSummary("Hadir", uiState.summary?.present ?: 0, Icons.Default.CheckCircle, StatusSuccess),
        AttendanceSummary("Sakit", uiState.summary?.sick ?: 0, Icons.Default.MedicalServices, StatusWarning),
        AttendanceSummary("Izin", uiState.summary?.excused ?: 0, Icons.Default.DateRange, Secondary),
        AttendanceSummary("Alpa", uiState.summary?.absent ?: 0, Icons.Default.Cancel, StatusDanger)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kehadiran")
                        Text(
                            text = "Monitoring Kehadiran Siswa",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
        if (uiState.isLoading && uiState.attendances.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            val attendedDays = uiState.attendances.filter { it.status == "Hadir" }.mapNotNull { it.date.split("-").lastOrNull()?.toInt() }.toSet()
            val sickDays = uiState.attendances.filter { it.status == "Sakit" }.mapNotNull { it.date.split("-").lastOrNull()?.toInt() }.toSet()
            val excusedDays = uiState.attendances.filter { it.status == "Izin" }.mapNotNull { it.date.split("-").lastOrNull()?.toInt() }.toSet()
            val absentDays = uiState.attendances.filter { it.status == "Alpa" }.mapNotNull { it.date.split("-").lastOrNull()?.toInt() }.toSet()
            val today = 0 // Needs proper date handling
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "Juni 2026",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("● Hadir", color = StatusSuccess, style = MaterialTheme.typography.labelSmall)
                Text("● Sakit", color = StatusWarning, style = MaterialTheme.typography.labelSmall)
                Text("● Izin", color = Secondary, style = MaterialTheme.typography.labelSmall)
                Text("● Alpa", color = StatusDanger, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(220.dp),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(summaryData) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = item.color
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Column {
                                Text(
                                    text = "${item.count}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.color
                                )
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Ringkasan Bulan Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            val dayHeaders = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dayHeaders.forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    val weeks = daysInMonth.chunked(7)
                    weeks.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            week.forEach { day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .then(
                                            if (day != null) {
                                                val color = when {
                                                    absentDays.contains(day) -> StatusDanger
                                                    sickDays.contains(day) -> StatusWarning
                                                    excusedDays.contains(day) -> Secondary
                                                    attendedDays.contains(day) -> StatusSuccess
                                                    else -> Color.Transparent
                                                }
                                                Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (day == today) Primary.copy(alpha = 0.15f)
                                                        else color.copy(alpha = 0.2f)
                                                    )
                                                    .clickable { /* Detail for day */ }
                                            } else Modifier.size(32.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (day != null) {
                                        val textColor = when {
                                            absentDays.contains(day) -> StatusDanger
                                            sickDays.contains(day) -> StatusWarning
                                            day == today && !attendedDays.contains(day) && !sickDays.contains(day) -> Primary
                                            else -> Color.Black
                                        }
                                        Text(
                                            text = "$day",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (attendedDays.contains(day) || sickDays.contains(day) || absentDays.contains(day)) FontWeight.Bold else FontWeight.Normal,
                                            color = if (attendedDays.contains(day)) StatusSuccess else textColor,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Detail Kehadiran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            uiState.attendances.forEach { attendance ->
                val logColor = when (attendance.status) {
                    "Hadir" -> StatusSuccess
                    "Sakit" -> StatusWarning
                    "Izin" -> Secondary
                    else -> StatusDanger
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = attendance.date,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        StatusChip(text = attendance.status, color = logColor)
                    }
                }
            }
        }
        }
        }
    }
}

data class AttendanceLog(
    val date: String,
    val status: String,
    val color: Color
)
