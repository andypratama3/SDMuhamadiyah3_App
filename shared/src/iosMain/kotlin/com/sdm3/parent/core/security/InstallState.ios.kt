package com.sdm3.parent.core.security

import platform.Foundation.NSUserDefaults

/**
 * Implementasi iOS — memakai NSUserDefaults yang terhapus saat uninstall.
 * (Keychain yang dipakai KVault justru bertahan setelah uninstall.)
 */
class IosInstallState : InstallState {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun isFreshInstall(): Boolean = !defaults.boolForKey(KEY_INSTALLED)

    override fun markInstalled() {
        defaults.setBool(true, forKey = KEY_INSTALLED)
    }

    private companion object {
        const val KEY_INSTALLED = "sdm3_installed"
    }
}
