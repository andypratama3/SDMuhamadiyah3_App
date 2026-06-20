package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig

expect fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>)
