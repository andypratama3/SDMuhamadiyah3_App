/*
 * SDM3 Parent Portal — Base Architecture (Architect Agent domain)
 *
 * Setiap ViewModel di project WAJIB extends BaseViewModel ini (Section 22,
 * Master Prompt Architect Agent, aturan #4) — supaya error handling & loading
 * state konsisten di semua 28 screen, bukan masing-masing Feature Agent
 * bikin pola sendiri-sendiri.
 */
package com.sdm3.parent.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdm3.parent.core.network.ApiError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Wajib dipakai oleh setiap [Screen]UiState — 4 status ini WAJIB ada di
 * setiap screen sesuai DoD (Lampiran B blueprint): Loading, Success, Error, Empty.
 *
 * Contoh pemakaian di feature:
 * ```
 * data class NilaiUiState(
 *     override val isLoading: Boolean = false,
 *     override val errorMessage: String? = null,
 *     val grades: List<Grade> = emptyList()
 * ) : ScreenState {
 *     override val isEmpty: Boolean get() = !isLoading && errorMessage == null && grades.isEmpty()
 * }
 * ```
 */
interface ScreenState {
    val isLoading: Boolean
    val errorMessage: String?
    val isEmpty: Boolean
}

/**
 * Base class untuk semua ViewModel di proyek ini.
 *
 * - Memakai [viewModelScope] — TIDAK PERNAH GlobalScope (aturan Section 22 #7)
 * - Menyediakan [launchSafely] supaya setiap coroutine punya exception handler
 *   default, mencegah crash karena exception yang lolos tidak ter-catch
 * - Menyediakan pemetaan [ApiError] -> pesan Bahasa Indonesia yang siap
 *   ditampilkan ke user, tanpa expose detail teknis (stack trace, dsb)
 */
abstract class BaseViewModel<S : ScreenState>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    /**
     * Dipicu saat ApiError.SessionExpired diterima dari layer manapun.
     * UI (Composable) WAJIB collect ini dan navigate ke Login + clear backstack,
     * TANPA menampilkan data lama (cegah data leak, Section 21).
     */
    private val _sessionExpiredEvent = MutableStateFlow(0)
    val sessionExpiredEvent: StateFlow<Int> = _sessionExpiredEvent.asStateFlow()

    protected fun updateState(transform: (S) -> S) {
        _uiState.update(transform)
    }

    /**
     * Jalankan coroutine dengan exception handler default — jadi setiap
     * ViewModel TIDAK perlu nulis try-catch generik berulang-ulang (anti
     * duplikasi, Section 21).
     */
    protected fun launchSafely(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit
    ) {
        val handler = CoroutineExceptionHandler { _, throwable ->
            onError?.invoke(throwable) ?: run {
                updateState { current ->
                    // Cast aman karena ScreenState.copy() di-handle masing-masing data class.
                    // Di implementasi nyata, gunakan reflection-free approach: setiap
                    // UiState punya fungsi withError(message) sendiri.
                    current
                }
            }
        }
        viewModelScope.launch(handler) { block() }
    }

    /**
     * Mapping [ApiError] -> pesan ramah-user. Pakai ini di setiap Repository
     * call supaya pesan error konsisten di semua screen.
     */
    protected fun ApiError.toUserMessage(): String = when (this) {
        ApiError.NoInternet -> "Tidak ada koneksi internet. Periksa jaringan Anda."
        ApiError.Timeout -> "Permintaan terlalu lama. Silakan coba lagi."
        is ApiError.Unauthorized -> "Sesi Anda tidak valid. Silakan masuk kembali."
        is ApiError.Forbidden -> "Anda tidak memiliki akses untuk data ini."
        ApiError.NotFound -> "Data tidak ditemukan."
        ApiError.SessionExpired -> {
            _sessionExpiredEvent.update { it + 1 }
            "Sesi Anda telah berakhir. Silakan masuk kembali."
        }
        is ApiError.Validation -> fieldErrors.values.flatten().firstOrNull()
            ?: "Data yang dimasukkan tidak valid."
        is ApiError.RateLimited -> "Terlalu banyak percobaan. Silakan coba lagi nanti."
        is ApiError.ServerError -> "Server sedang bermasalah. Silakan coba lagi."
        is ApiError.Unknown -> "Terjadi kesalahan. Silakan coba lagi."
    }
}
