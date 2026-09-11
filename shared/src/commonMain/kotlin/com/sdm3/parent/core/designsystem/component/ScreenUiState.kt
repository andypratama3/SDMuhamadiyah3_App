package com.sdm3.parent.core.designsystem.component

/**
 * Shared UI state for all screens.
 * Eliminates the need for each screen to define its own identical sealed class.
 */
sealed class ScreenUiState {
    data object Loading : ScreenUiState()
    data object Empty : ScreenUiState()
    data class Error(val message: String) : ScreenUiState()
    data object Success : ScreenUiState()
}

/**
 * Resolves ViewModel state booleans into [ScreenUiState].
 */
fun resolveScreenState(
    isLoading: Boolean,
    isEmpty: Boolean,
    errorMessage: String?
): ScreenUiState = when {
    isLoading -> ScreenUiState.Loading
    errorMessage != null -> ScreenUiState.Error(errorMessage)
    isEmpty -> ScreenUiState.Empty
    else -> ScreenUiState.Success
}
