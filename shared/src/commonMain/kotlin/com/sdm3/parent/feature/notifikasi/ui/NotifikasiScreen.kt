package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

private val filterOptions = listOf("Semua", "Nilai", "Pembayaran", "Pengumuman")

data class NotifItem(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean
)

private val notifList = listOf(
    NotifItem("1", "nilai", "Nilai Baru: Matematika", "Ananda mendapatkan nilai Sumatif Matematika: 92 (A)", "5 menit lalu", false),
    NotifItem("2", "pembayaran", "Tagihan SPP Juli 2026", "Tagihan SPP Juli 2026 sebesar Rp350.000 telah diterbitkan", "1 jam lalu", false),
    NotifItem("3", "pengumuman", "Libur Hari Raya", "Sehubungan dengan Hari Raya Idul Adha, pembelajaran diliburkan.", "3 jam lalu", false),
    NotifItem("4", "nilai", "Nilai Baru: IPA", "Ananda mendapatkan nilai Projek IPA: 95 (A)", "Kemarin", true),
    NotifItem("5", "pengumuman", "Kegiatan P5", "Projek P5 tema Gaya Hidup Berkelanjutan dimulai.", "Kemarin", true),
    NotifItem("6", "pembayaran", "Pembayaran Berhasil", "Pembayaran SPP Juni 2026 sebesar Rp350.000 berhasil.", "3 hari lalu", true),
    NotifItem("7", "nilai", "Rapor Tersedia", "Rapor Sumatif 1 Tahun 2025/2026 telah tersedia.", "1 minggu lalu", true),
)

private sealed class LazyNotifItem {
    data class Header(val title: String) : LazyNotifItem()
    data class Notif(val item: NotifItem) : LazyNotifItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    viewModel: NotifikasiViewModel = koinViewModel()
) {
    var selectedFilter by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllRead() }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Tandai semua dibaca")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(filterOptions.size) { index ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(filterOptions[index]) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Secondary,
                            selectedLabelColor = SurfaceWhite,
                            containerColor = SurfaceWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            val filteredNotifs = if (selectedFilter == 0) notifList
            else notifList.filter { it.type == filterOptions[selectedFilter].lowercase() }

            if (filteredNotifs.isEmpty()) {
                EmptyNotifikasiState()
            } else {
                val lazyItems = buildList {
                    var lastDateSeparator = ""
                    for (notif in filteredNotifs) {
                        val dateGroup = when {
                            notif.timestamp.contains("menit") || notif.timestamp.contains("jam") -> "Baru Ini"
                            notif.timestamp.contains("Kemarin") -> "Kemarin"
                            notif.timestamp.contains("hari") -> "Beberapa Hari Lalu"
                            else -> "Lainnya"
                        }
                        if (dateGroup != lastDateSeparator) {
                            add(LazyNotifItem.Header(dateGroup))
                            lastDateSeparator = dateGroup
                        }
                        add(LazyNotifItem.Notif(notif))
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(lazyItems) { lazyItem ->
                        when (lazyItem) {
                            is LazyNotifItem.Header -> {
                                Text(
                                    text = lazyItem.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                                    color = OnSurfaceVariant
                                )
                            }
                            is LazyNotifItem.Notif -> {
                                val notif = lazyItem.item
                                val typeColor = when (notif.type) {
                                    "nilai" -> Primary
                                    "pembayaran" -> Secondary
                                    "pengumuman" -> StatusWarning
                                    else -> StatusSuccess
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Spacing.md),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (notif.isRead) SurfaceWhite else Primary.copy(alpha = 0.03f)
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (notif.isRead) 0.dp else 2.dp
                                    )
                                ) {
                                    Row(modifier = Modifier.padding(Spacing.md)) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(typeColor.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Notifications,
                                                contentDescription = notif.type,
                                                modifier = Modifier.size(28.dp),
                                                tint = typeColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(Spacing.md))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = notif.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.SemiBold,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = notif.timestamp,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = OnSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = notif.body,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (notif.isRead) OnSurfaceVariant.copy(alpha = 0.7f) else OnSurfaceVariant,
                                                maxLines = 2
                                            )
                                        }
                                        if (!notif.isRead) {
                                            Spacer(modifier = Modifier.width(Spacing.sm))
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Secondary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotifikasiState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = "Belum ada notifikasi",
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        IconButton(onClick = { }) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }
}
