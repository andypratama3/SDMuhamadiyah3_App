package com.sdm3.parent.feature.rapor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3OutlinedButton
import com.sdm3.parent.core.designsystem.component.Sdm3TextField
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusDanger
import com.sdm3.parent.core.designsystem.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiQrRaporScreen(
    raporId: String,
    viewModel: VerifikasiQrRaporViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(raporId) { viewModel.init(raporId) }

    val resultAlpha by animateFloatAsState(
        targetValue = if (state.showResult) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "resultAlpha"
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorScheme.onSurface)
                        Spacer(Modifier.width(8.dp))
                        Text("Verifikasi QR Rapor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, colorScheme.outline, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text("Arahkan kamera ke QR Code", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Text("Atau masukkan data QR secara manual", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)

            Sdm3TextField(value = state.qrInput, onValueChange = { viewModel.updateQrInput(it) }, label = "Data QR Code", leadingIcon = Icons.Outlined.QrCode2)

            Sdm3Button(
                text = "Verifikasi",
                onClick = { viewModel.verify() },
                isLoading = state.isLoading,
                enabled = !state.isLoading && state.qrInput.isNotBlank(),
                containerColor = colorScheme.secondary,
                icon = Icons.Default.Search
            )

            AnimatedVisibility(
                visible = state.showResult,
                enter = fadeIn(animationSpec = tween(400)) + scaleIn(animationSpec = tween(400))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().alpha(resultAlpha),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    state.errorMessage?.let { error ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CardShape,
                            color = StatusDanger.copy(alpha = 0.05f),
                            tonalElevation = 0.dp
                        ) {
                            Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(24.dp), tint = StatusDanger)
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(error, style = MaterialTheme.typography.bodySmall, color = StatusDanger)
                            }
                        }
                    }

                    val result = state.verifyResult
                    if (result != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CardShape,
                            color = colorScheme.surface,
                            tonalElevation = 2.dp,
                            shadowElevation = 4.dp
                        ) {
                            Column(modifier = Modifier.padding(Spacing.lg)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (result.valid) Icons.Default.CheckCircle else Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = if (result.valid) StatusSuccess else StatusDanger
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.md))
                                    Column {
                                        Text(
                                            text = if (result.valid) "Terverifikasi" else "Tidak Valid",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (result.valid) StatusSuccess else StatusDanger
                                        )
                                        Text(result.message, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(Spacing.md))
                                Text("Detail Siswa", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(Spacing.sm))

                                result.studentName?.let { VerifInfoRow("Nama", it) }
                                result.nisn?.let { VerifInfoRow("NISN", it) }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Status", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (result.valid) Icons.Default.CheckCircle else Icons.Default.Clear,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (result.valid) StatusSuccess else StatusDanger
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (result.valid) "Sah" else "Tidak Sah",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Sdm3OutlinedButton(text = "Verifikasi Ulang", onClick = { viewModel.reset() }, icon = Icons.Default.Refresh)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun VerifInfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

private val CardShape = com.sdm3.parent.core.designsystem.theme.CardShape
