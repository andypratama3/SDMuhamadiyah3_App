package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.network.sanitizeUserFacingMessage
import com.sdm3.parent.domain.repository.SettingsRepositoryContract

data class PengaturanNotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val settings: NotificationSettings = NotificationSettings()
) : ScreenState

class PengaturanNotifikasiViewModel(
    private val settingsRepository: SettingsRepositoryContract,
    private val fcmRegistration: FcmRegistrar,
) : BaseViewModel<PengaturanNotifikasiUiState>(PengaturanNotifikasiUiState()) {

    fun loadSettings() {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(error.message)) }
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
                updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(error.message)) }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            settingsRepository.saveNotificationSettings(uiState.value.settings)
            updateState { it.copy(isLoading = false) }
        }
    }

    fun togglePush() {
        val enabled = !uiState.value.settings.pushEnabled
        updateState { it.copy(settings = it.settings.copy(pushEnabled = enabled)) }
        persistSettings {
            if (enabled) {
                fcmRegistration.registerIfAvailable()
            } else {
                fcmRegistration.unregisterIfNeeded()
            }
        }
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

    private fun persistSettings(onSuccess: suspend () -> Unit = {}) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(errorMessage = sanitizeUserFacingMessage(error.message)) }
            }
        ) {
            settingsRepository.saveNotificationSettings(uiState.value.settings)
            onSuccess()
        }
    }
}
