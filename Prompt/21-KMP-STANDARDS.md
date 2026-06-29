# 21 — Kotlin Multiplatform Standards

## Module Boundaries

```
shared/
└── src/
    ├── commonMain/      ← ~90% of code lives here: UI, ViewModels, domain, data, DI wiring
    ├── androidMain/     ← platform actuals only: Context-dependent APIs, Android-specific drivers
    ├── iosMain/         ← platform actuals only: iOS-specific drivers, interop
    ├── desktopMain/      ← platform actuals only (if/when desktop target is added formally)
    ├── commonTest/      ← shared unit tests for domain/business logic
    ├── androidHostTest/ ← Android-specific instrumented/unit tests
    └── iosTest/         ← iOS-specific tests
androidApp/    ← thin shell: Application/Activity, manifest, platform wiring only
iosApp/        ← thin shell: SwiftUI/UIKit entry point hosting the Compose UIViewController
```

**Rule:** if code does not *require* a platform API, it belongs in `commonMain`. `androidApp`/`iosApp`
should contain almost no logic — they host the shared UI and wire platform-specific bootstrapping
(push notification registration, deep link handling at the OS level) and nothing else.

## `expect`/`actual` Usage

Use `expect`/`actual` only for genuine platform divergence: secure storage, biometric auth, file
picker, haptics (`10-HAPTICS.md`), backdrop blur capability (`08-GLASSMORPHISM.md`), date/time
locale APIs not covered by `kotlinx-datetime`. Prefer a common interface with platform
implementations injected via DI over scattering `expect fun` across many files — keep the platform
surface small and centralized in clearly named files (`PlatformHaptics.kt`, `PlatformStorage.kt`).

```kotlin
// commonMain
interface SecureStorage {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
}
expect fun provideSecureStorage(): SecureStorage
```

## Dependency Rules

| Dependency | Where it's allowed |
|---|---|
| Ktor Client | `commonMain` (data layer only) |
| SQLDelight | `commonMain` (data layer only) — driver provided via `expect/actual` |
| Compose Multiplatform / Material 3 | `commonMain` (presentation layer) |
| kotlinx.serialization, kotlinx.datetime, kotlinx.coroutines | `commonMain`, everywhere |
| Android-only libs (e.g. WorkManager) | `androidMain` only, behind a common interface |

## Offline-First Data Strategy

SQLDelight is the **single source of truth for the UI.** The pattern for every feature:

```
Network (Ktor) → Repository → writes to SQLDelight → UI observes SQLDelight (Flow) → Compose
```

UI never reads network responses directly — it always reads through the local database as a
reactive `Flow<List<T>>`, so the app remains usable (read-only) offline and updates reactively the
moment a sync completes. See `23-CLEAN-ARCHITECTURE.md` for the full repository contract.

## Coroutines & Dispatchers

- All suspend functions in the data/domain layer are dispatcher-agnostic; the call site (ViewModel)
  decides context via a shared `Dispatchers.Default`/`Dispatchers.IO`-equivalent injected
  `CoroutineDispatcher` set (KMP-safe, since `Dispatchers.IO` isn't available identically on all
  targets — define `EduOctoDispatchers` as an injectable holder).
- ViewModels use `viewModelScope` (from Compose Multiplatform's lifecycle-viewmodel KMP artifact),
  never `GlobalScope`.

## Build & Versioning

- Kotlin, Compose Multiplatform, and Ktor versions are pinned in `gradle/libs.versions.toml`
  (version catalog) — no inline version strings in module `build.gradle.kts` files.
- Every shared module change must build for **all three active targets** (Android, iOS, Desktop)
  before being considered complete — an agent must not claim a feature is "done" having only
  verified the Android target.

## Anti-Patterns

- Platform-specific business logic (e.g. a fee calculation rule different "for Android") — business
  rules are defined once in `commonMain`, no exceptions.
- Importing `android.*` types into any file under `commonMain`.
- Using `java.util.Date`/`Calendar` anywhere — use `kotlinx-datetime` exclusively for KMP safety.
