package com.sdm3.parent.feature.infoanak.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoAnakScreen(
    studentId: String,
    onBack: () -> Unit,
    onQuickNavClick: (SDM3Route) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Profil Siswa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "IDENTITAS RESMI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.secondary.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.2f),
                        radius = size.width
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                item {
                    Sdm3Card(padding = 24.dp) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = RoundedCornerShape(32.dp),
                                color = colorScheme.primary.copy(alpha = 0.05f),
                                border = BorderStroke(2.dp, Color.White)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "AF",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Ahmad Fathan",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = colorScheme.secondary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = " KELAS 4-A • IBNU SINA ",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = colorScheme.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatusChip(text = "NISN: 0012345678", color = colorScheme.primary)
                                StatusChip(text = "AKTIF", color = StatusSuccess)
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Biodata Institusi",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Sdm3Card(padding = 16.dp) {
                        Column {
                            InfoRow("Tempat Lahir", "Samarinda")
                            HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            InfoRow("Tanggal Lahir", "15 Januari 2015")
                            HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            InfoRow("Wali Kelas", "Ibu Siti Rahmawati, S.Pd.")
                            HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            InfoRow("ID Portal", "SDM3-2025-00123")
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Eksplorasi Akademik",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickNavItem(
                            modifier = Modifier.weight(1f),
                            title = "Analitik Nilai",
                            icon = Icons.Outlined.Assessment,
                            color = colorScheme.primary,
                            onClick = { onQuickNavClick(SDM3Route.NilaiRapor(studentId, "ganjil")) }
                        )
                        QuickNavItem(
                            modifier = Modifier.weight(1f),
                            title = "Rapor Digital",
                            icon = Icons.Outlined.Description,
                            color = colorScheme.primary,
                            onClick = { onQuickNavClick(SDM3Route.HalamanRapor(studentId)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickNavItem(
                            modifier = Modifier.weight(1f),
                            title = "Presensi",
                            icon = Icons.Outlined.EventAvailable,
                            color = colorScheme.primary,
                            onClick = { onQuickNavClick(SDM3Route.KehadiranSiswa(studentId)) }
                        )
                        QuickNavItem(
                            modifier = Modifier.weight(1f),
                            title = "Administrasi",
                            icon = Icons.Outlined.Payments,
                            color = colorScheme.primary,
                            onClick = { onQuickNavClick(SDM3Route.PembayaranSpp(studentId)) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.primary.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
    }
}

@Composable
private fun QuickNavItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(
        modifier = modifier.clickable(onClick = onClick),
        padding = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun DetailInfoAnakScreenPreview() {
    SDM3Theme {
        DetailInfoAnakScreen(
            studentId = "",
            onBack = {},
            onQuickNavClick = {}
        )
    }
}
