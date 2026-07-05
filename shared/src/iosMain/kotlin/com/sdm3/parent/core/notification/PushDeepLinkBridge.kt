package com.sdm3.parent.core.notification

/**
 * Entry point for the iOS Swift layer to pass notification tap payloads into Kotlin.
 */
object PushDeepLinkBridge {
    fun onNotificationOpened(data: Map<String, String>) {
        PushDeepLinkHolder.setFromMap(data)
    }
}
