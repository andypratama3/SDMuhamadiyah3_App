package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureStorage
import com.sdm3.parent.data.remote.api.NotificationPreferencesApi
import com.sdm3.parent.data.remote.dto.toDomain
import com.sdm3.parent.data.remote.dto.toDto
import com.sdm3.parent.domain.repository.SettingsRepositoryContract
import com.sdm3.parent.feature.profil.NotificationSettings
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SettingsRepository(
    private val api: NotificationPreferencesApi,
    private val secureStorage: SecureStorage,
) : SettingsRepositoryContract {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = "notification_settings"

    override suspend fun loadNotificationSettings(): NotificationSettings {
        return when (val result = api.getPreferences()) {
            is ApiResult.Success -> {
                val settings = result.data.toDomain()
                cacheLocally(settings)
                settings
            }
            is ApiResult.Error -> loadFromLocal()
        }
    }

    override suspend fun saveNotificationSettings(settings: NotificationSettings) {
        when (val result = api.updatePreferences(settings.toDto())) {
            is ApiResult.Success -> cacheLocally(result.data.toDomain())
            is ApiResult.Error -> throw IllegalStateException(result.error.message())
        }
    }

    private fun ApiError.message(): String = when (this) {
        ApiError.NoInternet -> "Tidak ada koneksi internet"
        ApiError.Timeout -> "Permintaan timeout"
        is ApiError.Unauthorized -> message
        is ApiError.Forbidden -> message
        ApiError.NotFound -> "Data tidak ditemukan"
        ApiError.SessionExpired -> "Sesi berakhir, silakan login kembali"
        is ApiError.Validation -> fieldErrors.values.flatten().firstOrNull() ?: "Validasi gagal"
        is ApiError.RateLimited -> "Terlalu banyak permintaan"
        is ApiError.ServerError -> "Kesalahan server ($code)"
        is ApiError.Unknown -> message
    }

    private suspend fun loadFromLocal(): NotificationSettings {
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

    private suspend fun cacheLocally(settings: NotificationSettings) {
        secureStorage.set(key, json.encodeToString(settings))
    }
}
