package com.sdm3.parent.core.security

/**
 * Menandai apakah ini peluncuran pertama setelah aplikasi (re)install.
 *
 * Disimpan pada storage biasa (SharedPreferences di Android, NSUserDefaults di iOS)
 * yang OTOMATIS TERHAPUS saat uninstall — berbeda dengan Keychain (iOS) yang dipakai
 * KVault dan justru BERTAHAN setelah uninstall. Dengan ini, saat aplikasi diinstal
 * ulang, kita bisa membersihkan sisa data lama (bearer token & flag onboarding) agar
 * pengguna kembali melihat onboarding + login dari awal.
 */
interface InstallState {
    /** `true` hanya pada peluncuran pertama setelah (re)install. */
    fun isFreshInstall(): Boolean

    /** Tandai bahwa aplikasi sudah pernah diluncurkan pada instalasi ini. */
    fun markInstalled()
}
