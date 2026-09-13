package com.sdm3.parent

actual fun getPlatformName(): String = "Android"

// Semua build menunjuk ke server produksi (domain publik HTTPS).
// Untuk pengembangan lokal, ganti sementara ke "http://10.0.2.2:8000" (emulator)
// atau pakai `adb reverse tcp:8000 tcp:8000` dengan "http://127.0.0.1:8000".
actual fun defaultBaseUrl(): String = "https://app.sdmuhammadiyah3smd.com"

actual fun isDebugBuild(): Boolean {
    return try {
        Class.forName("com.sdm3.parent.BuildConfig")
            .getField("DEBUG")
            .getBoolean(null)
    } catch (_: Exception) {
        try {
            Class.forName("com.sdm3.parent.shared.BuildConfig")
                .getField("DEBUG")
                .getBoolean(null)
        } catch (_: Exception) {
            false
        }
    }
}
