package com.sdm3.parent.core.auth

import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.domain.repository.AuthRepositoryContract

/**
 * Single logout path: unregister FCM, revoke server token, clear local session.
 */
class SessionLogoutCoordinator(
    private val authRepository: AuthRepositoryContract,
    private val fcmRegistrar: FcmRegistrar,
) {
    suspend fun logout() {
        try {
            fcmRegistrar.unregisterIfNeeded()
            authRepository.apiLogout()
        } catch (_: Exception) {
            // Best-effort server logout; always clear local session.
        } finally {
            authRepository.logout()
        }
    }
}
