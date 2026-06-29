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
import com.sdm3.parent.feature.home.HomeIntent
import com.sdm3.parent.feature.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val isPreview = LocalInspectionMode.current
    
    LaunchedEffect(studentId) {
        if (!isPreview) {
            viewModel.onIntent(HomeIntent.LoadDashboard(studentId))
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            HomeHeader(
                navController = navController,
                onNotificationClick = { navController.navigate(SDM3Route.Notifikasi) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl)
            ) {
                // 1. Greeting Section
                item {
                    GreetingSection(
                        name = if (state.isLoading) "..." else state.studentName.split(" ").firstOrNull() ?: "Wali Murid",
                        info = if (state.isLoading) "Memuat data..." else state.className
                    )
                }

                // 2. Shortcut Favorit (Horizontal Cards)
                item {
                    ShortcutFavoritSection(
                        onSppClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) },
                        onAbsensiClick = { navController.navigate(SDM3Route.KehadiranSiswa(studentId)) }
                    )
                }

                // 3. Layanan Sekolah (Grid 4x3)
                item {
                    LayananSekolahSection(
                        onEkskulClick = { navController.navigate(SDM3Route.KegiatanProgram(studentId)) }
                    )
                }

                // 4. Pengumuman Card
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

                // 5. Tabungan Sekolah (Dark Card)
                item {
                    TabunganSekolahSection(amount = "Rp 1.250.000")
                }
            }

            if (state.isLoading && state.isEmpty) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            
            state.errorMessage?.let { error ->
                NetworkErrorDialog(
                    message = error,
                    onRetry = { viewModel.onIntent(HomeIntent.Refresh(studentId)) },
                    onDismiss = { /* Handle dismiss if needed */ }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    navController: NavHostController,
    onNotificationClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Institutional Identity (ProductSchool Logo)
            Sdm3Logo(
                size = 40.dp,
                showBackground = false
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SD Muhammadiyah 3 Samarinda",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = (-0.5).sp
            )
        }

        IconButton(onClick = onNotificationClick) {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.size(8.dp),
                        containerColor = colorScheme.error
                    )
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
private fun GreetingSection(name: String, info: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Halo, $name!",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = (-1).sp
        )
        Text(
            text = info,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ShortcutFavoritSection(
    onSppClick: () -> Unit,
    onAbsensiClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shortcut Favorit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelLarge,
                color = StatusSuccess,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ShortcutCard(
                    title = "SPP & Biaya",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconColor = StatusSuccess,
                    onClick = onSppClick
                )
            }
            item {
                ShortcutCard(
                    title = "E-Library",
                    icon = Icons.Outlined.LocalLibrary,
                    iconColor = MaterialTheme.colorScheme.primary,
                    onClick = { /* TODO */ }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(140.dp, 160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LayananSekolahSection(
    onEkskulClick: () -> Unit
) {
    val items = listOf(
        Triple("PERPUS", Icons.Outlined.AutoStories, {}),
        Triple("KANTIN", Icons.Outlined.Restaurant, {}),
        Triple("EKSKUL", Icons.Outlined.SportsSoccer, onEkskulClick),
        Triple("ALUMNI", Icons.Outlined.Groups, {}),
        Triple("EVENT", Icons.Outlined.CalendarMonth, {}),
        Triple("CS", Icons.Outlined.SupportAgent, {}),
        Triple("BEASISWA", Icons.Outlined.School, {}),
        Triple("KONSELING", Icons.Outlined.Psychology, {}),
        Triple("BUS", Icons.Outlined.DirectionsBus, {}),
        Triple("UKS", Icons.Outlined.MedicalServices, {}),
        Triple("HALL FAME", Icons.Outlined.MilitaryTech, {}),
        Triple("LAINNYA", Icons.Outlined.GridView, {})
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Layanan Sekolah",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                items.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { (label, icon, onClick) ->
                            ServiceItem(label, icon, onClick)
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Sdm3Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            padding = 20.dp
        ) {
            Column {
                Surface(
                    color = StatusSuccess.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "PENGUMUMAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusSuccess,
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
private fun TabunganSekolahSection(amount: String) {
    val colorScheme = MaterialTheme.colorScheme
    val glowColor = colorScheme.surfaceTint.copy(alpha = 0.4f)
    
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Background Glow for Dark Card
                Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).alpha(0.15f)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor, Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.2f),
                            radius = size.width
                        )
                    )
                }
                
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Tabungan Sekolah",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(0.35f).height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                    ) {
                        Text("Top Up", fontWeight = FontWeight.Bold, color = Color.White)
                    }
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
