package com.sdm3.parent.core.notification

interface FcmRegistrar {
    suspend fun registerIfAvailable()
    suspend fun unregisterIfNeeded()
}
