/*
 * SDM3 Parent Portal — Secure Token Storage (Security Agent domain)
 *
 * Memenuhi aturan Section 21:
 *  - JANGAN simpan token di SharedPreferences/DataStore biasa
 *  - WAJIB pakai KVault (AES-256 via Android Keystore / iOS Keychain)
 *  - JANGAN log token sama sekali, termasuk di Napier debug build
 */
package com.sdm3.parent.core.security

import com.liftric.kvault.KVault

private const val KEY_BEARER_TOKEN = "sdm3_bearer_token"
private const val KEY_SELECTED_STUDENT_ID = "sdm3_selected_student_id"
private const val KEY_BIOMETRIC_ENABLED = "sdm3_biometric_enabled"
private const val KEY_FCM_TOKEN = "sdm3_fcm_token"

/**
 * Satu-satunya pintu masuk untuk membaca/menulis data sensitif lokal.
 * [KVault] sudah multiplatform: di Android memakai EncryptedSharedPreferences
 * berbasis Android Keystore, di iOS memakai Keychain — tidak perlu
 * expect/actual manual.
 *
 * Instance ini didaftarkan sekali di Koin sebagai `single`, lalu di-inject
 * ke AuthRepository, AuthInterceptor, dan SessionManager — JANGAN dibuat
 * instance baru di tempat lain (anti duplikasi, Section 21).
 */
class SecureTokenManager(private val kVault: KVault) {

    /**
     * Hanya dipakai sebagai FALLBACK untuk client native/WebView yang tidak
     * bisa mengandalkan cookie. Jalur utama tetap Sanctum SPA cookie-based
     * (lihat 02-NetworkClient.kt).
     */
    fun saveBearerToken(token: String) {
        kVault.set(key = KEY_BEARER_TOKEN, stringValue = token)
        // TIDAK ADA logging di sini — bahkan di level debug.
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

    /**
     * Dipanggil saat:
     *  1. User tap "Keluar Akun" (logout biasa)
     *  2. Server membalas 419 Session Expired
     *  3. User berhasil submit "Hapus Akun" (lihat 04-AccountDeletionFeature.kt)
     *  4. 5x gagal login berturut-turut (Section 21 — data wipe setelah brute force)
     *
     * WAJIB hapus SEMUA key, jangan hanya token — supaya tidak ada sisa data
     * pribadi (selected_student_id dkk) yang ketinggalan di device setelah logout.
     */
    fun clearAllSecureData() {
        kVault.deleteObject(forKey = KEY_BEARER_TOKEN)
        kVault.deleteObject(forKey = KEY_SELECTED_STUDENT_ID)
        kVault.deleteObject(forKey = KEY_FCM_TOKEN)
        // Sengaja TIDAK menghapus KEY_BIOMETRIC_ENABLED — itu preferensi device,
        // bukan data pribadi siswa, jadi aman dipertahankan antar sesi login.
    }
}

/*
 * SETUP GRADLE:
 *
 * commonMain.dependencies {
 *     implementation("com.liftric:kvault:<versi-terbaru>")
 * }
 *
 * Koin module:
 *
 * val securityModule = module {
 *     single { KVault(serviceName = "SDM3ParentPortal", accessGroup = null) }
 *     single { SecureTokenManager(kVault = get()) }
 * }
 */
