package com.sdm3.parent.data.repository

import com.sdm3.parent.core.security.SecureStorage
import com.sdm3.parent.domain.repository.SettingsRepositoryContract
import com.sdm3.parent.feature.profil.NotificationSettings
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SettingsRepository(
    private val secureStorage: SecureStorage
) : SettingsRepositoryContract {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = "notification_settings"

    override suspend fun loadNotificationSettings(): NotificationSettings {
        val stored = secureStorage.string(key)
        return if (stored != null) {
            try {
                json.decodeFromString<NotificationSettings>(stored)
            } catch (_: Exception) {
                NotificationSettings()
            }
        } else {
            NotificationSettings()
        }
    }

    override suspend fun saveNotificationSettings(settings: NotificationSettings) {
        secureStorage.set(key, json.encodeToString(settings))
    }
}
