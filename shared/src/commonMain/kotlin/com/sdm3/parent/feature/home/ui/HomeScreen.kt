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
import org.jetbrains.compose.resources.stringResource
import sdmuhammadiyah3samarinda.shared.generated.resources.*
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.navigation.SDM3BottomTab
import com.sdm3.parent.feature.home.HomeEffect
import com.sdm3.parent.feature.home.HomeIntent
import com.sdm3.parent.feature.home.HomeViewModel
import com.sdm3.parent.feature.home.HomeUiState
import com.sdm3.parent.feature.auth.ui.PilihAnakBottomSheet
import com.sdm3.parent.core.security.SecureTokenManager
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController,
    viewModel: HomeViewModel? = if (LocalInspectionMode.current) null else koinViewModel(),
) {
    val isPreview = LocalInspectionMode.current
    val state by if (viewModel != null) {
        viewModel.uiState.collectAsState()
    } else {
        remember { mutableStateOf(HomeUiState()) }
    }
    val colorScheme = MaterialTheme.colorScheme

    val errorMessage = state.errorMessage

    val screenState = remember(state.isLoading, state.isEmpty, errorMessage, isPreview) {
        if (isPreview) ScreenUiState.Success
        else resolveScreenState(state.isLoading, state.isEmpty, errorMessage)
    }

    LaunchedEffect(studentId) {
        if (!isPreview) {
            viewModel?.onIntent(HomeIntent.LoadDashboard(studentId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel?.effect?.collect { effect ->
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
    var showStudentSheet by remember { mutableStateOf(value = false) }
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
                unreadCount = state.unreadNotificationCount
            ) {
                navController.navigate(SDM3Route.Notifikasi)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            // High-performance Atmospheric Glow
            AtmosphericGlow(
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.TopEnd,
                color = colorScheme.primaryContainer,
                alpha = 0.15f,
                animate = true
            )

            when (screenState) {
                is ScreenUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = Spacing.bottomNavSafeArea),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                    ) {
                        item { GreetingShimmer() }
                        item { ShortcutFavoritShimmer() }
                        item { LayananSekolahShimmer() }
                        item { PengumumanShimmer() }
                        item { TabunganSekolahShimmer() }
                    }
                }
                is ScreenUiState.Error -> {
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data",
                        message = screenState.message,
                        style = ErrorStateStyle.Generic,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel?.onIntent(HomeIntent.Refresh(studentId)) }
                            )
                        }
                    )
                }
                is ScreenUiState.Empty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Data",
                        message = "Data dashboard belum tersedia.",
                        style = EmptyStateStyle.Neutral
                    )
                }
                is ScreenUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = Spacing.bottomNavSafeArea), // floating nav safe area
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                    ) {
                        item {
                            GreetingSection(
                                name = state.studentName.trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "Pengguna",
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

    if (showStudentSheet && (state.students.size > 1)) {
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
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
                        color = ProductSchoolTheme.colors.onSurfaceMuted,
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
                            color = ProductSchoolTheme.colors.onSurfaceMuted,
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
    unreadCount: Int,
    onNotificationClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = Spacing.lg, end = 12.dp, top = 24.dp, bottom = 12.dp),
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
                text = stringResource(Res.string.school_name),
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
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
                .padding(horizontal = Spacing.lg),
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
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
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
            border = BorderStroke(1.dp, liquidGlassBorderColor())
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
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.lg)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "Halo, $name!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(contentAlignment = Alignment.CenterStart) {
            if (!info.isEmpty()) {
                LocalizedGlow(
                    modifier = Modifier.size(80.dp, 30.dp),
                    color = colorScheme.secondary,
                    alpha = 0.1f
                )
            }
            Surface(
                color = colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
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
            .size(140.dp, 160.dp)
            .clickable(onClick = onClick),
        padding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!comingSoon) {
                        LocalizedGlow(
                            modifier = Modifier.size(80.dp),
                            color = iconColor,
                            alpha = 0.15f
                        )
                    }
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = iconColor.copy(alpha = if (comingSoon) 0.08f else 0.12f),
                        shadowElevation = if (comingSoon) 0.dp else 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                icon,
                                contentDescription = title,
                                tint = if (comingSoon) iconColor.copy(alpha = 0.4f) else iconColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (comingSoon) ProductSchoolTheme.colors.onSurfaceFaint else colorScheme.primary,
                    lineHeight = 20.sp
                )
            }
            if (comingSoon) {
                Surface(
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    color = colorScheme.secondary,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "SEGERA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.onSecondary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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

    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
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
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!comingSoon) {
                LocalizedGlow(
                    modifier = Modifier.size(64.dp),
                    color = colorScheme.primary,
                    alpha = 0.12f
                )
            }
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.primaryContainer.copy(alpha = if (comingSoon) 0.2f else 0.4f),
                shadowElevation = if (comingSoon) 0.dp else 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = if (comingSoon) colorScheme.primary.copy(alpha = 0.4f) else colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            if (comingSoon) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colorScheme.secondary,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 2.dp, top = 2.dp)
                ) {
                    Text(
                        text = "SEGERA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.onSecondary,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (comingSoon) ProductSchoolTheme.colors.onSurfaceFaint else colorScheme.onSurfaceVariant,
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
    val colorScheme = MaterialTheme.colorScheme
    val infoColor = statusInfoColor()
    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
        Sdm3Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            padding = 20.dp
        ) {
            Row {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = infoColor.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Campaign,
                            contentDescription = "Pengumuman",
                            tint = infoColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PENGUMUMAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = infoColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.AccessTime,
                            contentDescription = "Waktu",
                            modifier = Modifier.size(14.dp),
                            tint = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
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
    val glowColor = colorScheme.secondary.copy(alpha = 0.3f)

    Column(modifier = Modifier.padding(horizontal = Spacing.lg)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.fillMaxWidth().height(180.dp).alpha(0.4f)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.15f),
                            radius = size.width * 1.2f
                        )
                    )
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (hasBills) "Total Tagihan Aktif" else "Tagihan Sekolah",
                            style = MaterialTheme.typography.titleMedium,
                            color = heroContent.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            color = heroContent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.AccountBalanceWallet,
                                contentDescription = "Tagihan",
                                tint = heroContent,
                                modifier = Modifier.padding(8.dp).size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (hasBills) amount else "Lunas",
                        style = MaterialTheme.typography.displaySmall,
                        color = heroContent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Sdm3Button(
                        text = if (hasBills) "Bayar Sekarang" else "Lihat Riwayat",
                        onClick = onBayarClick,
                        containerColor = statusSuccess,
                        contentColor = colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth()
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
        HomeScreen(
            studentId = "",
            navController = rememberNavController(),
            viewModel = null
        )
    }
}
