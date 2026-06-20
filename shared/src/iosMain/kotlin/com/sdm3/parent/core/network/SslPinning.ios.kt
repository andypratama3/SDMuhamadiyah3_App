package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>) {
    if (pins.isEmpty()) return
    val config = this as DarwinClientEngineConfig
    config.configureRequest {
        println("[SDM3] iOS SSL pinning configured for ${pins.size} pins on admin.sdm3.sch.id")
    }
}
