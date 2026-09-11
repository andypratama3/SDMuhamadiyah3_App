package com.sdm3.parent.feature.guru.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing
import androidx.compose.ui.unit.dp
import com.sdm3.parent.data.remote.dto.TeacherClassroomDto
import com.sdm3.parent.feature.guru.TeacherHomeViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.school_name
import sdmuhammadiyah3samarinda.shared.generated.resources.teacher_panel_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onOpenAbsensi: (String, String) -> Unit,
    onOpenAbsensiSaya: () -> Unit,
    onLogout: () -> Unit,
    onBackToParent: (() -> Unit)? = null,
    viewModel: TeacherHomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) { viewModel.load() }

    ScreenScaffold(
        title = stringResource(Res.string.school_name),
        subtitle = stringResource(Res.string.teacher_panel_title),
        actions = {
            if (onBackToParent != null) {
                IconButton(onClick = onBackToParent) {
                    Icon(Icons.Outlined.Home, contentDescription = "Kembali ke beranda")
                }
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Keluar")
            }
        }
    ) { padding ->
        val errorMessage = state.errorMessage

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg),
        ) {
            ScreenGlowBackground()

            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Sdm3EmptyState(
                        title = "Gagal Memuat",
                        message = errorMessage,
                        modifier = Modifier.align(Alignment.Center),
                        action = {
                            Sdm3Button(text = "Coba Lagi", onClick = { viewModel.load() })
                        },
                    )
                }
                state.isEmpty -> {
                    Sdm3EmptyState(
                        title = "Belum Ada Kelas",
                        message = "Kelas yang Anda ampu belum tersedia untuk absensi.",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(vertical = Spacing.md),
                    ) {
                        item {
                            Text(
                                text = "Halo, ${state.teacherName}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "Pilih kelas untuk mencatat absensi hari ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurface.copy(alpha = 0.7f),
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                        item {
                            TeacherAbsensiSayaCard(onClick = onOpenAbsensiSaya)
                        }
                        items(state.classrooms, key = { it.id }) { classroom ->
                            TeacherClassroomCard(
                                classroom = classroom,
                                onClick = { onOpenAbsensi(classroom.id, classroom.name) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherAbsensiSayaCard(onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = Spacing.lg,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sdm3IconBadge(
                icon = Icons.Outlined.LocationOn,
                size = 56.dp,
                iconSize = 28.dp,
                iconTint = colorScheme.secondary,
                backgroundColor = colorScheme.secondaryContainer.copy(alpha = 0.3f),
                borderColor = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text("Absensi Saya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Text(
                    "Check-in/out dengan GPS realtime",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "Buka absensi saya",
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun TeacherClassroomCard(
    classroom: TeacherClassroomDto,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = Spacing.lg,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sdm3IconBadge(
                icon = Icons.Outlined.Groups,
                size = 56.dp,
                iconSize = 28.dp,
                iconTint = colorScheme.primary,
                backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.3f),
                borderColor = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(classroom.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Text(
                    "${classroom.studentCount} siswa",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "Buka kelas ${classroom.name}",
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
