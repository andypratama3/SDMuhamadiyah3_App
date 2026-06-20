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

abstract class BaseViewModel<S : ScreenState>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _sessionExpiredEvent = MutableStateFlow(0)
    val sessionExpiredEvent: StateFlow<Int> = _sessionExpiredEvent.asStateFlow()

    protected fun updateState(transform: (S) -> S) {
        _uiState.update(transform)
    }

    protected fun launchSafely(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit
    ) {
        val handler = CoroutineExceptionHandler { _, throwable ->
            onError?.invoke(throwable)
        }
        viewModelScope.launch(handler) { block() }
    }

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
