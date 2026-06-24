package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>) {
    // SSL pinning is not supported on Darwin natively via Ktor config.
    // Consider using NSAppTransportSecurity in Info.plist for production.
}
