package com.sdm3.parent.core.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AndroidBiometricProvider {
    @Volatile
    var activity: FragmentActivity? = null
}

actual class BiometricAuthenticator {
    actual suspend fun authenticate(reason: String): BiometricResult {
        val activity = AndroidBiometricProvider.activity
            ?: return BiometricResult.NotAvailable

        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return BiometricResult.NotAvailable
            else -> return BiometricResult.NotAvailable
        }

        return suspendCancellableCoroutine { continuation ->
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(reason)
                .setSubtitle("Verifikasi identitas Anda")
                .setNegativeButtonText("Batal")
                .build()

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) {
                        continuation.resume(BiometricResult.Success)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (continuation.isActive) {
                        continuation.resume(BiometricResult.Error(errString.toString()))
                    }
                }

                override fun onAuthenticationFailed() {
                }
            }

            BiometricPrompt(activity, callback).authenticate(promptInfo)
        }
    }
}
