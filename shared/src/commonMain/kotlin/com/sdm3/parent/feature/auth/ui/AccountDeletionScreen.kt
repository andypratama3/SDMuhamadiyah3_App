package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.base.ScreenState

data class AccountDeletionUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val reasonText: String = "",
    val isConfirmDialogShown: Boolean = false,
    val isRequestSubmitted: Boolean = false
) : ScreenState {
    override val isEmpty: Boolean = false
}

@Composable
fun AccountDeletionScreen(
    onDeletionRequestSubmitted: () -> Unit
) {
    var uiState by remember { mutableStateOf(AccountDeletionUiState()) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Hapus Akun",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Permintaan ini akan mengajukan penghapusan akun Anda beserta " +
                    "data pribadi yang terkait ke admin SD Muhammadiyah 3 Samarinda. " +
                    "Data akademik resmi (nilai, rapor, riwayat kehadiran) dan riwayat " +
                    "transaksi pembayaran akan tetap disimpan sesuai kewajiban arsip sekolah.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = uiState.reasonText,
                onValueChange = { uiState = uiState.copy(reasonText = it) },
                label = { Text("Alasan (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { uiState = uiState.copy(isConfirmDialogShown = true) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                } else {
                    Text("Ajukan Penghapusan Akun")
                }
            }
        }
    }

    if (uiState.isConfirmDialogShown) {
        AlertDialog(
            onDismissRequest = { uiState = uiState.copy(isConfirmDialogShown = false) },
            title = { Text("Konfirmasi Hapus Akun") },
            text = {
                Text("Apakah Anda yakin ingin mengajukan penghapusan akun? Tindakan ini tidak dapat dibatalkan setelah diproses oleh sekolah.")
            },
            confirmButton = {
                TextButton(onClick = {
                    uiState = uiState.copy(isConfirmDialogShown = false, isRequestSubmitted = true)
                    onDeletionRequestSubmitted()
                }) {
                    Text("Ya, Hapus Akun Saya", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { uiState = uiState.copy(isConfirmDialogShown = false) }) {
                    Text("Batal")
                }
            }
        )
    }
}
