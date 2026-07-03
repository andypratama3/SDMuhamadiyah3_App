package com.sdm3.parent.core.notification

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

actual class FcmTokenProvider {
    actual suspend fun getToken(): String? = FcmTokenStore.getCachedToken()

    actual fun onNewToken(token: String) {
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    actual fun requestPermissionIfNeeded() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ -> }
    }
}
