package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview

data class AccountDeletionUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val reasonText: String = "",
    val isConfirmDialogShown: Boolean = false,
    val isRequestSubmitted: Boolean = false
) : ScreenState {
    override val isEmpty: Boolean = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDeletionScreen(
    onBack: () -> Unit,
    onDeletionRequestSubmitted: () -> Unit
) {
    var uiState by remember { mutableStateOf(AccountDeletionUiState()) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Penghapusan Akun",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "KONFIGURASI PRIVASI",
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
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.error, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Sdm3Card(padding = 24.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = colorScheme.error.copy(alpha = 0.05f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = colorScheme.error,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Aturan Terminasi Akun",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Permintaan ini bersifat permanen untuk akses digital. Data akademik resmi tetap tersimpan pada database institusi sesuai regulasi kependidikan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.primary.copy(alpha = 0.7f),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        item { StatusChip(text = "Arsip Terjamin", color = colorScheme.primary) }
                        item { StatusChip(text = "Proses 7 Hari", color = colorScheme.secondary) }
                        item { StatusChip(text = "Final & Absolut", color = colorScheme.error) }
                    }
                }

                item {
                    SectionHeader(title = "Alasan Terminasi", modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Sdm3Card(padding = 20.dp) {
                        Sdm3TextField(
                            value = uiState.reasonText,
                            onValueChange = { uiState = uiState.copy(reasonText = it) },
                            label = "JUSTIFIKASI PENGHAPUSAN",
                            placeholder = "Tuliskan alasan Anda...",
                            leadingIcon = Icons.Outlined.EditNote,
                            singleLine = false,
                            modifier = Modifier.height(120.dp)
                        )
                    }
                }

                if (uiState.errorMessage != null) {
                    item {
                        Surface(
                            color = colorScheme.error.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Sdm3Button(
                        text = "Ajukan Terminasi Akun",
                        onClick = { uiState = uiState.copy(isConfirmDialogShown = true) },
                        isLoading = uiState.isLoading,
                        containerColor = colorScheme.error,
                        contentColor = Color.White,
                        icon = Icons.Outlined.DeleteForever,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }

    if (uiState.isConfirmDialogShown) {
        AlertDialog(
            onDismissRequest = { uiState = uiState.copy(isConfirmDialogShown = false) },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Konfirmasi Mutlak",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            },
            text = {
                Text(
                    text = "Tindakan ini akan mengakhiri seluruh hak akses digital Anda pada portal ini. Apakah Anda yakin?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        uiState = uiState.copy(isConfirmDialogShown = false, isRequestSubmitted = true)
                        onDeletionRequestSubmitted()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text("Ya, Hapus Akun", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { uiState = uiState.copy(isConfirmDialogShown = false) }) {
                    Text("Batal", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview
@Composable
private fun AccountDeletionScreenPreview() {
    SDM3Theme {
        AccountDeletionScreen(
            onBack = {},
            onDeletionRequestSubmitted = {}
        )
    }
}
