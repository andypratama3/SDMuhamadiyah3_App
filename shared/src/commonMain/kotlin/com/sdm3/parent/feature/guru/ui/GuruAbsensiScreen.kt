package com.sdm3.parent.feature.guru.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.ScreenGlowBackground
import com.sdm3.parent.core.designsystem.component.ScreenScaffold
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.statusDangerColor
import com.sdm3.parent.core.designsystem.theme.statusInfoColor
import com.sdm3.parent.core.designsystem.theme.statusSuccessColor
import com.sdm3.parent.core.designsystem.theme.statusWarningColor
import com.sdm3.parent.domain.model.AttendanceStatus
import com.sdm3.parent.feature.guru.GuruAbsensiViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun AttendanceChip(
    label: String,
    selected: Boolean,
    status: AttendanceStatus,
    onClick: () -> Unit,
) {
    val color = when (status) {
        AttendanceStatus.HADIR -> statusSuccessColor()
        AttendanceStatus.IZIN -> statusInfoColor()
        AttendanceStatus.SAKIT -> statusWarningColor()
        AttendanceStatus.PULANG -> statusInfoColor()
        AttendanceStatus.ALPA -> statusDangerColor()
    }
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        shape = RoundedCornerShape(999.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.18f),
            selectedLabelColor = color,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruAbsensiScreen(
    classroomId: String,
    classroomName: String,
    onBack: () -> Unit,
    viewModel: GuruAbsensiViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val hasChanges = remember(state.selectedStatuses, state.originalStatuses) {
        state.selectedStatuses.any { (studentId, status) ->
            state.originalStatuses[studentId] != status
        }
    }

    LaunchedEffect(classroomId, classroomName) {
        viewModel.init(classroomId, classroomName)
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Absensi berhasil disimpan")
        }
    }

    ScreenScaffold(
        title = classroomName,
        subtitle = state.date,
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Sdm3Button(
                    text = if (state.isSaving) "Menyimpan..." else "Simpan Absensi",
                    onClick = { viewModel.save() },
                    enabled = !state.isSaving && hasChanges,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(Spacing.lg),
                )
            }
        },
    ) { padding ->
        val errorMessage = state.errorMessage

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenGlowBackground(color = colorScheme.primaryContainer)

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Sdm3EmptyState(
                        title = "Gagal Memuat",
                        message = errorMessage,
                        modifier = Modifier.fillMaxSize().padding(padding),
                        action = {
                            Sdm3Button(text = "Coba Lagi", onClick = { viewModel.loadRoster() })
                        },
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        contentPadding = PaddingValues(vertical = Spacing.md),
                    ) {
                        items(state.students, key = { it.studentId }) { student ->
                            val selected = state.selectedStatuses[student.studentId]
                                ?: state.originalStatuses[student.studentId]
                            Sdm3Card(padding = Spacing.md) {
                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                    Text(student.name, fontWeight = FontWeight.Bold)
                                    if (!student.nis.isNullOrBlank()) {
                                        Text(
                                            "NIS ${student.nis}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = colorScheme.onSurface.copy(alpha = 0.6f),
                                        )
                                    }
                                    if (selected == null) {
                                        Text(
                                            "Belum diabsen",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = colorScheme.onSurface.copy(alpha = 0.5f),
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                        AttendanceStatus.teacherSelectable.forEach { option ->
                                            AttendanceChip(
                                                label = option.label,
                                                selected = selected?.equals(option.apiValue, ignoreCase = true) == true,
                                                status = option,
                                                onClick = { viewModel.setStatus(student.studentId, option.apiValue) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
