package com.sdm3.parent.feature.rapor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3OutlinedButton
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewRaporPdfScreen(
    raporId: String,
    downloadUrl: String,
    viewModel: PreviewRaporPdfViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(raporId, downloadUrl) { viewModel.init(raporId, downloadUrl) }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorScheme.onSurface)
                        Spacer(Modifier.width(8.dp))
                        Text("Preview Rapor PDF", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
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
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(40.dp), tint = colorScheme.primary)
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(state.fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Ukuran: ${state.fileSize}", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text("Informasi Dokumen", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    InfoRow("ID Rapor", state.raporId)
                    InfoRow("Status", "Tersedia")
                    InfoRow("Format", "PDF")
                }
            }

            state.errorMessage?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = colorScheme.errorContainer
                ) {
                    Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(error, style = MaterialTheme.typography.bodySmall, color = colorScheme.onErrorContainer)
                    }
                }
            }

            AnimatedVisibility(visible = state.isDownloading) {
                Column {
                    LinearProgressIndicator(
                        progress = { state.downloadProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text("Mengunduh...", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            AnimatedVisibility(visible = state.isDownloaded) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = StatusSuccess.copy(alpha = 0.1f)
                ) {
                    Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp), tint = StatusSuccess)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text("PDF berhasil diunduh", style = MaterialTheme.typography.bodyMedium, color = StatusSuccess)
                    }
                }
            }

            Sdm3Button(
                text = "Unduh PDF",
                onClick = { viewModel.download() },
                enabled = !state.isDownloading && !state.isDownloaded,
                icon = Icons.Default.FileDownload,
                containerColor = colorScheme.secondary
            )

            Sdm3OutlinedButton(text = "Bagikan", onClick = { }, icon = Icons.Default.Link)
            Sdm3OutlinedButton(text = "Buka di Browser", onClick = { }, icon = Icons.Default.Language)

            Spacer(modifier = Modifier.height(Spacing.lg))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                color = colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(24.dp), tint = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = if (state.isDownloaded) "Dokumen siap dibuka" else "Tekan tombol Unduh untuk memulai",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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
