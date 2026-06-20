package com.sdm3.parent.core.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

actual class BiometricAuthenticator {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun authenticate(reason: String): BiometricResult {
        return suspendCancellableCoroutine { continuation ->
            val context = LAContext()

            if (!context.canEvaluatePolicy(
                    LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                    null
                )
            ) {
                continuation.resume(BiometricResult.NotAvailable)
                return@suspendCancellableCoroutine
            }

            context.evaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason
            ) { success, error ->
                if (success) {
                    continuation.resume(BiometricResult.Success)
                } else {
                    val message = error?.localizedDescription
                        ?: "Authentication failed"
                    continuation.resume(BiometricResult.Error(message))
                }
            }
        }
    }
}
