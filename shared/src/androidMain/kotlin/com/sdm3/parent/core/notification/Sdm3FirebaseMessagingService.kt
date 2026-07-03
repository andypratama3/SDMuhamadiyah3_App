package com.sdm3.parent.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Sdm3FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        PushNotificationDisplay.show(applicationContext, message)
    }
}
