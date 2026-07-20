package com.sdm3.parent.core.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual fun HttpClientEngineConfig.applyPlatformSslPinning(pins: List<String>) {
    // SSL pinning on iOS requires NSAppTransportSecurity configuration in Info.plist.
    // For production, ensure Info.plist contains:
    // <key>NSAppTransportSecurity</key>
    // <dict>
    //     <key>NSAllowsArbitraryLoads</key>
    //     <false/>
    //     <key>NSExceptionDomains</key>
    //     <dict>
    //         <key>sdmuhammadiyah3smd.cloud</key>
    //         <dict>
    //             <key>NSExceptionAllowsInsecureHTTPLoads</key>
    //             <false/>
    //             <key>NSExceptionMinimumTLSVersion</key>
    //             <string>TLSv1.2</string>
    //             <key>NSExceptionRequiresForwardSecrecy</key>
    //             <true/>
    //         </dict>
    //     </dict>
    // </dict>
    //
    // For certificate pinning, implement a custom URLSession delegate with
    // URLAuthenticationChallenge handling to validate server certificates.
    // This requires native Swift/Objective-C implementation.
}
