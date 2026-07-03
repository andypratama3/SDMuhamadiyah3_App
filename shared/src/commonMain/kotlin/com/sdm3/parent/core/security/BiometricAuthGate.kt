package com.sdm3.parent.core.security

interface BiometricAuthGate {
    suspend fun authenticate(reason: String): BiometricResult
}
