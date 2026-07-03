package com.sdm3.parent.core.security

sealed class BiometricResult {
    data object Success : BiometricResult()
    data class Error(val message: String) : BiometricResult()
    data object NotAvailable : BiometricResult()
}

expect class BiometricAuthenticator() : BiometricAuthGate {
    override suspend fun authenticate(reason: String): BiometricResult
}
