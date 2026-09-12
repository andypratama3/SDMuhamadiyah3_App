package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.ProductSchoolTheme
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.feature.auth.PilihAnakViewModel
import org.koin.compose.viewmodel.koinViewModel

private val PremiumEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihAnakScreen(
    onChildSelected: (String) -> Unit,
    viewModel: PilihAnakViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val uiState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.auth.PilihAnakUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }

    ScreenScaffold(
        title = "Pilih Data Anak",
        subtitle = "AKADEMIK · SDM3",
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScreenGlowBackground()

            when {
                uiState.isLoading == true -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 3.dp
                    )
                }

                uiState.errorMessage != null -> {
                    val errorMsg = uiState.errorMessage ?: ""
                    Sdm3ErrorState(
                        message = errorMsg,
                        primaryAction = {
                            Sdm3Button(
                                text = "Coba Lagi",
                                onClick = { viewModel.loadStudents() },
                                modifier = Modifier.widthIn(min = 200.dp),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        }
                    )
                }

                else -> {
                    val students = uiState.students

                    if (students.isEmpty()) {
                        Sdm3EmptyState(
                            title = "Belum Ada Data Anak",
                            message = "Data anak tidak ditemukan atau belum ditambahkan ke akun Anda. Silakan hubungi admin sekolah.",
                            icon = Icons.Outlined.Face
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = Spacing.xl,
                                end = Spacing.xl,
                                top = Spacing.md,
                                bottom = Spacing.xxxl
                            ),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(bottom = Spacing.lg)
                                ) {
                                    Text(
                                        text = "Silakan pilih data anak untuk melanjutkan.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 24.sp
                                    )
                                }
                            }

                            itemsIndexed(students, key = { _, student -> student.id }) { index, student ->
                                var isVisible by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) {
                                    isVisible = true
                                }

                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(600, delayMillis = index * 80)) +
                                            slideInVertically(
                                                initialOffsetY = { 30 },
                                                animationSpec = tween(600, delayMillis = index * 80, easing = PremiumEasing)
                                            )
                                ) {
                                    StudentItem(
                                        student = student,
                                        isSelected = uiState.selectedStudentId == student.id,
                                        onClick = { viewModel.selectStudent(student.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Modern Island Button Architecture
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = ProductSchoolTheme.colors.liquidGlassSurface,
                    shadowElevation = 12.dp,
                    tonalElevation = 4.dp,
                    border = BorderStroke(1.5.dp, ProductSchoolTheme.colors.liquidGlassBorder)
                ) {
                    Box(modifier = Modifier.padding(Spacing.xs)) {
                        Sdm3Button(
                            text = "Lanjutkan Ke Dashboard",
                            onClick = {
                                uiState.selectedStudentId?.let { onChildSelected(it) }
                            },
                            enabled = uiState.selectedStudentId != null && uiState.isLoading == false,
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentItem(
    student: StudentDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val glassSurface = ProductSchoolTheme.colors.liquidGlassSurface
    val glassBorder = ProductSchoolTheme.colors.liquidGlassBorder

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else glassBorder,
        animationSpec = tween(durationMillis = 400, easing = PremiumEasing),
        label = "borderColor"
    )

    // Modern Glass Card Architecture
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                else glassSurface.copy(alpha = 0.5f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(1.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(19.dp),
            color = if (isSelected) glassSurface.copy(alpha = 0.95f) else glassSurface,
            tonalElevation = if (isSelected) 4.dp else 0.dp,
            shadowElevation = if (isSelected) 4.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(Spacing.md)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Container with Modern Border
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (student.photo != null) {
                        AsyncImage(
                            model = student.photo,
                            contentDescription = "Foto ${student.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = student.name.firstOrNull()?.uppercase() ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NISN: ${student.nisn ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                        letterSpacing = 0.2.sp
                    )
                    student.className?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Sdm3CategoryBadge(text = it)
                    }
                }

                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut()
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Terpilih",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PilihAnakScreenPreview() {
    SDM3Theme {
        PilihAnakScreen(onChildSelected = {})
    }
}
