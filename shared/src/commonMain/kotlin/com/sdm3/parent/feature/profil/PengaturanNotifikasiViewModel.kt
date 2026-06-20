package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState

data class PengaturanNotifikasiUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = false,
    val smsEnabled: Boolean = false,
    val nilaiNotif: Boolean = true,
    val tagihanNotif: Boolean = true,
    val pengumumanNotif: Boolean = true,
    val kehadiranNotif: Boolean = true,
    val raporNotif: Boolean = false
) : ScreenState

class PengaturanNotifikasiViewModel : BaseViewModel<PengaturanNotifikasiUiState>(PengaturanNotifikasiUiState()) {

    fun loadSettings() {
        updateState { it.copy(isLoading = true) }
        // Would load from a settings repository/preferences
        updateState { it.copy(isLoading = false) }
    }

    fun saveSettings() {
        updateState { it.copy(isLoading = true) }
        // Would persist to a settings repository/preferences
        updateState { it.copy(isLoading = false) }
    }

    fun togglePush() {
        updateState { it.copy(pushEnabled = !it.pushEnabled) }
    }

    fun toggleEmail() {
        updateState { it.copy(emailEnabled = !it.emailEnabled) }
    }

    fun toggleSms() {
        updateState { it.copy(smsEnabled = !it.smsEnabled) }
    }

    fun toggleNilai() {
        updateState { it.copy(nilaiNotif = !it.nilaiNotif) }
    }

    fun toggleTagihan() {
        updateState { it.copy(tagihanNotif = !it.tagihanNotif) }
    }

    fun togglePengumuman() {
        updateState { it.copy(pengumumanNotif = !it.pengumumanNotif) }
    }

    fun toggleKehadiran() {
        updateState { it.copy(kehadiranNotif = !it.kehadiranNotif) }
    }

    fun toggleRapor() {
        updateState { it.copy(raporNotif = !it.raporNotif) }
    }
}
