package com.sdm3.parent.feature.home.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.navigation.SDM3BottomTab
import com.sdm3.parent.feature.home.HomeEffect
import com.sdm3.parent.feature.home.HomeIntent
import com.sdm3.parent.feature.home.HomeViewModel
import com.sdm3.parent.feature.auth.ui.PilihAnakBottomSheet
import com.sdm3.parent.core.security.SecureTokenManager
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data object Empty : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
    data object Success : HomeScreenUiState()
}

@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val isPreview = LocalInspectionMode.current

    val errorMessage = state.errorMessage

    val screenState = remember(state.isLoading, state.isEmpty, errorMessage, isPreview) {
        if (isPreview) HomeScreenUiState.Success
        else when {
            state.isLoading && state.isEmpty -> HomeScreenUiState.Loading
            errorMessage != null -> HomeScreenUiState.Error(errorMessage)
            !state.isLoading && state.isEmpty -> HomeScreenUiState.Empty
            else -> HomeScreenUiState.Success
        }
    }

    LaunchedEffect(studentId) {
        if (!isPreview) {
            viewModel.onIntent(HomeIntent.LoadDashboard(studentId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToLogin -> {
                    navController.navigate(SDM3Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showComingSoon: () -> Unit = {
        scope.launch {
            snackbarHostState.showSnackbar("Fitur ini akan segera hadir")
        }
    }

    // Penukar anak aktif (1 orang tua bisa punya banyak anak).
    val secureTokenManager = if (isPreview) null else koinInject<SecureTokenManager>()
    var showStudentSheet by remember { mutableStateOf(false) }
    val switchStudent: (String) -> Unit = { newId ->
        showStudentSheet = false
        if (newId != state.studentId) {
            secureTokenManager?.saveSelectedStudentId(newId)
            navController.navigate(SDM3Route.Main(newId)) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeHeader(
                navController = navController,
                unreadCount = state.unreadNotificationCount,
                onNotificationClick = { navController.navigate(SDM3Route.Notifikasi) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            // Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width * 1.2f
                    )
                )
            }

            when (screenState) {
                is HomeScreenUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                    ) {
                        item { GreetingShimmer() }
                        item { ShortcutFavoritShimmer() }
                        item { LayananSekolahShimmer() }
                        item { PengumumanShimmer() }
                        item { TabunganSekolahShimmer() }
                    }
                }
                is HomeScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data",
                        message = screenState.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.onIntent(HomeIntent.Refresh(studentId)) }
                            )
                        }
                    )
                }
                is HomeScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Data",
                        message = "Data dashboard belum tersedia.",
                        style = EmptyStateStyle.Neutral
                    )
                }
                is HomeScreenUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                    ) {
                        item {
                            GreetingSection(
                                name = state.studentName.trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "Wali Murid",
                                info = state.className.takeIf { it.isNotBlank() }?.let { com.sdm3.parent.core.util.formatClassName(it) } ?: "",
                                onClick = { navController.navigate(SDM3Route.DetailInfoAnak(studentId)) }
                            )
                        }
                        if (state.students.size > 1) {
                            item {
                                StudentSwitcherBar(
                                    name = state.studentName.ifBlank { "Pilih Siswa" },
                                    className = state.className,
                                    childCount = state.students.size,
                                    onClick = { showStudentSheet = true }
                                )
                            }
                        }
                        item {
                            ShortcutFavoritSection(
                                onSppClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) },
                                onAbsensiClick = { navController.navigate(SDM3Route.KehadiranSiswa(studentId)) },
                                onELibraryClick = showComingSoon
                            )
                        }
                        item {
                            LayananSekolahSection(
                                onEkskulClick = { navController.navigate(SDM3Route.KegiatanProgram(studentId)) },
                                onComingSoonClick = showComingSoon
                            )
                        }
                        item {
                            val latestAnnouncement = state.announcements.firstOrNull()
                            PengumumanSection(
                                title = latestAnnouncement?.title ?: "Belum ada pengumuman baru",
                                time = latestAnnouncement?.publishedAt ?: "-",
                                onClick = {
                                    navController.navigate(SDM3Route.PengumumanSekolah)
                                }
                            )
                        }
                        item {
                            val totalActive = state.activeFees.sumOf { it.amount.toLong() }
                            val formatted = com.sdm3.parent.core.util.formatRupiah(totalActive)
                            TabunganSekolahSection(
                                amount = formatted,
                                hasBills = totalActive > 0L,
                                onBayarClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStudentSheet && state.students.size > 1) {
        PilihAnakBottomSheet(
            students = state.students,
            selectedStudentId = state.studentId,
            onStudentSelected = switchStudent,
            onDismiss = { showStudentSheet = false }
        )
    }
}

@Composable
private fun StudentSwitcherBar(
    name: String,
    className: String,
    childCount: Int,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Sdm3Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            padding = 14.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = com.sdm3.parent.core.util.nameInitials(name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MEMANTAU · $childCount ANAK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = colorScheme.primary.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (className.isNotBlank()) {
                        Text(
                            text = com.sdm3.parent.core.util.formatClassName(className),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colorScheme.primary.copy(alpha = 0.06f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.SwapHoriz,
                            contentDescription = "Ganti anak",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ganti",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    navController: NavHostController,
    unreadCount: Int,
    onNotificationClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 12.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Sdm3Logo(
                size = 36.dp,
                showBackground = false
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SD Muhammadiyah 3 Samarinda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onNotificationClick) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier.size(8.dp),
                            containerColor = colorScheme.error
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun GreetingShimmer() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(18.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
    }
}

@Composable
private fun ShortcutFavoritShimmer() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(140.dp, 160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun LayananSekolahShimmer() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, glassBorderColor())
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(70.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .shimmerEffect()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerEffect()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PengumumanShimmer() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Sdm3Card(
            modifier = Modifier.fillMaxWidth(),
            padding = 20.dp
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
private fun TabunganSekolahShimmer() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun GreetingSection(name: String, info: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "Halo, $name!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = (-1).sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = info,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ShortcutFavoritSection(
    onSppClick: () -> Unit,
    onAbsensiClick: () -> Unit,
    onELibraryClick: () -> Unit
) {
    val statusSuccess = statusSuccessColor()
    Column {
        Text(
            text = "Shortcut Favorit",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                ShortcutCard(
                    title = "SPP & Biaya",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconColor = statusSuccess,
                    onClick = onSppClick
                )
            }
            item {
                ShortcutCard(
                    title = "E-Library",
                    icon = Icons.Outlined.LocalLibrary,
                    iconColor = MaterialTheme.colorScheme.primary,
                    comingSoon = true,
                    onClick = onELibraryClick
                )
            }
            item {
                ShortcutCard(
                    title = "Absensi",
                    icon = Icons.Outlined.QrCodeScanner,
                    iconColor = MaterialTheme.colorScheme.secondary,
                    onClick = onAbsensiClick
                )
            }
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    comingSoon: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(
        modifier = Modifier
            .size(130.dp, 150.dp)
            .clickable(onClick = onClick),
        padding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(Spacing.sm),
                    color = iconColor.copy(alpha = if (comingSoon) 0.05f else 0.08f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (comingSoon) iconColor.copy(alpha = 0.4f) else iconColor,
                            modifier = Modifier.size(Spacing.xl)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (comingSoon) colorScheme.primary.copy(alpha = 0.6f) else colorScheme.primary,
                    lineHeight = 18.sp
                )
            }
            if (comingSoon) {
                Surface(
                    shape = RoundedCornerShape(bottomStart = Spacing.sm),
                    color = colorScheme.secondary,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "SEGERA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.primary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                    )
                }
            }
        }
    }
}

private data class ServiceEntry(
    val label: String,
    val icon: ImageVector,
    val comingSoon: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun LayananSekolahSection(
    onEkskulClick: () -> Unit,
    onComingSoonClick: () -> Unit
) {
    val items = listOf(
        ServiceEntry("EKSKUL", Icons.Outlined.SportsSoccer, false, onEkskulClick),
        ServiceEntry("PERPUS", Icons.Outlined.AutoStories, true, onComingSoonClick),
        ServiceEntry("KANTIN", Icons.Outlined.Restaurant, true, onComingSoonClick),
        ServiceEntry("ALUMNI", Icons.Outlined.Groups, true, onComingSoonClick),
        ServiceEntry("EVENT", Icons.Outlined.CalendarMonth, true, onComingSoonClick),
        ServiceEntry("CS", Icons.Outlined.SupportAgent, true, onComingSoonClick),
        ServiceEntry("BEASISWA", Icons.Outlined.School, true, onComingSoonClick),
        ServiceEntry("KONSELING", Icons.Outlined.Psychology, true, onComingSoonClick),
        ServiceEntry("BUS", Icons.Outlined.DirectionsBus, true, onComingSoonClick),
        ServiceEntry("UKS", Icons.Outlined.MedicalServices, true, onComingSoonClick),
        ServiceEntry("HALL FAME", Icons.Outlined.MilitaryTech, true, onComingSoonClick),
        ServiceEntry("LAINNYA", Icons.Outlined.GridView, true, onComingSoonClick)
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Layanan Sekolah",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Sdm3Card(padding = 4.dp) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                items.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { entry ->
                            ServiceItem(entry.label, entry.icon, entry.comingSoon, entry.onClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceItem(
    label: String,
    icon: ImageVector,
    comingSoon: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick)
    ) {
        Box {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = colorScheme.primaryContainer.copy(alpha = if (comingSoon) 0.15f else 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (comingSoon) colorScheme.primary.copy(alpha = 0.4f) else colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (comingSoon) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colorScheme.secondary,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-4).dp)
                ) {
                    Text(
                        text = "SEGERA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.primary,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (comingSoon) colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun PengumumanSection(
    title: String,
    time: String,
    onClick: () -> Unit
) {
    val successColor = statusSuccessColor()
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Sdm3Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            padding = 20.dp
        ) {
            Column {
                Surface(
                    color = successColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "PENGUMUMAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = successColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TabunganSekolahSection(amount: String, hasBills: Boolean, onBayarClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val heroContent = heroContentColor()
    val statusSuccess = statusSuccessColor()
    val glowColor = colorScheme.surfaceTint.copy(alpha = 0.4f)

    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Spacing.xxl),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).alpha(0.15f)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor, Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.2f),
                            radius = size.width
                        )
                    )
                }

                Column(modifier = Modifier.padding(Spacing.xl)) {
                    Text(
                        text = if (hasBills) "Total Tagihan Aktif" else "Tagihan Sekolah",
                        style = MaterialTheme.typography.titleMedium,
                        color = heroContent.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = if (hasBills) amount else "Lunas",
                        style = MaterialTheme.typography.displayMedium,
                        color = heroContent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Sdm3Button(
                        text = if (hasBills) "Bayar" else "Riwayat",
                        onClick = onBayarClick,
                        containerColor = statusSuccess,
                        contentColor = colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth(0.4f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    SDM3Theme {
        HomeScreen(studentId = "", navController = rememberNavController())
    }
}
