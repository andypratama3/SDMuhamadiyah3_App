package com.sdm3.parent.core.notification

import com.sdm3.parent.isDebugBuild
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Sdm3FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        if (isDebugBuild()) Log.d(TAG, "Refreshed FCM token")
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (isDebugBuild()) Log.d(TAG, "Push received: ${message.from}")
        PushNotificationDisplay.show(applicationContext, message)
    }

    companion object {
        private const val TAG = "Sdm3FcmService"
    }
}
