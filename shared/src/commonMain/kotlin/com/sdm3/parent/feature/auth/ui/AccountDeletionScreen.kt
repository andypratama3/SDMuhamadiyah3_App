package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.feature.auth.AccountDeletionUiState
import com.sdm3.parent.feature.auth.AccountDeletionViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDeletionScreen(
    onBack: () -> Unit,
    onDeletionRequestSubmitted: () -> Unit,
    viewModel: AccountDeletionViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val uiState by if (isPreview) {
        remember { mutableStateOf(AccountDeletionUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(uiState.isRequestSubmitted) {
        if (uiState.isRequestSubmitted) {
            onDeletionRequestSubmitted()
        }
    }

    ScreenScaffold(
        title = "Penghapusan Akun",
        subtitle = "KONFIGURASI PRIVASI",
        onBack = onBack,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
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
                                        contentDescription = "Peringatan",
                                        tint = colorScheme.error,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Spacing.lg))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Aturan Terminasi Akun",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Permintaan ini bersifat permanen untuk akses digital. Data akademik resmi tetap tersimpan pada database institusi sesuai regulasi kependidikan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ProductSchoolTheme.colors.onSurfaceMuted,
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
                        item { StatusChip(text = "Proses 7 Hari", color = statusWarningColor()) }
                        item { StatusChip(text = "Final & Absolut", color = statusDangerColor()) }
                    }
                }

                item {
                    SectionHeader(title = "Alasan Terminasi", modifier = Modifier.padding(top = Spacing.xs))
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Sdm3Card(padding = 20.dp) {
                        Sdm3TextField(
                            value = uiState.reasonText,
                            onValueChange = {
                                if (!isPreview) viewModel.updateReason(it)
                            },
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
                        Sdm3ErrorBanner(message = uiState.errorMessage ?: "")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Sdm3Button(
                        text = "Ajukan Terminasi Akun",
                        onClick = {
                            if (!isPreview) viewModel.showConfirmDialog()
                        },
                        isLoading = uiState.isLoading,
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError,
                        icon = Icons.Outlined.DeleteForever,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xxxl)) }
            }
        }
    }

    if (uiState.isConfirmDialogShown) {
        Sdm3Dialog(
            onDismissRequest = {
                if (!isPreview) viewModel.dismissConfirmDialog()
            },
            title = "Konfirmasi Mutlak",
            confirmLabel = "Ya, Hapus Akun",
            onConfirm = {
                if (!isPreview) viewModel.submitDeletionRequest()
            },
            destructive = true,
            body = {
                Text(
                    text = "Tindakan ini akan mengakhiri seluruh hak akses digital Anda pada aplikasi ini. Apakah Anda yakin?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                )
            },
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
