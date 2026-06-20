package com.sdm3.parent.core.security

interface SecureStorage {
    fun set(key: String, value: String): Boolean
    fun set(key: String, value: Boolean): Boolean
    fun string(forKey: String): String?
    fun bool(forKey: String): Boolean?
    fun deleteObject(forKey: String): Boolean
    fun clear(): Boolean
}
