package com.sdm3.parent.core.security

private const val KEY_BEARER_TOKEN = "sdm3_bearer_token"
private const val KEY_SELECTED_STUDENT_ID = "sdm3_selected_student_id"
private const val KEY_BIOMETRIC_ENABLED = "sdm3_biometric_enabled"
private const val KEY_FCM_TOKEN = "sdm3_fcm_token"

class SecureTokenManager(private val storage: SecureStorage) {

    fun saveBearerToken(token: String) {
        storage.set(key = KEY_BEARER_TOKEN, value = token)
    }

    fun getBearerToken(): String? = storage.string(forKey = KEY_BEARER_TOKEN)

    fun saveSelectedStudentId(studentId: String) {
        storage.set(key = KEY_SELECTED_STUDENT_ID, value = studentId)
    }

    fun getSelectedStudentId(): String? = storage.string(forKey = KEY_SELECTED_STUDENT_ID)

    fun setBiometricEnabled(enabled: Boolean) {
        storage.set(key = KEY_BIOMETRIC_ENABLED, value = enabled)
    }

    fun isBiometricEnabled(): Boolean = storage.bool(forKey = KEY_BIOMETRIC_ENABLED) ?: false

    fun saveFcmToken(token: String) {
        storage.set(key = KEY_FCM_TOKEN, value = token)
    }

    fun getFcmToken(): String? = storage.string(forKey = KEY_FCM_TOKEN)

    fun clearAllSecureData() {
        storage.deleteObject(forKey = KEY_BEARER_TOKEN)
        storage.deleteObject(forKey = KEY_SELECTED_STUDENT_ID)
        storage.deleteObject(forKey = KEY_FCM_TOKEN)
    }
}
