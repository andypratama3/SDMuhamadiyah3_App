package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite
import com.sdm3.parent.feature.notifikasi.PengumumanSekolahViewModel
import org.koin.compose.viewmodel.koinViewModel

private val categoryFilters = listOf("Semua", "Umum", "Akademik", "Keuangan", "Kegiatan")

data class AnnouncementItem(
    val title: String,
    val date: String,
    val category: String,
    val imageUrl: String? = null
)

private val announcements = listOf(
    AnnouncementItem("Libur Hari Raya Idul Adha 1447 H", "18 Juni 2026", "Umum"),
    AnnouncementItem("Rapor Sumatif 1 Telah Terbit", "15 Juni 2026", "Akademik"),
    AnnouncementItem("Jadwal Pembayaran SPP Semester Baru", "10 Juni 2026", "Keuangan"),
    AnnouncementItem("Pendaftaran Ekstrakurikuler 2026/2027", "5 Juni 2026", "Kegiatan"),
    AnnouncementItem("Sosialisasi Program Tahfiz Quran", "1 Juni 2026", "Kegiatan"),
    AnnouncementItem("Pemberitahuan Perubahan Seragam Sekolah", "28 Mei 2026", "Umum"),
    AnnouncementItem("Jadwal Ujian Sumatif Semester Genap", "20 Mei 2026", "Akademik"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengumumanSekolahScreen(
    onBack: () -> Unit = {},
    viewModel: PengumumanSekolahViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadArticles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengumuman Sekolah") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Cari")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(categoryFilters.size) { index ->
                    val isSelected = selectedCategory == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = index },
                        label = {
                            Text(
                                categoryFilters[index],
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Secondary,
                            selectedLabelColor = SurfaceWhite,
                            containerColor = SurfaceWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            val filtered = if (selectedCategory == 0) uiState.articles
            else uiState.articles.filter { it.category == categoryFilters[selectedCategory].lowercase() }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(filtered) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        onClick = { /* Detail Pengumuman */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.Top
                        ) {
                                Card(
                                    modifier = Modifier.size(72.dp, 64.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Primary.copy(alpha = 0.08f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(Spacing.sm),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = item.publishedAt?.split("T")?.first()?.split("-")?.last() ?: "-",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = item.publishedAt?.split("T")?.first()?.split("-")?.get(1) ?: "-",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Primary
                                        )
                                    }
                                }
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                val chipColor = when (item.category?.lowercase()) {
                                    "umum" -> Secondary
                                    "akademik" -> Primary
                                    "keuangan" -> StatusWarning
                                    else -> Secondary
                                }
                                StatusChip(text = item.category ?: "Umum", color = chipColor)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
