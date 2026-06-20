package com.sdm3.parent.core.security

import com.sdm3.parent.core.test.TestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeKVault {
    private val store = mutableMapOf<String, Any>()

    fun set(key: String, stringValue: String): Boolean {
        store[key] = stringValue
        return true
    }

    fun set(key: String, boolValue: Boolean): Boolean {
        store[key] = boolValue
        return true
    }

    fun string(forKey: String): String? = store[forKey] as? String

    fun bool(forKey: String): Boolean? = store[forKey] as? Boolean

    fun deleteObject(forKey: String): Boolean {
        store.remove(forKey)
        return true
    }

    fun clear(): Boolean {
        store.clear()
        return true
    }
}

class SecureTokenManagerTest : TestDispatcher() {

    @Test
    fun saveAndRetrieveBearerToken() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        manager.saveBearerToken("test-bearer-token")
        val retrieved = manager.getBearerToken()

        assertEquals("test-bearer-token", retrieved)
    }

    @Test
    fun saveAndRetrieveSelectedStudentId() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        manager.saveSelectedStudentId("student-123")
        val retrieved = manager.getSelectedStudentId()

        assertEquals("student-123", retrieved)
    }

    @Test
    fun saveAndRetrieveFcmToken() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        manager.saveFcmToken("fcm-test-token")
        val retrieved = manager.getFcmToken()

        assertEquals("fcm-test-token", retrieved)
    }

    @Test
    fun biometricEnabledDefaultsToFalse() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        val enabled = manager.isBiometricEnabled()

        assertFalse(enabled)
    }

    @Test
    fun setAndCheckBiometricEnabled() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        manager.setBiometricEnabled(true)
        assertTrue(manager.isBiometricEnabled())

        manager.setBiometricEnabled(false)
        assertFalse(manager.isBiometricEnabled())
    }

    @Test
    fun clearAllSecureDataRemovesAllTokens() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

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
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        assertNull(manager.getBearerToken())
    }

    @Test
    fun getFcmTokenReturnsNullWhenNotSet() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        assertNull(manager.getFcmToken())
    }

    @Test
    fun overwriteBearerToken() {
        val fakeVault = FakeKVault()
        val manager = SecureTokenManager(fakeVault as com.liftric.kvault.KVault)

        manager.saveBearerToken("first-token")
        manager.saveBearerToken("second-token")

        assertEquals("second-token", manager.getBearerToken())
    }
}
