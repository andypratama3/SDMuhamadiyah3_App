package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import io.ktor.client.engine.darwin.certificates.CertificatePinner

actual fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>) {
    if (pins.isEmpty()) return
    val config = this as DarwinClientEngineConfig
    val builder = CertificatePinner.Builder()
    pins.forEach { pin ->
        builder.add("app.sdmuhammadiyah3smd.com", pin)
    }
    config.handleChallenge(builder.build())
}