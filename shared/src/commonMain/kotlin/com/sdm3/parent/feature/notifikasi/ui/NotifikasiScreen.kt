package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
import org.koin.compose.viewmodel.koinViewModel

private val filterOptions = listOf("Semua", "Akademik", "Keuangan", "Pengumuman")

data class NotifItem(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean
)

private val notifList = listOf(
    NotifItem("1", "akademik", "Pembaruan Nilai: Matematika", "Ananda mendapatkan skor Sumatif Matematika: 92 (A). Detail tersedia di portal.", "5 menit lalu", false),
    NotifItem("2", "keuangan", "Tagihan Pendidikan Terbit", "Tagihan SPP Juli 2026 sebesar Rp350.000 telah siap untuk diselesaikan.", "1 jam lalu", false),
    NotifItem("3", "pengumuman", "Informasi Libur Institusi", "Sehubungan dengan Hari Raya, kegiatan belajar mengajar dijadwalkan libur.", "3 jam lalu", false),
    NotifItem("4", "akademik", "Prestasi Projek IPA", "Ananda menyelesaikan Projek IPA dengan skor optimal: 95 (A).", "Kemarin", true),
    NotifItem("5", "pengumuman", "Kick-off Projek P5", "Projek Penguatan Profil Pelajar Pancasila tema Kelestarian Alam dimulai.", "Kemarin", true),
    NotifItem("6", "keuangan", "Otentikasi Pembayaran", "Pembayaran SPP Juni 2026 sebesar Rp350.000 telah divalidasi sistem.", "3 hari lalu", true),
)

private sealed class LazyNotifItem {
    data class Header(val title: String) : LazyNotifItem()
    data class Notif(val item: NotifItem) : LazyNotifItem()
}

private fun iconForType(type: String): ImageVector = when (type) {
    "akademik" -> Icons.Outlined.School
    "keuangan" -> Icons.Outlined.Payments
    "pengumuman" -> Icons.Outlined.Campaign
    else -> Icons.Outlined.Notifications
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    viewModel: NotifikasiViewModel? = if (LocalInspectionMode.current) null else koinViewModel()
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Notifikasi Portal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "PUSAT INFORMASI TERPADU",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel?.markAllAsRead() }) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = colorScheme.primary.copy(alpha = 0.05f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.DoneAll,
                                    contentDescription = "Baca Semua",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
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
            ) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedFilter,
                    containerColor = Color.Transparent,
                    contentColor = colorScheme.primary,
                    edgePadding = 24.dp,
                    divider = {},
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedFilter),
                            color = colorScheme.secondary
                        )
                    }
                ) {
                    filterOptions.forEachIndexed { index, label ->
                        Tab(
                            selected = selectedFilter == index,
                            onClick = { selectedFilter = index },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            selectedContentColor = colorScheme.secondary,
                            unselectedContentColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                val filteredNotifs = if (selectedFilter == 0) notifList
                else notifList.filter { it.type == filterOptions[selectedFilter].lowercase() }

                if (filteredNotifs.isEmpty()) {
                    EmptyNotifikasiState()
                } else {
                    val lazyItems = buildList {
                        var lastGroup = ""
                        for (notif in filteredNotifs) {
                            val dateGroup = when {
                                notif.timestamp.contains("menit") || notif.timestamp.contains("jam") -> "BARU INI"
                                notif.timestamp.contains("Kemarin") -> "KEMARIN"
                                else -> "RIWAYAT"
                            }
                            if (dateGroup != lastGroup) {
                                add(LazyNotifItem.Header(dateGroup))
                                lastGroup = dateGroup
                            }
                            add(LazyNotifItem.Notif(notif))
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lazyItems) { lazyItem ->
                            when (lazyItem) {
                                is LazyNotifItem.Header -> {
                                    Text(
                                        text = lazyItem.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                        color = colorScheme.primary.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                                    )
                                }
                                is LazyNotifItem.Notif -> {
                                    val notif = lazyItem.item
                                    Sdm3Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        padding = 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    if (!notif.isRead) colorScheme.primary.copy(alpha = 0.03f)
                                                    else Color.Transparent
                                                )
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = colorScheme.primary.copy(alpha = 0.05f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        iconForType(notif.type),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(22.dp),
                                                        tint = colorScheme.primary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = notif.title,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                                        color = colorScheme.primary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = notif.timestamp,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = colorScheme.primary.copy(alpha = 0.3f),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = notif.body,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    lineHeight = 20.sp
                                                )
                                            }
                                            if (!notif.isRead) {
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(colorScheme.secondary)
                                                        .align(Alignment.CenterVertically)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotifikasiState() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(32.dp),
            color = colorScheme.primary.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colorScheme.primary.copy(alpha = 0.2f)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Hening Di Sini",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Seluruh aktivitas akademik dan administrasi Anda akan muncul di pusat notifikasi ini.",
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp),
            lineHeight = 26.sp
        )
    }
}

@Preview
@Composable
private fun NotifikasiScreenPreview() {
    SDM3Theme {
        NotifikasiScreen(onBack = {})
    }
}
