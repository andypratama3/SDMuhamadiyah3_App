package com.sdm3.parent.feature.kehadiran.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.kehadiran.KehadiranSiswaViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KehadiranSiswaScreen(
    studentId: String,
    onBack: () -> Unit,
    viewModel: KehadiranSiswaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(studentId) {
        viewModel.loadAttendances(studentId)
        viewModel.loadSummary(studentId)
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column {
                            Text(
                                "Kehadiran",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = uiState.studentId.ifEmpty { "Ahmad Fathoni" },
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable { },
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = "Oktober 2023",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        SummaryCard(modifier = Modifier.weight(1f), label = "Hadir", count = "18", color = StatusSuccess, icon = Icons.Outlined.CheckCircle)
                        SummaryCard(modifier = Modifier.weight(1f), label = "Sakit", count = "1", color = StatusWarning, icon = Icons.Outlined.MedicalServices)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        SummaryCard(modifier = Modifier.weight(1f), label = "Izin", count = "2", color = colorScheme.secondary, icon = Icons.Outlined.EventAvailable)
                        SummaryCard(modifier = Modifier.weight(1f), label = "Alpa", count = "0", color = StatusDanger, icon = Icons.Outlined.Cancel)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ringkasan Bulan Ini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                            Row {
                                Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(Spacing.md))
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("M", "S", "S", "R", "K", "J", "S").forEach {
                                Text(
                                    text = it,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

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
                                        if (day == 9) {
                                            Box(modifier = Modifier.fillMaxSize(0.8f).clip(RoundedCornerShape(8.dp)).background(colorScheme.primary.copy(alpha = 0.1f)))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$day",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (day == 9) FontWeight.SemiBold else FontWeight.Medium,
                                                color = if (day == 7) StatusDanger else if (day == 9) colorScheme.primary else colorScheme.onSurface
                                            )
                                            if (listOf(1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13).contains(day)) {
                                                Box(modifier = Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(StatusSuccess))
                                            }
                                        }
                                    }
                                }
                                if (row.size < 7) { repeat(7 - row.size) { Spacer(Modifier.weight(1f)) } }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Harian",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.md)
                )
            }

            item { AttendanceLogRow("Senin, 4 Okt", "Sakit", "Demam dan flu, istirahat di rumah.", "07:15", StatusWarning, Icons.Outlined.MedicalServices) }
            item { AttendanceLogRow("Selasa, 3 Okt", "Hadir", "Tepat waktu.", "06:58", StatusSuccess, Icons.Outlined.CheckCircle) }
            item { AttendanceLogRow("Sabtu, 30 Sep", "Izin", "Acara keluarga di luar kota.", "-", colorScheme.secondary, Icons.Outlined.EventAvailable) }

            item { Spacer(Modifier.height(Spacing.xxxl)) }
        }
    }
}

@Composable
private fun SummaryCard(modifier: Modifier = Modifier, label: String, count: String, color: Color, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                Spacer(Modifier.width(Spacing.xxs))
                Text(
                    text = "Hari",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun AttendanceLogRow(date: String, status: String, note: String, time: String, color: Color, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                    Spacer(Modifier.width(Spacing.sm))
                    Surface(
                        shape = RoundedCornerShape(100),
                        color = color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            status,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(note, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
            }
            Text(time, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}
