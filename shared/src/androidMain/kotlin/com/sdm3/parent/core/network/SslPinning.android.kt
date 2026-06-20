package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.CertificatePinner

actual fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>) {
    if (pins.isEmpty()) return
    val config = this as OkHttpConfig
    val pinner = CertificatePinner.Builder()
    pins.forEach { pin ->
        pinner.add("admin.sdm3.sch.id", pin)
    }
    val existing = config.preconfigured
    if (existing != null) {
        config.preconfigured = existing.newBuilder()
            .certificatePinner(pinner.build())
            .build()
    }
}
