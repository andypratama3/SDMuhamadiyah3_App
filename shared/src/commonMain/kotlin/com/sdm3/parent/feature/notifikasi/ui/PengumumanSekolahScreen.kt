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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengumumanSekolahScreen(
    onBack: () -> Unit,
    onDetailClick: (String) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    var selectedCategory by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme
    var startAnimation by remember { mutableStateOf(isPreview) }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            startAnimation = true
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Informasi Sekolah",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
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
                        Icon(Icons.Outlined.Search, contentDescription = "Cari", tint = colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        // Background Glow
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, 0f),
                        radius = size.width
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val categoryFilters = listOf("Semua", "Umum", "Akademik", "Kegiatan")

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedCategory,
                containerColor = Color.Transparent,
                contentColor = colorScheme.primary,
                edgePadding = Spacing.lg,
                divider = {},
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedCategory),
                        color = colorScheme.secondary
                    )
                }
            ) {
                categoryFilters.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        text = {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedCategory == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = colorScheme.secondary,
                        unselectedContentColor = colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md)
            ) {
                val dummyAnnouncements = listOf(
                    "Libur Hari Raya Idul Adha 1447 H",
                    "Rapor Sumatif 1 Telah Terbit",
                    "Jadwal Pembayaran SPP Semester Baru",
                    "Pendaftaran Ekstrakurikuler 2026/2027",
                    "Sosialisasi Program Tahfiz Quran"
                )

                itemsIndexed(dummyAnnouncements) { index, title ->
                    Sdm3Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailClick("announcement_$index") }
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(52.dp, 60.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = colorScheme.primary.copy(alpha = 0.05f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "18",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                    Text(
                                        text = "JUN",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = colorScheme.secondary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "PENGUMUMAN UMUM",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        letterSpacing = 0.5.sp
                                    )
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

@Preview
@Composable
private fun PengumumanSekolahScreenPreview() {
    SDM3Theme {
        PengumumanSekolahScreen(onBack = {}, onDetailClick = {})
    }
}
