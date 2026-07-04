package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.component.EmptyStateStyle
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.notifikasi.PengumumanSekolahUiState
import com.sdm3.parent.feature.notifikasi.PengumumanSekolahViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed class PengumumanUiState {
    data object Loading : PengumumanUiState()
    data object Empty : PengumumanUiState()
    data class Error(val message: String) : PengumumanUiState()
    data object Success : PengumumanUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengumumanSekolahScreen(
    onBack: () -> Unit,
    onDetailClick: (String) -> Unit,
    viewModel: PengumumanSekolahViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    var selectedCategory by remember { mutableIntStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    var startAnimation by remember { mutableStateOf(isPreview) }

    val uiState by if (isPreview) {
        remember { mutableStateOf(PengumumanSekolahUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    val errorMessage = uiState.errorMessage

    val screenState = remember(uiState.isLoading, uiState.isEmpty, errorMessage, isPreview) {
        if (isPreview) PengumumanUiState.Success
        else when {
            uiState.isLoading && uiState.isEmpty -> PengumumanUiState.Loading
            errorMessage != null -> PengumumanUiState.Error(errorMessage)
            !uiState.isLoading && uiState.isEmpty -> PengumumanUiState.Empty
            else -> PengumumanUiState.Success
        }
    }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            startAnimation = true
            viewModel.loadArticles()
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
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
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

            AnimatedVisibility(visible = isSearchActive) {
                Sdm3TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "CARI PENGUMUMAN",
                    placeholder = "Ketik judul pengumuman...",
                    leadingIcon = Icons.Outlined.Search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                )
            }

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

            when (screenState) {
                is PengumumanUiState.Loading -> {
                    ShimmerPengumumanList()
                }
                is PengumumanUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3EmptyState(
                            title = "Belum Ada Pengumuman",
                            message = "Belum terdapat pengumuman sekolah saat ini.",
                            style = EmptyStateStyle.Neutral
                        )
                    }
                }
                is PengumumanUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3ErrorState(
                            title = "Gagal Memuat",
                            message = screenState.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.refresh() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }
                }
                    is PengumumanUiState.Success -> {
                        val categoryLabel = categoryFilters.getOrElse(selectedCategory) { "Semua" }
                        val articles = uiState.articles
                            .filter { article ->
                                val matchesCategory = selectedCategory == 0 ||
                                    article.category?.equals(categoryLabel, ignoreCase = true) == true
                                val matchesSearch = searchQuery.isBlank() ||
                                    article.title.contains(searchQuery, ignoreCase = true) ||
                                    article.excerpt?.contains(searchQuery, ignoreCase = true) == true
                                matchesCategory && matchesSearch
                            }

                        if (articles.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Sdm3EmptyState(
                                    title = "Tidak Ditemukan",
                                    message = "Tidak ada pengumuman yang cocok dengan pencarian Anda.",
                                    style = EmptyStateStyle.Neutral
                                )
                            }
                        } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                            contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md)
                        ) {
                            items(articles) { article ->
                                val (badgeDay, badgeMonth) = dateBadgeParts(article.publishedAt ?: "")
                                Sdm3Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onDetailClick(article.id) }
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
                                                    text = badgeDay,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorScheme.primary
                                                )
                                                Text(
                                                    text = badgeMonth,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorScheme.primary
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(Spacing.md))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = article.title,
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
                                                    text = article.category?.uppercase() ?: "UMUM",
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
        }
    }
}

@Composable
private fun ShimmerPengumumanList() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(5) {
            Sdm3Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp, 60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

private val bulanBadge = listOf(
    "JAN", "FEB", "MAR", "APR", "MEI", "JUN", "JUL", "AGU", "SEP", "OKT", "NOV", "DES"
)

/** Ambil (tanggal, bulan singkat) dari string tanggal ISO untuk badge. */
private fun dateBadgeParts(iso: String): Pair<String, String> {
    if (iso.isBlank()) return "--" to ""
    val datePart = iso.substringBefore('T').substringBefore(' ')
    val parts = datePart.split('-')
    if (parts.size >= 3) {
        val month = parts[1].toIntOrNull()
        val day = parts[2].take(2).toIntOrNull()
        if (month != null && day != null && month in 1..12) {
            return day.toString() to bulanBadge[month - 1]
        }
    }
    val sp = iso.split(" ")
    return (sp.getOrNull(0) ?: "--") to (sp.getOrNull(1)?.uppercase() ?: "")
}

@Preview
@Composable
private fun PengumumanSekolahScreenPreview() {
    SDM3Theme {
        PengumumanSekolahScreen(onBack = {}, onDetailClick = {})
    }
}
