package com.sdm3.parent.core.security

import android.content.Context

/**
 * Implementasi Android — memakai SharedPreferences biasa (bukan terenkripsi),
 * yang akan terhapus saat aplikasi di-uninstall.
 */
class AndroidInstallState(context: Context) : InstallState {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isFreshInstall(): Boolean = !prefs.getBoolean(KEY_INSTALLED, false)

    override fun markInstalled() {
        prefs.edit().putBoolean(KEY_INSTALLED, true).apply()
    }

    private companion object {
        const val PREFS_NAME = "sdm3_app_state"
        const val KEY_INSTALLED = "sdm3_installed"
    }
}
