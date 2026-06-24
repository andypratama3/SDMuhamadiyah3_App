package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
import org.koin.compose.viewmodel.koinViewModel

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

private fun iconForType(type: String): ImageVector = when (type) {
    "nilai" -> Icons.Outlined.School
    "pembayaran" -> Icons.Outlined.CreditCard
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
            TopAppBar(
                title = {
                    Text(
                        "Notifikasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel?.markAllAsRead() }) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = SDM3Shapes.small,
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = "Tandai semua dibaca",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
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
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md)
            ) {
                filterOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = filterOptions.size
                        )
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedFilter == index) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            val filteredNotifs = if (selectedFilter == 0) notifList
            else notifList.filter { it.type == filterOptions[selectedFilter].lowercase() }

            if (filteredNotifs.isEmpty()) {
                EmptyNotifikasiState()
            } else {
                val lazyItems = buildList {
                    var lastGroup = ""
                    for (notif in filteredNotifs) {
                        val dateGroup = when {
                            notif.timestamp.contains("menit") || notif.timestamp.contains("jam") -> "Baru Ini"
                            notif.timestamp.contains("Kemarin") -> "Kemarin"
                            notif.timestamp.contains("hari") -> "Beberapa Hari Lalu"
                            else -> "Lainnya"
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
                    contentPadding = PaddingValues(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(lazyItems) { lazyItem ->
                        when (lazyItem) {
                            is LazyNotifItem.Header -> {
                                Text(
                                    text = lazyItem.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = Spacing.sm)
                                )
                            }
                            is LazyNotifItem.Notif -> {
                                val notif = lazyItem.item
                                val typeColor = when (notif.type) {
                                    "nilai" -> colorScheme.secondary
                                    "pembayaran" -> colorScheme.error
                                    "pengumuman" -> StatusWarning
                                    else -> StatusSuccess
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = CardShape,
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!notif.isRead) colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                       else colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (!notif.isRead) 0.dp else 0.dp
                                    )
                                ) {
                                    Row(modifier = Modifier.padding(Spacing.md)) {
                                        Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = SDM3Shapes.small,
                                            color = typeColor.copy(alpha = 0.1f)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    iconForType(notif.type),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = typeColor
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(Spacing.md))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = notif.title,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = if (!notif.isRead) FontWeight.SemiBold else FontWeight.Normal,
                                                        color = colorScheme.onSurface
                                                    )
                                                }
                                                Text(
                                                    text = notif.timestamp,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(Spacing.xxs))
                                            Text(
                                                text = notif.body,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        if (!notif.isRead) {
                                            Spacer(modifier = Modifier.width(Spacing.sm))
                                            Box(
                                                modifier = Modifier
                                                    .width(2.5.dp)
                                                    .height(40.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(typeColor)
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
}

@Composable
private fun EmptyNotifikasiState() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = SDM3Shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = "Belum ada notifikasi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Text(
            text = "Semua aktivitas akan muncul di sini",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
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
