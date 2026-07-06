package com.sdm3.parent.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdm3.parent.core.event.SessionEventBus
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.userMessage
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
        viewModelScope.launch(handler) {
            try {
                block()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    protected fun ApiError.toUserMessage(): String {
        val message = userMessage()
        if (this is ApiError.Unauthorized || this is ApiError.SessionExpired) {
            _sessionExpiredEvent.update { it + 1 }
            SessionEventBus.emit()
        }
        return message
    }
}
