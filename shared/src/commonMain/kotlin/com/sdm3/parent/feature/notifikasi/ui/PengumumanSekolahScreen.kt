package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengumumanSekolahScreen(
    onBack: () -> Unit,
    onDetailClick: (String) -> Unit
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Info Sekolah",
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
                        Icon(Icons.Outlined.Search, contentDescription = "Cari", tint = colorScheme.onSurface)
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
        ) {
            val categoryFilters = listOf("Semua", "Umum", "Akademik", "Keuangan", "Kegiatan")

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            ) {
                categoryFilters.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = categoryFilters.size
                        )
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedCategory == index) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
            ) {
                val dummyAnnouncements = listOf(
                    "Libur Hari Raya Idul Adha 1447 H",
                    "Rapor Sumatif 1 Telah Terbit",
                    "Jadwal Pembayaran SPP Semester Baru",
                    "Pendaftaran Ekstrakurikuler 2026/2027",
                    "Sosialisasi Program Tahfiz Quran"
                )

                itemsIndexed(dummyAnnouncements) { index, title ->
                    AnimatedVisibility(
                        visible = startAnimation,
                        enter = fadeIn(androidx.compose.animation.core.tween(500, delayMillis = index * 100)) +
                                slideInVertically(androidx.compose.animation.core.tween(500, delayMillis = index * 100)) { it / 4 }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDetailClick("announcement_$index") },
                            shape = CardShape,
                            color = colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.lg),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp, 64.dp),
                                    shape = SDM3Shapes.medium,
                                    color = colorScheme.primaryContainer
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = "18",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = colorScheme.primary
                                        )
                                        Text(
                                            text = "JUN",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(Spacing.md))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xs))
                                    Surface(
                                        shape = ChipShape,
                                        color = colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "Pengumuman Umum",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
            }
        }
    }
}
