package com.sdm3.parent.core.permission

enum class NotificationPermissionStatus {
    GRANTED,
    DENIED,
    NOT_REQUESTED
}

class NotificationPermissionController {

    suspend fun checkStatus(): NotificationPermissionStatus = NotificationPermissionStatus.GRANTED

    suspend fun requestPermission(): NotificationPermissionStatus = NotificationPermissionStatus.GRANTED
}
