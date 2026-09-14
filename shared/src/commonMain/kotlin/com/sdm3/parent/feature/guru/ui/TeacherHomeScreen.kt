package com.sdm3.parent.feature.guru.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    Sdm3ErrorState(
                        title = "Gagal Memuat Data",
                        message = errorMessage,
                        style = ErrorStateStyle.Generic,
                        modifier = Modifier.align(Alignment.Center),
                        primaryAction = {
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
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                        contentPadding = PaddingValues(vertical = Spacing.lg),
                    ) {
                        item {
                            Text(
                                text = "Halo, ${state.teacherName}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = "Pilih kelas untuk mencatat absensi hari ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProductSchoolTheme.colors.onSurfaceMuted,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
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
        padding = Spacing.xl,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sdm3IconBadge(
                icon = Icons.Outlined.LocationOn,
                size = 60.dp,
                iconSize = 30.dp,
                iconTint = colorScheme.primary,
                backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.35f),
                borderColor = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(Spacing.lg))
            Column(modifier = Modifier.weight(1f)) {
                Text("Absensi Saya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary, letterSpacing = (-0.2).sp)
                Text(
                    "Check-in/out dengan GPS realtime",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "Buka absensi saya",
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp),
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
        padding = Spacing.xl,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sdm3IconBadge(
                icon = Icons.Outlined.Groups,
                size = 60.dp,
                iconSize = 30.dp,
                iconTint = colorScheme.primary,
                backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.35f),
                borderColor = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(Spacing.lg))
            Column(modifier = Modifier.weight(1f)) {
                Text(classroom.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary, letterSpacing = (-0.2).sp)
                Text(
                    "${classroom.studentCount} siswa",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "Buka kelas ${classroom.name}",
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
