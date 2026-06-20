package com.sdm3.parent.core.security

import com.liftric.kvault.KVault

class KVaultSecureStorage(private val kVault: KVault) : SecureStorage {

    override fun set(key: String, value: String): Boolean =
        kVault.set(key, value)

    override fun set(key: String, value: Boolean): Boolean =
        kVault.set(key, value)

    override fun string(forKey: String): String? =
        kVault.string(forKey)

    override fun bool(forKey: String): Boolean? =
        kVault.bool(forKey)

    override fun deleteObject(forKey: String): Boolean =
        kVault.deleteObject(forKey)

    override fun clear(): Boolean =
        kVault.clear()
}
