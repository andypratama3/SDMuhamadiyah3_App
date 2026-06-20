package com.sdm3.parent.feature.rapor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.Error
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Outline
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.core.designsystem.theme.SurfaceContainer
import com.sdm3.parent.core.designsystem.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiQrRaporScreen(
    raporId: String,
    viewModel: VerifikasiQrRaporViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(raporId) {
        viewModel.init(raporId)
    }

    val resultAlpha by animateFloatAsState(
        targetValue = if (state.showResult) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "resultAlpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔍 Verifikasi QR Rapor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 2.dp,
                                color = Outline,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = "Arahkan kamera ke QR Code",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Text(
                text = "Atau masukkan data QR secara manual",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            OutlinedTextField(
                value = state.qrInput,
                onValueChange = { viewModel.updateQrInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Data QR Code") },
                placeholder = { Text("Tempel data QR di sini...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Outline
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true
            )

            Button(
                onClick = { viewModel.verify() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading && state.qrInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
            ) {
                if (state.isLoading) {
                    Text("⏳")
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text("Memverifikasi...", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("🔍")
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text("Verifikasi", fontWeight = FontWeight.SemiBold)
                }
            }

            AnimatedVisibility(
                visible = state.showResult,
                enter = fadeIn(animationSpec = tween(400)) + scaleIn(animationSpec = tween(400))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(resultAlpha),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    state.errorMessage?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("❌", modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Error
                                )
                            }
                        }
                    }

                    val result = state.verifyResult
                    if (result != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.lg)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (result.valid) "✅" else "❌",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.md))
                                    Column {
                                        Text(
                                            text = if (result.valid) "Terverifikasi" else "Tidak Valid",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (result.valid) StatusSuccess else Error
                                        )
                                        Text(
                                            text = result.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(Spacing.md))

                                Text(
                                    text = "Detail Siswa",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                result.studentName?.let {
                                    VerifInfoRow(label = "Nama", value = it)
                                }
                                result.nisn?.let {
                                    VerifInfoRow(label = "NISN", value = it)
                                }
                                VerifInfoRow(
                                    label = "Status",
                                    value = if (result.valid) "Sah ✅" else "Tidak Sah ❌"
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.reset() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                        ) {
                            Text("🔄")
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Verifikasi Ulang")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
