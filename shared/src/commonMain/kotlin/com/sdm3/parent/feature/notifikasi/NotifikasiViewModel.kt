package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.domain.repository.NotificationRepositoryContract

data class NotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0
) : ScreenState

class NotifikasiViewModel(
    private val notificationRepository: NotificationRepositoryContract
) : BaseViewModel<NotifikasiUiState>(NotifikasiUiState()) {

    fun loadNotifications() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = notificationRepository.getNotifications()) {
                is ApiResult.Success -> {
                    val notifs = result.data
                    updateState {
                        it.copy(
                            notifications = notifs,
                            unreadCount = notifs.count { n -> n.readAt == null },
                            isLoading = false,
                            isEmpty = notifs.isEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun markAsRead(id: String) {
        // Jangan tandai terbaca secara lokal bila panggilan API gagal.
        if (uiState.value.notifications.any { it.id == id && it.readAt == null }.not()) return
        launchSafely {
            when (notificationRepository.markAsRead(id)) {
                is ApiResult.Success -> {
                    val updated = uiState.value.notifications.map { n ->
                        if (n.id == id) n.copy(readAt = "now") else n
                    }
                    updateState {
                        it.copy(
                            notifications = updated,
                            unreadCount = updated.count { n -> n.readAt == null }
                        )
                    }
                }
                is ApiResult.Error -> Unit
            }
        }
    }

    fun markAllAsRead() {
        launchSafely {
            when (notificationRepository.markAllAsRead()) {
                is ApiResult.Success -> {
                    val updated = uiState.value.notifications.map { it.copy(readAt = "now") }
                    updateState {
                        it.copy(
                            notifications = updated,
                            unreadCount = 0
                        )
                    }
                }
                is ApiResult.Error -> Unit
            }
        }
    }

    fun refresh() {
        loadNotifications()
    }
}
