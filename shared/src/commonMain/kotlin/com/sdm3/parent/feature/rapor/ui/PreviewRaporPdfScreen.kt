package com.sdm3.parent.feature.rapor.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
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
        viewModel!!.uiState.collectAsState()
    }

    if (!isPreview) {
        LaunchedEffect(raporId, downloadUrl) { viewModel?.init(raporId, downloadUrl) }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pratinjau Dokumen",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "DOKUMEN PORTABEL PDF",
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
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Sdm3Card(padding = 24.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primary.copy(alpha = 0.05f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(32.dp), tint = colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                text = state.fileName.ifEmpty { "Rapor_Digital_Siswa.pdf" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                            Text(
                                text = "Ukuran: ${state.fileSize.ifEmpty { "1.2 MB" }}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Sdm3Card(padding = 20.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.isDownloaded) Icons.Outlined.TaskAlt else Icons.Outlined.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (state.isDownloaded) StatusSuccess else colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (state.isDownloaded) "Dokumen telah siap dibuka melalui PDF Viewer eksternal." else "Sedang menyinkronkan data dengan server institusi...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.primary.copy(alpha = 0.7f),
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Sdm3Button(
                    text = "Buka Dokumen PDF",
                    onClick = { },
                    icon = Icons.AutoMirrored.Outlined.OpenInNew,
                    enabled = state.isDownloaded,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRaporPdfScreenPreview() {
    SDM3Theme {
        PreviewRaporPdfScreen(raporId = "", downloadUrl = "", onBack = {})
    }
}
