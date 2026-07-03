package com.sdm3.parent

actual fun getPlatformName(): String = "Android"
actual fun defaultBaseUrl(): String = "https://admin.sdm3.sch.id"

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
