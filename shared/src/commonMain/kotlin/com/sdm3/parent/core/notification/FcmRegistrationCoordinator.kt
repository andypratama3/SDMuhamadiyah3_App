package com.sdm3.parent.core.notification

import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Re-registers FCM token with backend when Firebase refreshes the device token.
 */
object FcmRegistrationCoordinator {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var registrar: FcmRegistrar? = null
    private var secureTokenManager: SecureTokenManager? = null

    fun bind(registrar: FcmRegistrar, secureTokenManager: SecureTokenManager) {
        this.registrar = registrar
        this.secureTokenManager = secureTokenManager
    }

    fun onTokenRefresh(token: String) {
        FcmTokenStore.updateToken(token)
        val manager = secureTokenManager ?: return
        if (manager.getBearerToken().isNullOrBlank()) return
        if (!manager.getRoleContext().hasParentAccess) return
        scope.launch {
            registrar?.registerIfAvailable()
        }
    }
}
