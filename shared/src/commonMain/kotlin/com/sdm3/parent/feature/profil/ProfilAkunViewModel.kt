package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.security.BiometricAuthGate
import com.sdm3.parent.core.security.BiometricResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import com.sdm3.parent.domain.repository.ProfileRepositoryContract
import com.sdm3.parent.domain.repository.StudentRepositoryContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class ProfilAkunUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val students: List<StudentDto> = emptyList(),
    val isEditing: Boolean = false,
    val editedName: String = "",
    val editedPhone: String = "",
    val biometricEnabled: Boolean = false,
    val biometricMessage: String? = null,
) : ScreenState

class ProfilAkunViewModel(
    private val profileRepository: ProfileRepositoryContract,
    private val studentRepository: StudentRepositoryContract,
    private val authRepository: AuthRepositoryContract,
    private val fcmRegistration: FcmRegistrar,
    private val secureTokenManager: SecureTokenManager,
    private val biometricAuth: BiometricAuthGate,
) : BaseViewModel<ProfilAkunUiState>(ProfilAkunUiState()) {

    private val logoutCompleted = Channel<Unit>(Channel.BUFFERED)
    val logoutCompletedFlow = logoutCompleted.receiveAsFlow()

    fun loadBiometricState() {
        updateState { it.copy(biometricEnabled = secureTokenManager.isBiometricEnabled()) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        if (!enabled) {
            secureTokenManager.setBiometricEnabled(false)
            updateState { it.copy(biometricEnabled = false, biometricMessage = "Login biometrik dinonaktifkan") }
            return
        }
        launchSafely {
            when (val result = biometricAuth.authenticate("Aktifkan login biometrik")) {
                BiometricResult.Success -> {
                    secureTokenManager.setBiometricEnabled(true)
                    updateState { it.copy(biometricEnabled = true, biometricMessage = "Login biometrik diaktifkan") }
                }
                is BiometricResult.Error -> {
                    updateState { it.copy(biometricEnabled = false, biometricMessage = result.message) }
                }
                BiometricResult.NotAvailable -> {
                    updateState {
                        it.copy(
                            biometricEnabled = false,
                            biometricMessage = "Biometrik tidak tersedia di perangkat ini",
                        )
                    }
                }
            }
        }
    }

    fun clearBiometricMessage() {
        updateState { it.copy(biometricMessage = null) }
    }

    fun loadProfile() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = profileRepository.getProfile()) {
                is ApiResult.Success -> {
                    val p = result.data
                    updateState {
                        it.copy(
                            name = p.name,
                            phone = p.phone.orEmpty(),
                            email = p.email,
                            editedName = p.name,
                            editedPhone = p.phone.orEmpty(),
                            isLoading = false
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

    fun loadStudents() {
        launchSafely {
            // Daftar siswa bersifat pelengkap; kegagalannya tidak boleh menutup
            // seluruh layar profil yang datanya sudah berhasil dimuat.
            val result = studentRepository.getStudents()
            if (result is ApiResult.Success) {
                updateState { it.copy(students = result.data) }
            }
        }
    }

    fun startEdit() {
        val s = uiState.value
        updateState { it.copy(isEditing = true, editedName = s.name, editedPhone = s.phone) }
    }

    fun cancelEdit() {
        val s = uiState.value
        updateState { it.copy(isEditing = false, editedName = s.name, editedPhone = s.phone) }
    }

    fun updateEditedName(name: String) {
        updateState { it.copy(editedName = name) }
    }

    fun updateEditedPhone(phone: String) {
        updateState { it.copy(editedPhone = phone) }
    }

    fun updateProfile() {
        val s = uiState.value
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = profileRepository.updateProfile(name = s.editedName, phone = s.editedPhone.ifEmpty { null })) {
                is ApiResult.Success -> {
                    val p = result.data
                    updateState {
                        it.copy(
                            name = p.name,
                            phone = p.phone.orEmpty(),
                            isEditing = false,
                            isLoading = false
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

    fun refresh() {
        loadProfile()
        loadStudents()
    }

    fun logout() {
        launchSafely {
            try {
                fcmRegistration.unregisterIfNeeded()
                authRepository.apiLogout()
            } catch (_: Exception) {
                // Best-effort server logout; always clear local session.
            } finally {
                authRepository.logout()
                logoutCompleted.send(Unit)
            }
        }
    }
}
