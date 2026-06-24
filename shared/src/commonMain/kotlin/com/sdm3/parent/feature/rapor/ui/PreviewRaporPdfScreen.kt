package com.sdm3.parent.feature.rapor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.designsystem.component.Sdm3ElevatedCard
import com.sdm3.parent.core.designsystem.theme.CardShape
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.feature.rapor.PreviewRaporPdfUiState
import com.sdm3.parent.feature.rapor.PreviewRaporPdfViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewRaporPdfScreen(
    raporId: String,
    downloadUrl: String,
    viewModel: PreviewRaporPdfViewModel? = if (LocalInspectionMode.current) null else koinInject(),
    onBack: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme = MaterialTheme.colorScheme
    val state by if (isPreview) {
        remember { mutableStateOf(PreviewRaporPdfUiState()) }
    } else {
        val vm = viewModel ?: return Text("")
        vm.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(raporId, downloadUrl) { viewModel?.init(raporId, downloadUrl) }
    }

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
            Sdm3ElevatedCard(padding = Spacing.md) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(40.dp), tint = colorScheme.primary)
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(state.fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Ukuran: ${state.fileSize}", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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

@Preview
@Composable
private fun PreviewRaporPdfScreenPreview() {
    SDM3Theme {
        PreviewRaporPdfScreen(raporId = "", downloadUrl = "", onBack = {})
    }
}
