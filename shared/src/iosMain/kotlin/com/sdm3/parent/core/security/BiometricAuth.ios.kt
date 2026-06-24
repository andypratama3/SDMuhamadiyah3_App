package com.sdm3.parent.core.security

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

actual class BiometricAuthenticator {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun authenticate(reason: String): BiometricResult {
        return suspendCancellableCoroutine { continuation ->
            val context = LAContext()

            val canEvaluate = memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                context.canEvaluatePolicy(
                    LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                    errorPtr.ptr
                )
            }

            if (!canEvaluate) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.NotAvailable)
                }
                return@suspendCancellableCoroutine
            }

            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason
            ) { success, authenticationError ->
                if (continuation.isActive) {
                    if (success) {
                        continuation.resume(BiometricResult.Success)
                    } else {
                        val message = authenticationError?.localizedDescription ?: "Autentikasi gagal"
                        continuation.resume(BiometricResult.Error(message))
                    }
                }
            }
        }
    }
}
