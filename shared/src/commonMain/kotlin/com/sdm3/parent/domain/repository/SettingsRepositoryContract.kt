package com.sdm3.parent.domain.repository

import com.sdm3.parent.feature.profil.NotificationSettings

interface SettingsRepositoryContract {
    suspend fun loadNotificationSettings(): NotificationSettings
    suspend fun saveNotificationSettings(settings: NotificationSettings)
}
