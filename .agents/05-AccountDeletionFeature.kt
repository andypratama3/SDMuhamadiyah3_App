/*
 * SDM3 Parent Portal — Account Deletion Feature
 *
 * KENAPA INI WAJIB ADA (tidak ada di blueprint v1.0.0 asli):
 *
 * Google Play (User Data Policy — Account Deletion Requirement):
 *   "If your app allows users to create an account from within your app,
 *    then it must also allow users to request for their account to be
 *    deleted. Users must have a readily discoverable option to initiate
 *    app account deletion from within your app AND outside of your app."
 *   -> Tanpa ini, app TIDAK BISA disubmit/diupdate di Play Console
 *      (Data Safety form akan menandai non-compliant).
 *
 * Apple App Store Review Guideline 5.1.1(v):
 *   App yang punya akun WAJIB punya jalur hapus akun di dalam app,
 *   bukan cuma "hubungi customer service".
 *
 * NUANSA KHUSUS APP SEKOLAH:
 * Akun orang tua biasanya diprovisikan oleh admin sekolah (bukan self-signup),
 * dan data siswa terhubung ke catatan akademik resmi yang punya kewajiban
 * retensi (rapor, riwayat pembayaran untuk audit keuangan sekolah). Karena
 * itu, proses ini didesain sebagai "REQUEST" yang diverifikasi admin —
 * BUKAN instant-delete tanpa konfirmasi — dan harus dijelaskan dengan jelas
 * di Privacy Policy + di dalam UI (lihat copy text di Composable di bawah).
 */
package com.sdm3.parent.feature.profil.accountdeletion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// =============================================================================
// 1. UI STATE
// =============================================================================

data class AccountDeletionUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val reasonText: String = "",
    val isConfirmDialogShown: Boolean = false,
    val isRequestSubmitted: Boolean = false
) : ScreenState {
    override val isEmpty: Boolean = false
}

// =============================================================================
// 2. REPOSITORY CONTRACT (implementasi sebenarnya ada di Data Agent domain)
// =============================================================================

interface AccountDeletionRepository {
    /**
     * POST /api/parent/account/deletion-request
     * Body: { "reason": "<opsional, alasan dari orang tua>" }
     *
     * Laravel akan:
     *  1. Menandai user.deletion_requested_at = now()
     *  2. Mengirim notifikasi ke admin sekolah untuk verifikasi & proses manual
     *  3. Mengirim email konfirmasi ke parent (supaya ada bukti request)
     *  4. SLA: data WAJIB benar-benar terhapus dalam batas waktu yang
     *     dijanjikan di Privacy Policy (rekomendasi: maksimal 30 hari,
     *     kecuali ada kewajiban retensi data akademik/keuangan yang
     *     dijelaskan terpisah ke user)
     */
    suspend fun requestAccountDeletion(reason: String?): ApiResult<Unit>
}

// =============================================================================
// 3. VIEWMODEL
// =============================================================================

class AccountDeletionViewModel(
    private val repository: AccountDeletionRepository,
    private val secureTokenManager: SecureTokenManager
) : BaseViewModel<AccountDeletionUiState>(AccountDeletionUiState()) {

    private val _uiState = MutableStateFlow(AccountDeletionUiState())
    val state: StateFlow<AccountDeletionUiState> = _uiState.asStateFlow()

    fun onReasonChanged(value: String) {
        _uiState.value = _uiState.value.copy(reasonText = value)
    }

    fun onRequestDeletionClicked() {
        _uiState.value = _uiState.value.copy(isConfirmDialogShown = true)
    }

    fun onDismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(isConfirmDialogShown = false)
    }

    /** Dipanggil setelah user menekan "Ya, Hapus Akun Saya" di dialog konfirmasi. */
    fun onConfirmDeletion() {
        val reason = _uiState.value.reasonText.takeIf { it.isNotBlank() }
        _uiState.value = _uiState.value.copy(isLoading = true, isConfirmDialogShown = false)

        viewModelScope.launch {
            when (val result = repository.requestAccountDeletion(reason)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRequestSubmitted = true
                    )
                    // PENTING: setelah request terkirim, lakukan logout lokal +
                    // hapus seluruh secure storage di device ini — bukan tunggu
                    // proses admin selesai. Data di server tetap ada sampai admin
                    // memproses, tapi sesi di device ini harus langsung berakhir.
                    secureTokenManager.clearAllSecureData()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.error.toUserMessageSafe()
                    )
                }
            }
        }
    }
}

/** Helper kecil supaya tidak bergantung ke fungsi protected di BaseViewModel. */
private fun com.sdm3.parent.core.network.ApiError.toUserMessageSafe(): String = when (this) {
    com.sdm3.parent.core.network.ApiError.NoInternet -> "Tidak ada koneksi internet."
    else -> "Gagal mengirim permintaan. Silakan coba lagi atau hubungi sekolah."
}

// =============================================================================
// 4. UI — Screen ini WAJIB bisa diakses dari Profil Akun (lihat SCR-014)
//    dengan label yang jelas, contoh: "Hapus Akun Saya" — JANGAN disembunyikan
//    di sub-menu yang sulit ditemukan (syarat Play Store: "readily discoverable")
// =============================================================================

@Composable
fun AccountDeletionScreen(
    viewModel: AccountDeletionViewModel,
    onDeletionRequestSubmitted: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    "data pribadi yang terkait (profil, riwayat notifikasi, preferensi " +
                    "aplikasi) ke admin SD Muhammadiyah 3 Samarinda. " +
                    "Data akademik resmi (nilai, rapor, riwayat kehadiran) dan riwayat " +
                    "transaksi pembayaran akan tetap disimpan sesuai kewajiban arsip " +
                    "sekolah, sebagaimana dijelaskan pada Kebijakan Privasi kami. " +
                    "Proses ini biasanya selesai dalam 30 hari kerja.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = uiState.reasonText,
                onValueChange = viewModel::onReasonChanged,
                label = { Text("Alasan (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::onRequestDeletionClicked,
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
            onDismissRequest = viewModel::onDismissConfirmDialog,
            title = { Text("Konfirmasi Hapus Akun") },
            text = {
                Text("Apakah Anda yakin ingin mengajukan penghapusan akun? Tindakan ini tidak dapat dibatalkan setelah diproses oleh sekolah.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDeletion) {
                    Text("Ya, Hapus Akun Saya", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissConfirmDialog) {
                    Text("Batal")
                }
            }
        )
    }

    if (uiState.isRequestSubmitted) {
        onDeletionRequestSubmitted()
    }
}

/*
 * =============================================================================
 * 5. SISI LARAVEL — routes/api.php (perlu ditambahkan ke Phase 0 / P0-T2)
 * =============================================================================
 *
 * Route::middleware(['auth:sanctum', 'permission:access-parent-portal'])
 *     ->prefix('parent')
 *     ->group(function () {
 *         Route::post('/account/deletion-request', [AccountDeletionController::class, 'store']);
 *     });
 *
 * class AccountDeletionController extends Controller
 * {
 *     public function store(Request $request)
 *     {
 *         $request->validate(['reason' => 'nullable|string|max:1000']);
 *
 *         $user = $request->user();
 *         $user->update(['deletion_requested_at' => now()]);
 *
 *         // Notifikasi ke admin sekolah untuk verifikasi manual
 *         Notification::send(User::role('admin')->get(), new AccountDeletionRequested($user));
 *
 *         // Email konfirmasi ke parent sebagai bukti request sudah diterima
 *         Mail::to($user->email)->queue(new AccountDeletionRequestedMail($user));
 *
 *         return response()->json([
 *             'success' => true,
 *             'message' => 'Permintaan penghapusan akun telah diterima.',
 *         ]);
 *     }
 * }
 *
 * =============================================================================
 * 6. JANGAN LUPA — bagian "di luar app" (web link, wajib didaftarkan di
 *    Play Console > App Content > Data deletion):
 * =============================================================================
 *
 * Buat 1 halaman statis publik, contoh: https://sdm3.sch.id/hapus-akun
 * Isinya: form sederhana (nama, email/no HP terdaftar, alasan) yang submit
 * ke endpoint Laravel yang sama secara tidak-login (public form + verifikasi
 * manual admin), supaya orang tua TIDAK PERLU install ulang app untuk
 * mengajukan penghapusan akun.
 */
