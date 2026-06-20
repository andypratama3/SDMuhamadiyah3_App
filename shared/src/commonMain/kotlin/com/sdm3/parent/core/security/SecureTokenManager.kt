package com.sdm3.parent.core.security

import com.liftric.kvault.KVault

private const val KEY_BEARER_TOKEN = "sdm3_bearer_token"
private const val KEY_SELECTED_STUDENT_ID = "sdm3_selected_student_id"
private const val KEY_BIOMETRIC_ENABLED = "sdm3_biometric_enabled"
private const val KEY_FCM_TOKEN = "sdm3_fcm_token"

class SecureTokenManager(private val kVault: KVault) {

    fun saveBearerToken(token: String) {
        kVault.set(key = KEY_BEARER_TOKEN, stringValue = token)
    }

    fun getBearerToken(): String? = kVault.string(forKey = KEY_BEARER_TOKEN)

    fun saveSelectedStudentId(studentId: String) {
        kVault.set(key = KEY_SELECTED_STUDENT_ID, stringValue = studentId)
    }

    fun getSelectedStudentId(): String? = kVault.string(forKey = KEY_SELECTED_STUDENT_ID)

    fun setBiometricEnabled(enabled: Boolean) {
        kVault.set(key = KEY_BIOMETRIC_ENABLED, boolValue = enabled)
    }

    fun isBiometricEnabled(): Boolean = kVault.bool(forKey = KEY_BIOMETRIC_ENABLED) ?: false

    fun saveFcmToken(token: String) {
        kVault.set(key = KEY_FCM_TOKEN, stringValue = token)
    }

    fun getFcmToken(): String? = kVault.string(forKey = KEY_FCM_TOKEN)

    fun clearAllSecureData() {
        kVault.deleteObject(forKey = KEY_BEARER_TOKEN)
        kVault.deleteObject(forKey = KEY_SELECTED_STUDENT_ID)
        kVault.deleteObject(forKey = KEY_FCM_TOKEN)
    }
}
