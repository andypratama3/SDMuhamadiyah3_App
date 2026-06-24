package com.sdm3.parent.core.notification

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

actual class FcmTokenProvider {
    private var cachedToken: String? = null

    actual suspend fun getToken(): String? {
        return cachedToken
    }

    actual fun onNewToken(token: String) {
        cachedToken = token
    }

    fun requestPermission() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, _ ->
            if (granted) {
                // Register for remote notifications handled in Swift layer
            }
        }
    }
}
