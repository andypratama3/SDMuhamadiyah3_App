package com.sdm3.parent.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Sdm3FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed FCM token: $token")
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Push received: ${message.from}")
        PushNotificationDisplay.show(applicationContext, message)
    }

    companion object {
        private const val TAG = "Sdm3FcmService"
    }
}
