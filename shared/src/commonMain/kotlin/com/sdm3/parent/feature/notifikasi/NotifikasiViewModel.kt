package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.NotificationDto
import kotlinx.coroutines.delay

data class NotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0
) : ScreenState

class NotifikasiViewModel : BaseViewModel<NotifikasiUiState>(NotifikasiUiState()) {

    fun loadNotifications() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)
            val dummyNotifs = listOf(
                NotificationDto(id = "1", type = "pembayaran", title = "Tagihan SPP Januari", message = "Tagihan SPP untuk bulan Januari sudah tersedia. Silakan lakukan pembayaran tepat waktu.", createdAt = "2024-01-15T08:00:00Z"),
                NotificationDto(id = "2", type = "nilai", title = "Nilai Baru: Matematika", message = "Nilai Ujian Tengah Semester mata pelajaran Matematika Ananda telah terbit.", createdAt = "2024-01-15T11:00:00Z"),
                NotificationDto(id = "3", type = "pengumuman", title = "Libur Semester Ganjil", message = "Berdasarkan kalender akademik, libur semester ganjil dimulai dari tanggal 20...", createdAt = "2024-01-14T09:00:00Z", readAt = "2024-01-14T10:00:00Z"),
                NotificationDto(id = "4", type = "pengumuman", title = "Laporan Kedisiplinan", message = "Laporan mingguan kedisiplinan dan kehadiran siswa telah diterbitkan oleh...", createdAt = "2024-01-14T14:00:00Z", readAt = "2024-01-14T15:00:00Z")
            )
            updateState {
                it.copy(
                    notifications = dummyNotifs,
                    unreadCount = dummyNotifs.count { n -> n.readAt == null },
                    isLoading = false,
                    isEmpty = dummyNotifs.isEmpty()
                )
            }
        }
    }

    fun markAsRead(id: String) {
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

    fun markAllAsRead() {
        val updated = uiState.value.notifications.map { it.copy(readAt = "now") }
        updateState {
            it.copy(
                notifications = updated,
                unreadCount = 0
            )
        }
    }

    fun refresh() {
        loadNotifications()
    }
}
