package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            TopAppBar(
                title = {
                    Text(
                        text = "Hapus Akun",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Sdm3ElevatedCard(padding = Spacing.lg) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = SDM3Shapes.medium,
                            color = StatusDanger.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = StatusDanger,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(Spacing.md))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Perhatian Penting!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusDanger
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "Permintaan ini akan mengajukan penghapusan akun Anda beserta " +
                                    "data pribadi yang terkait ke admin SD Muhammadiyah 3 Samarinda. " +
                                    "Data akademik resmi (nilai, rapor, riwayat kehadiran) dan riwayat " +
                                    "transaksi pembayaran akan tetap disimpan sesuai kewajiban arsip sekolah.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    item { StatusChip(text = "Data Akademik Tetap Tersimpan", color = StatusWarning) }
                    item { StatusChip(text = "Proses 3-7 Hari Kerja", color = StatusInfo) }
                    item { StatusChip(text = "Tidak Dapat Dibatalkan", color = StatusDanger) }
                }
            }

            item {
                Sdm3Card(padding = Spacing.lg) {
                    Text(
                        text = "Alasan Penghapusan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Sdm3TextField(
                        value = uiState.reasonText,
                        onValueChange = { uiState = uiState.copy(reasonText = it) },
                        label = "Alasan (Opsional)",
                        leadingIcon = Icons.Outlined.EditNote,
                        singleLine = false
                    )
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Text(
                        text = uiState.errorMessage!!,
                        color = colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Sdm3Button(
                    text = "Ajukan Penghapusan Akun",
                    onClick = { uiState = uiState.copy(isConfirmDialogShown = true) },
                    isLoading = uiState.isLoading,
                    containerColor = StatusDanger,
                    icon = Icons.Outlined.DeleteForever
                )
            }

            item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
        }
    }

    if (uiState.isConfirmDialogShown) {
        AlertDialog(
            onDismissRequest = { uiState = uiState.copy(isConfirmDialogShown = false) },
            shape = DialogShape,
            title = {
                Text(
                    text = "Konfirmasi Penghapusan Akun",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin mengajukan penghapusan akun? " +
                        "Tindakan ini tidak dapat dibatalkan setelah diproses oleh sekolah.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        uiState = uiState.copy(isConfirmDialogShown = false, isRequestSubmitted = true)
                        onDeletionRequestSubmitted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Ya, Hapus", color = colorScheme.onError, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { uiState = uiState.copy(isConfirmDialogShown = false) }) {
                    Text("Batal", color = colorScheme.primary, fontWeight = FontWeight.Medium)
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
