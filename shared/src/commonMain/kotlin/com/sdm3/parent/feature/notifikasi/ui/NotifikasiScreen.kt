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
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.component.EmptyStateStyle
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.notifikasi.NotifikasiUiState
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import org.koin.compose.viewmodel.koinViewModel

private val filterOptions = listOf("Semua", "Akademik", "Keuangan", "Pengumuman")

// Backend memakai kunci tipe bahasa Inggris (grade/payment/attendance/announcement/
// general/leave/…). Petakan ke kategori tab agar filter benar-benar bekerja
// (sebelumnya membandingkan langsung ke "akademik/keuangan/pengumuman" → selalu kosong).
private fun notifMatchesCategory(type: String, category: String): Boolean {
    val t = type.lowercase()
    return when (category) {
        "Akademik" -> t in setOf("grade", "nilai", "rapor", "report", "akademik", "academic", "attendance", "kehadiran", "leave", "izin", "exam", "ujian")
        "Keuangan" -> t in setOf("payment", "payment_receipt", "keuangan", "spp", "finance", "tagihan", "invoice", "billing")
        "Pengumuman" -> t in setOf("announcement", "pengumuman", "general", "info", "broadcast", "umum")
        else -> true
    }
}

sealed class NotifUiState {
    data object Loading : NotifUiState()
    data object Empty : NotifUiState()
    data class Error(val message: String) : NotifUiState()
    data object Success : NotifUiState()
}

data class NotifItem(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean
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
    onRetry: () -> Unit = {},
    uiState: NotifUiState = NotifUiState.Success,
    viewModel: NotifikasiViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    var selectedFilter by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme

    val vmState by if (isPreview) {
        remember { mutableStateOf(NotifikasiUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    val screenState = remember(vmState, isPreview) {
        if (isPreview) uiState
        else {
            val s = vmState
            when {
                s.isLoading -> NotifUiState.Loading
                s.errorMessage != null -> NotifUiState.Error(s.errorMessage)
                s.isEmpty -> NotifUiState.Empty
                else -> NotifUiState.Success
            }
        }
    }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            viewModel.loadNotifications()
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Notifikasi",
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
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
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
                                    modifier = Modifier.size(22.dp)
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
            // Modern Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.4f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primary.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.1f),
                        radius = size.width * 1.5f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.9f),
                        radius = size.width * 1.0f
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

                when (screenState) {
                    is NotifUiState.Loading -> {
                        ShimmerNotifikasiList()
                    }
                    is NotifUiState.Empty -> {
                        EmptyNotifikasiState()
                    }
                    is NotifUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Sdm3ErrorState(
                                title = "Gagal Memuat",
                                message = screenState.message,
                                style = ErrorStateStyle.Generic,
                                primaryAction = {
                                    Sdm3Button(
                                        text = "Coba Lagi",
                                        onClick = { viewModel.loadNotifications() },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }
                    }
                    is NotifUiState.Success -> {
                        val notifItems = vmState.notifications.map { n ->
                            NotifItem(
                                id = n.id,
                                type = n.type,
                                title = n.title.orEmpty(),
                                body = n.message,
                                timestamp = n.createdAt ?: "",
                                isRead = n.isRead == true || n.readAt != null
                            )
                        }
                        val filteredNotifs = if (selectedFilter == 0) notifItems
                        else notifItems.filter { notifMatchesCategory(it.type, filterOptions[selectedFilter]) }

                        if (filteredNotifs.isEmpty()) {
                            EmptyNotifikasiState()
                        } else {
                            val lazyItems = buildList {
                                var lastGroup = ""
                                for (notif in filteredNotifs) {
                                    val (dateGroup, _) = notifGroupAndLabel(notif.timestamp)
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
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        if (!isPreview) viewModel.markAsRead(notif.id)
                                                    },
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
                                                        modifier = Modifier.size(48.dp),
                                                        shape = RoundedCornerShape(14.dp),
                                                        color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                                                        border = BorderStroke(1.5.dp, colorScheme.primary.copy(alpha = 0.2f)),
                                                        shadowElevation = 4.dp
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                iconForType(notif.type),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(28.dp),
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
                                                                text = notifGroupAndLabel(notif.timestamp).second,
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
    }
}

@Composable
private fun ShimmerNotifikasiList() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Sdm3Card(
                modifier = Modifier.fillMaxWidth(),
                padding = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerEffect()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
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

private val bulanSingkatNotif = listOf(
    "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
)

private fun parseInstantOrNull(iso: String): Instant? {
    if (iso.isBlank()) return null
    runCatching { return Instant.parse(iso) }
    // Format tanpa zona ("2026-07-04 10:30:00") -> ubah spasi jadi 'T' dan tambahkan Z.
    val normalized = iso.trim().replace(' ', 'T').let { if (it.endsWith("Z")) it else "${it}Z" }
    return runCatching { Instant.parse(normalized) }.getOrNull()
}

/**
 * Mengubah timestamp ISO menjadi pasangan (grup, label relatif).
 * Grup dipakai untuk header seksi, label untuk teks kecil di kartu.
 */
@Suppress("DEPRECATION")
private fun notifGroupAndLabel(iso: String): Pair<String, String> {
    val instant = parseInstantOrNull(iso)
        ?: return "RIWAYAT" to (iso.substringBefore('T').substringBefore(' '))
    val tz = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val nowDate = now.toLocalDateTime(tz).date
    val notifDate = instant.toLocalDateTime(tz).date
    val daysDiff = (nowDate.toEpochDays() - notifDate.toEpochDays()).toInt()
    val diff = now - instant

    val label = when {
        diff.isNegative() -> "Baru saja"
        diff.inWholeMinutes < 1 -> "Baru saja"
        diff.inWholeMinutes < 60 -> "${diff.inWholeMinutes} mnt lalu"
        daysDiff <= 0 -> "${diff.inWholeHours} jam lalu"
        daysDiff == 1 -> "Kemarin"
        else -> "${notifDate.dayOfMonth} ${bulanSingkatNotif[notifDate.monthNumber - 1]}"
    }
    val group = when {
        daysDiff <= 0 -> "BARU INI"
        daysDiff == 1 -> "KEMARIN"
        daysDiff <= 7 -> "MINGGU INI"
        else -> "RIWAYAT"
    }
    return group to label
}

@Preview
@Composable
private fun NotifikasiScreenPreview() {
    SDM3Theme {
        NotifikasiScreen(onBack = {})
    }
}
