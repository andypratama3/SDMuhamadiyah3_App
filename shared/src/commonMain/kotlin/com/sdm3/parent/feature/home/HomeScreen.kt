package com.sdm3.parent.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.StatusWarning
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite
import com.sdm3.parent.core.designsystem.theme.TertiaryFixed
import com.sdm3.parent.core.navigation.SDM3Route
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    studentId: String,
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showChildSelector by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        viewModel.loadDashboard(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable { showChildSelector = true }
                    ) {
                        Text(
                            text = "Halo, Orang Tua ${uiState.studentName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SDM3 Parent",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(SDM3Route.Notifikasi) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh(studentId) },
            modifier = Modifier.padding(padding)
        ) {
        if (uiState.isLoading && uiState.isEmpty) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.errorMessage != null && uiState.isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize().padding(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = uiState.errorMessage ?: "Terjadi kesalahan", color = Color.Red)
                Spacer(modifier = Modifier.height(Spacing.md))
                Sdm3Button(text = "Coba Lagi", onClick = { viewModel.refresh(studentId) })
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.md)
            ) {
                Text(
                    text = "Pantau Perkembangan Ananda",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SchoolGreenDark
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                val totalFee = uiState.activeFees.sumOf { it.amount }
                if (totalFee > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = TertiaryFixed)
                    ) {
                        Box {
                            Canvas(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).padding(Spacing.md)) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.2f),
                                    radius = size.minDimension / 1.5f
                                )
                            }
                            Column(modifier = Modifier.padding(Spacing.lg)) {
                                Text(
                                    text = "TOTAL TAGIHAN AKTIF",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = "Rp$totalFee",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                uiState.activeFees.firstOrNull()?.dueDate?.let { dueDate ->
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    Text(
                                        text = "Jatuh tempo terdekat: $dueDate",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                Text(
                    text = "Layanan Sekolah",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    MenuCard(
                        title = "Nilai & Rapor",
                        icon = Icons.Default.Assessment,
                        color = Primary,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                    )
                    MenuCard(
                        title = "Pembayaran SPP",
                        icon = Icons.Default.Payments,
                        color = Secondary,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(SDM3Route.PembayaranSpp(studentId)) }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    MenuCard(
                        title = "Kehadiran",
                        icon = Icons.Default.DateRange,
                        color = StatusWarning,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(SDM3Route.KehadiranSiswa(studentId)) }
                    )
                    MenuCard(
                        title = "Info Anak",
                        icon = Icons.Default.Person,
                        color = SchoolGreenVibrant,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(SDM3Route.DetailInfoAnak(studentId)) }
                    )
                }

                if (uiState.recentGrades.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Text(
                        text = "Nilai Terbaru",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(uiState.recentGrades) { grade ->
                            SubjectScoreCard(mapel = grade.subjectName, nilai = grade.score?.toInt()?.toString() ?: "-")
                        }
                    }
                }
            }
        }
        }
    }

    if (showChildSelector) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showChildSelector = false },
            sheetState = sheetState,
            dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle() }
        ) {
            ChildSelectorContent(
                onChildSelected = { id ->
                    showChildSelector = false
                    navController.navigate(SDM3Route.Main(id)) {
                        popUpTo<SDM3Route.Main> { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun ChildSelectorContent(
    onChildSelected: (String) -> Unit,
    viewModel: com.sdm3.parent.feature.auth.PilihAnakViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.md)
    ) {
        Text(
            text = "Pilih Anak",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        uiState.students.forEach { student ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs)
                    .clickable { onChildSelected(student.id) },
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.firstOrNull()?.toString()?.uppercase() ?: "",
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Kelas ${student.className ?: "-"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}

@Composable
private fun MenuCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubjectScoreCard(
    mapel: String,
    nilai: String
) {
    Sdm3Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mapel,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = nilai,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}
