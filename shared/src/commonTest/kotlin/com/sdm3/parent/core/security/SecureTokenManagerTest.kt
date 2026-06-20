package com.sdm3.parent.core.security

import com.sdm3.parent.core.test.TestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeSecureStorage : SecureStorage {
    private val store = mutableMapOf<String, Any>()

    override fun set(key: String, value: String): Boolean {
        store[key] = value
        return true
    }

    override fun set(key: String, value: Boolean): Boolean {
        store[key] = value
        return true
    }

    override fun string(forKey: String): String? = store[forKey] as? String

    override fun bool(forKey: String): Boolean? = store[forKey] as? Boolean

    override fun deleteObject(forKey: String): Boolean {
        store.remove(forKey)
        return true
    }

    override fun clear(): Boolean {
        store.clear()
        return true
    }
}

class SecureTokenManagerTest : TestDispatcher() {

    @Test
    fun saveAndRetrieveBearerToken() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.saveBearerToken("test-bearer-token")
        val retrieved = manager.getBearerToken()

        assertEquals("test-bearer-token", retrieved)
    }

    @Test
    fun saveAndRetrieveSelectedStudentId() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.saveSelectedStudentId("student-123")
        val retrieved = manager.getSelectedStudentId()

        assertEquals("student-123", retrieved)
    }

    @Test
    fun saveAndRetrieveFcmToken() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.saveFcmToken("fcm-test-token")
        val retrieved = manager.getFcmToken()

        assertEquals("fcm-test-token", retrieved)
    }

    @Test
    fun biometricEnabledDefaultsToFalse() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        val enabled = manager.isBiometricEnabled()

        assertFalse(enabled)
    }

    @Test
    fun setAndCheckBiometricEnabled() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.setBiometricEnabled(true)
        assertTrue(manager.isBiometricEnabled())

        manager.setBiometricEnabled(false)
        assertFalse(manager.isBiometricEnabled())
    }

    @Test
    fun clearAllSecureDataRemovesAllTokens() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.saveBearerToken("bearer")
        manager.saveFcmToken("fcm")
        manager.saveSelectedStudentId("student-1")

        manager.clearAllSecureData()

        assertNull(manager.getBearerToken())
        assertNull(manager.getFcmToken())
        assertNull(manager.getSelectedStudentId())
    }

    @Test
    fun getBearerTokenReturnsNullWhenNotSet() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        assertNull(manager.getBearerToken())
    }

    @Test
    fun getFcmTokenReturnsNullWhenNotSet() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        assertNull(manager.getFcmToken())
    }

    @Test
    fun overwriteBearerToken() {
        val storage = FakeSecureStorage()
        val manager = SecureTokenManager(storage)

        manager.saveBearerToken("first-token")
        manager.saveBearerToken("second-token")

        assertEquals("second-token", manager.getBearerToken())
    }
}
