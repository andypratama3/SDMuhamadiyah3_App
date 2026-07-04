package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.domain.repository.SettingsRepositoryContract

data class PengaturanNotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val settings: NotificationSettings = NotificationSettings()
) : ScreenState

class PengaturanNotifikasiViewModel(
    private val settingsRepository: SettingsRepositoryContract
) : BaseViewModel<PengaturanNotifikasiUiState>(PengaturanNotifikasiUiState()) {

    fun loadSettings() {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Gagal memuat pengaturan") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            val settings = settingsRepository.loadNotificationSettings()
            updateState { it.copy(isLoading = false, settings = settings) }
        }
    }

    fun saveSettings() {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Gagal menyimpan pengaturan") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            settingsRepository.saveNotificationSettings(uiState.value.settings)
            updateState { it.copy(isLoading = false) }
        }
    }

    fun togglePush() {
        updateState { it.copy(settings = it.settings.copy(pushEnabled = !it.settings.pushEnabled)) }
        persistSettings()
    }

    fun toggleEmail() {
        updateState { it.copy(settings = it.settings.copy(emailEnabled = !it.settings.emailEnabled)) }
        persistSettings()
    }

    fun toggleSms() {
        updateState { it.copy(settings = it.settings.copy(smsEnabled = !it.settings.smsEnabled)) }
        persistSettings()
    }

    fun toggleNilai() {
        updateState { it.copy(settings = it.settings.copy(nilaiNotif = !it.settings.nilaiNotif)) }
        persistSettings()
    }

    fun toggleTagihan() {
        updateState { it.copy(settings = it.settings.copy(tagihanNotif = !it.settings.tagihanNotif)) }
        persistSettings()
    }

    fun togglePengumuman() {
        updateState { it.copy(settings = it.settings.copy(pengumumanNotif = !it.settings.pengumumanNotif)) }
        persistSettings()
    }

    fun toggleKehadiran() {
        updateState { it.copy(settings = it.settings.copy(kehadiranNotif = !it.settings.kehadiranNotif)) }
        persistSettings()
    }

    fun toggleRapor() {
        updateState { it.copy(settings = it.settings.copy(raporNotif = !it.settings.raporNotif)) }
        persistSettings()
    }

    private fun persistSettings() {
        launchSafely(
            onError = { error ->
                updateState { it.copy(errorMessage = error.message ?: "Gagal menyimpan pengaturan") }
            }
        ) {
            settingsRepository.saveNotificationSettings(uiState.value.settings)
        }
    }
}
