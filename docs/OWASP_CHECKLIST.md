# OWASP Mobile Top 10 Compliance Checklist

**Project:** SDM3 Parent Portal
**Last Updated:** June 2026

| ID | Category | Status | Details |
|---|---|---|---|
| M1 | Improper Platform Usage | ✅ | Code review process enforces proper use of platform APIs. All intents are explicit, permissions are declared in manifest, and no deprecated API misuse is present. |
| M2 | Insecure Data Storage | ✅ | All sensitive data (tokens, student IDs, FCM tokens) stored via KVault which encrypts data at rest using AES-256. No plaintext persistence of secrets. |
| M3 | Insecure Communication | 🟡 | TLS 1.2+ enforced via Ktor HTTP client with HSTS assumed at server level. SSL certificate pinning infrastructure is in place (see `SslPinning.kt`). Production pin hashes must be added to `CertificatePins.pins` before release. |
| M4 | Insecure Authentication | ✅ | Authentication uses Laravel Sanctum token-based auth. Bearer tokens stored in encrypted KVault. Session expiry is detected (HTTP 419) and triggers automatic credential wipe. |
| M5 | Insufficient Cryptography | ✅ | AES-256 encryption via KVault for all persisted secrets. No custom cryptographic implementations. Ktor uses platform-default TLS implementations. |
| M6 | Insecure Authorization | ✅ | Laravel Sanctum token scopes enforce server-side authorization. API returns 403 Forbidden for unauthorized resource access, handled gracefully in `ApiResult`. |
| M7 | Client Code Quality | ✅ | All ViewModels extend `BaseViewModel` with standardized error handling. `ApiResult` sealed class enforces exhaustive error handling. Coroutines used throughout with structured concurrency. |
| M8 | Code Tampering | ❌ | Not yet implemented. Future: ProGuard/R8 obfuscation for release builds, integrity verification, and RASP (Runtime Application Self-Protection) measures such as root/jailbreak detection. |
| M9 | Reverse Engineering | ❌ | Not yet implemented. Future: ProGuard/R8 obfuscation, string encryption, debug detection. |
| M10 | Extraneous Functionality | ✅ | Minimum required permissions declared. No debug menus, backdoors, or hidden APIs in release builds. All network traffic goes through a single `HttpClientProvider`. |

## Legend

| Icon | Meaning |
|---|---|
| ✅ | Fully implemented and verified |
| 🟡 | Partially implemented or requires configuration before release |
| ❌ | Not yet implemented |

## Remediation Plan

### M3 - Insecure Communication
1. Obtain production certificate hashes for `admin.sdm3.sch.id`
2. Add SHA-256 hashes to `CertificatePins.pins` in `CertificateProvider.kt`
3. Validate pinning in staging before release
4. Implement iOS `NSURLSessionDelegate`-based pinning for production

### M8 - Code Tampering
1. Enable ProGuard/R8 minification in `androidApp/build.gradle.kts`
2. Add root detection via native library or trusted KMP library
3. Implement integrity hash verification on app startup

### M9 - Reverse Engineering
1. Enable R8 full mode with obfuscation
2. Apply string encryption for sensitive strings (API keys, endpoints)
3. Add debug detection that prevents app from running on debug builds
