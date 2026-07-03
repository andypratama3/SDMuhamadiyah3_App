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
        launchSafely {
            notificationRepository.markAsRead(id)
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
    }

    fun markAllAsRead() {
        launchSafely {
            notificationRepository.markAllAsRead()
            val updated = uiState.value.notifications.map { it.copy(readAt = "now") }
            updateState {
                it.copy(
                    notifications = updated,
                    unreadCount = 0
                )
            }
        }
    }

    fun refresh() {
        loadNotifications()
    }
}
