package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.data.repository.NotificationRepository

data class NotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0
) : ScreenState

class NotifikasiViewModel(
    private val notificationRepository: NotificationRepository
) : BaseViewModel<NotifikasiUiState>(NotifikasiUiState()) {

    fun loadNotifications() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
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
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun markAsRead(id: String) {
        launchSafely {
            when (val result = notificationRepository.markAsRead(id)) {
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
                is ApiResult.Error -> { }
            }
        }
    }

    fun markAllAsRead() {
        val unreadIds = uiState.value.notifications.filter { it.readAt == null }.map { it.id }
        unreadIds.forEach { id -> markAsRead(id) }
    }

    fun refresh() {
        loadNotifications()
    }
}
