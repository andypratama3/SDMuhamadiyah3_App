# SDM3 Parent Portal

**Portal Orang Tua Siswa SD Muhammadiyah 3 Samarinda**

Aplikasi mobile cross-platform (Android & iOS) untuk wali murid memantau perkembangan akademik, pembayaran SPP, kehadiran, dan informasi sekolah secara real-time.

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.4.0 |
| UI Framework | Compose Multiplatform | 1.11.1 |
| Material Design | Material 3 | 1.11.0-alpha07 |
| Navigation | Navigation Compose | 2.9.2 |
| DI | Koin | 4.2.1 |
| HTTP Client | Ktor | 3.1.2 |
| Serialization | Kotlinx Serialization | 1.8.0 |
| Database (local) | SQLDelight | 2.3.2 |
| Secure Storage | KVault | 1.12.0 |
| Image Loading | Coil | 3.1.0 |
| Date/Time | Kotlinx Datetime | 0.8.0 |
| Logging | Napier | 2.7.1 |
| Build System | Gradle + AGP | 9.0.1 |

---

## Architecture

**Clean Architecture + MVVM** pattern:

```
UI (Compose Screen)
    ↕ UiState / Events
ViewModel (StateFlow<UiState>)
    ↕ ApiResult<T>
Repository (data layer)
    ↕ DTO
Remote DataSource (Ktor → Laravel REST API)
Local DataSource (SQLDelight cache)
```

### Layers

| Layer | Package | Purpose |
|-------|---------|---------|
| **UI** | `feature/*/ui/` | Compose screens, no business logic |
| **ViewModel** | `feature/*/` | State management via `BaseViewModel` |
| **Domain** | `domain/entity/` | Business entities |
| **Data** | `data/repository/` | Repository pattern, error handling |
| **Remote** | `data/remote/api/` | Ktor API service classes |
| **DTO** | `data/remote/dto/` | JSON response/request models |
| **Core** | `core/` | DI, navigation, network, security, design system |

---

## Project Structure

```
androidApp/
└── src/main/kotlin/com/example/sdmuhammadiyah3samarinda/
    └── MainActivity.kt              # Android entry point, Koin init

shared/
└── src/
    ├── commonMain/kotlin/com/sdm3/parent/
    │   ├── App.kt                              # Root composable
    │   ├── Platform.kt                         # Platform expect declarations
    │   │
    │   ├── core/
    │   │   ├── base/
    │   │   │   ├── BaseViewModel.kt            # Abstract VM with StateFlow + launchSafely
    │   │   │   └── ScreenState.kt              # UI state marker interface
    │   │   │
    │   │   ├── designsystem/
    │   │   │   ├── component/
    │   │   │   │   ├── IconText.kt              # Row with Icon + Text helper
    │   │   │   │   ├── SDM3BottomNavBar.kt     # Bottom nav with 5 tabs
    │   │   │   │   ├── Sdm3Button.kt           # Primary/secondary/outline buttons
    │   │   │   │   ├── Sdm3Card.kt             # Reusable card component
    │   │   │   │   ├── Sdm3TextField.kt        # Styled text input field
    │   │   │   │   └── StatusChip.kt           # Status badge (success/warning/etc)
    │   │   │   └── theme/
    │   │   │       ├── Color.kt                # SDM3 color palette
    │   │   │       ├── SDM3Theme.kt            # Material 3 theme wrapper
    │   │   │       ├── Shape.kt                # Shape tokens
    │   │   │       ├── Spacing.kt              # Spacing constants
    │   │   │       └── Typography.kt           # Type scale definitions
    │   │   │
    │   │   ├── di/
    │   │   │   ├── AppModule.kt                # Koin modules (network, api, repo, VM, security)
    │   │   │   ├── KoinModules.kt              # Aggregates all modules
    │   │   │   └── PlatformModule.kt           # expect fun platformModule()
    │   │   │
    │   │   ├── navigation/
    │   │   │   ├── SDM3NavHost.kt              # NavHost with all routes + bottom nav
    │   │   │   └── SDM3Route.kt                # Route sealed class + BottomTab enum
    │   │   │
    │   │   ├── network/
    │   │   │   ├── ApiResult.kt                # Sealed class Success/Error + ApiError types
    │   │   │   ├── HttpClientProvider.kt       # Ktor HttpClient with auth + session handling
    │   │   │   └── SslPinning.kt               # expect SSL pinning config
    │   │   │
    │   │   ├── permission/
    │   │   │   └── NotificationPermission.kt   # Push notification permission handler
    │   │   │
    │   │   └── security/
    │   │       ├── BiometricAuth.kt            # expect BiometricAuthenticator
    │   │       ├── CertificateProvider.kt      # SSL pinning certificate hashes
    │   │       └── SecureTokenManager.kt       # KVault-backed encrypted storage
    │   │
    │   ├── data/
    │   │   ├── remote/
    │   │   │   ├── api/
    │   │   │   │   ├── ArticleApi.kt           # Pengumuman/announcements API
    │   │   │   │   ├── AttendanceApi.kt        # Kehadiran API
    │   │   │   │   ├── AuthApi.kt              # Login/register API
    │   │   │   │   ├── DashboardApi.kt         # Home dashboard API
    │   │   │   │   ├── Endpoints.kt            # All API route constants
    │   │   │   │   ├── ExtracurricularApi.kt   # Ekstrakurikuler API
    │   │   │   │   ├── GradeApi.kt             # Nilai + komponen API
    │   │   │   │   ├── NotificationApi.kt      # Notifikasi API
    │   │   │   │   ├── PaymentApi.kt           # Pembayaran + Midtrans API
    │   │   │   │   ├── ProfileApi.kt           # Profil API
    │   │   │   │   ├── RaporApi.kt             # Rapor PDF API
    │   │   │   │   └── StudentApi.kt           # Data siswa API
    │   │   │   └── dto/
    │   │   │       ├── ApiResponse.kt          # Generic wrapper { success, data, message }
    │   │   │       ├── ArticleDto.kt           # Announcement DTO
    │   │   │       ├── AttendanceDto.kt        # Attendance + summary DTO
    │   │   │       ├── AuthDto.kt              # Login request/response + UserDto
    │   │   │       ├── DashboardDto.kt         # Dashboard aggregate DTO
    │   │   │       ├── ExtracurricularDto.kt   # Extracurricular DTO
    │   │   │       ├── GradeDto.kt             # Grade + component DTOs
    │   │   │       ├── NotificationDto.kt      # Notification DTO
    │   │   │       ├── PaymentDto.kt           # Payment + fee + snap DTOs
    │   │   │       ├── ProfileDto.kt           # Parent profile DTO
    │   │   │       ├── RaporDto.kt             # Rapor instance + verify DTOs
    │   │   │       └── StudentDto.kt           # Student DTO
    │   │   └── repository/
    │   │       ├── ArticleRepository.kt
    │   │       ├── AttendanceRepository.kt
    │   │       ├── AuthRepository.kt
    │   │       ├── DashboardRepository.kt
    │   │       ├── ExtracurricularRepository.kt
    │   │       ├── GradeRepository.kt
    │   │       ├── NotificationRepository.kt
    │   │       ├── PaymentRepository.kt
    │   │       ├── ProfileRepository.kt
    │   │       ├── RaporRepository.kt
    │   │       └── StudentRepository.kt
    │   │
    │   ├── domain/entity/
    │   │   ├── Attendance.kt           # Domain attendance model
    │   │   ├── Grade.kt                # Domain grade model
    │   │   ├── Notification.kt         # Domain notification model
    │   │   ├── Payment.kt              # Domain payment model
    │   │   └── Student.kt              # Domain student model
    │   │
    │   └── feature/
    │       ├── auth/
    │       │   ├── LoginViewModel.kt
    │       │   ├── PilihAnakViewModel.kt
    │       │   └── ui/
    │       │       ├── AccountDeletionScreen.kt
    │       │       ├── LoginScreen.kt
    │       │       ├── OnboardingScreen.kt
    │       │       ├── PilihAnakScreen.kt
    │       │       └── SplashScreen.kt
    │       ├── home/
    │       │   ├── HomeScreen.kt
    │       │   └── HomeViewModel.kt
    │       ├── infoanak/
    │       │   ├── DetailInfoAnakViewModel.kt
    │       │   └── ui/
    │       │       ├── DetailInfoAnakScreen.kt
    │       │       └── KegiatanProgramScreen.kt
    │       ├── kegiatan/
    │       │   └── KegiatanProgramViewModel.kt
    │       ├── kehadiran/
    │       │   ├── KehadiranSiswaViewModel.kt
    │       │   └── ui/KehadiranSiswaScreen.kt
    │       ├── nilai/
    │       │   ├── DetailNilaiMapelViewModel.kt
    │       │   ├── NilaiRaporViewModel.kt
    │       │   └── ui/
    │       │       ├── DetailNilaiMapelScreen.kt
    │       │       ├── HalamanRaporScreen.kt
    │       │       └── NilaiRaporScreen.kt
    │       ├── notifikasi/
    │       │   ├── NotifikasiViewModel.kt
    │       │   ├── PengumumanSekolahViewModel.kt
    │       │   └── ui/
    │       │       ├── NotifikasiScreen.kt
    │       │       └── PengumumanSekolahScreen.kt
    │       ├── pembayaran/
    │       │   ├── DetailBuktiBayarViewModel.kt
    │       │   ├── PembayaranBerhasilViewModel.kt
    │       │   ├── PembayaranSppViewModel.kt
    │       │   ├── PilihMetodeBayarViewModel.kt
    │       │   ├── ProsesPembayaranViewModel.kt
    │       │   └── ui/
    │       │       ├── DetailBuktiBayarScreen.kt
    │       │       ├── PembayaranBerhasilScreen.kt
    │       │       ├── PembayaranSppScreen.kt
    │       │       ├── PilihMetodeBayarScreen.kt
    │       │       └── ProsesPembayaranScreen.kt
    │       ├── profil/
    │       │   ├── PengaturanNotifikasiViewModel.kt
    │       │   ├── ProfilAkunViewModel.kt
    │       │   └── ui/
    │       │       ├── PengaturanNotifikasiScreen.kt
    │       │       └── ProfilAkunScreen.kt
    │       └── rapor/
    │           └── HalamanRaporViewModel.kt
    │
    ├── androidMain/kotlin/com/sdm3/parent/
    │   ├── Platform.android.kt
    │   └── core/
    │       ├── di/PlatformModule.android.kt       # KVault with Android Context
    │       ├── network/SslPinning.android.kt       # OkHttp CertificatePinner
    │       └── security/BiometricAuth.android.kt   # Android BiometricPrompt
    │
    └── iosMain/kotlin/com/sdm3/parent/
        ├── Platform.ios.kt
        └── core/
            ├── di/PlatformModule.ios.kt           # KVault with Keychain
            ├── network/SslPinning.ios.kt          # Darwin SSL config
            └── security/BiometricAuth.ios.kt      # LocalAuthentication

iosApp/                              # Xcode project (iOS entry point)
gradle/                             # Gradle configuration
docs/
    └── OWASP_CHECKLIST.md           # OWASP Mobile Top 10 compliance
.agents/
    └── prompt.md                    # Master blueprint & progress tracker
```

---

## Features & Screens

### Auth (Phase 1)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| SplashScreen | - | - | ✅ |
| OnboardingScreen | - | - | ✅ |
| LoginScreen | LoginViewModel | AuthApi | ✅ |
| VerifikasiOtpScreen | VerifikasiOtpViewModel | AuthApi | ✅ |
| PilihAnakScreen | PilihAnakViewModel | StudentApi | ✅ |

### Dashboard (Phase 2)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| HomeScreen | HomeViewModel | DashboardApi | ✅ |
| BottomNavBar | - | - | ✅ |

### Nilai & Rapor (Phase 3)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| NilaiRaporScreen | NilaiRaporViewModel | GradeApi | ✅ |
| DetailNilaiMapelScreen | DetailNilaiMapelViewModel | GradeApi | ✅ |
| HalamanRaporScreen | HalamanRaporViewModel | RaporApi | ✅ |
| PreviewRaporPdfScreen | PreviewRaporPdfViewModel | RaporApi | ✅ |
| VerifikasiQrRaporScreen | VerifikasiQrRaporViewModel | RaporApi | ✅ |

### Pembayaran (Phase 4)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| PembayaranSppScreen | PembayaranSppViewModel | PaymentApi | ✅ |
| PilihMetodeBayarScreen | PilihMetodeBayarViewModel | PaymentApi | ✅ |
| ProsesPembayaranScreen | ProsesPembayaranViewModel | PaymentApi | ✅ |
| PembayaranBerhasilScreen | PembayaranBerhasilViewModel | - | ✅ |
| DetailBuktiBayarScreen | DetailBuktiBayarViewModel | PaymentApi | ✅ |

### Kehadiran (Phase 5)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| KehadiranSiswaScreen | KehadiranSiswaViewModel | AttendanceApi | ✅ |

### Info Anak (Phase 6)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| DetailInfoAnakScreen | DetailInfoAnakViewModel | StudentApi | ✅ |
| KegiatanProgramScreen | KegiatanProgramViewModel | ExtracurricularApi | ✅ |

### Notifikasi (Phase 7)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| NotifikasiScreen | NotifikasiViewModel | NotificationApi | ✅ |
| PengumumanSekolahScreen | PengumumanSekolahViewModel | ArticleApi | ✅ |
| FCM Push (background) | FcmTokenProvider | Firebase | ✅ |

### Profil (Phase 8)
| Screen | ViewModel | API | Status |
|--------|-----------|-----|--------|
| ProfilAkunScreen | ProfilAkunViewModel | ProfileApi | ✅ |
| PengaturanNotifikasiScreen | PengaturanNotifikasiViewModel | - | ✅ |

---

## API Endpoints

Base URL: `https://admin.sdm3.sch.id`

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/sanctum/token` | Login (email + password + device_name) |
| GET | `/api/user` | Get authenticated user |
| POST | `/login` | SPA login |
| POST | `/logout` | Logout |
| GET | `/sanctum/csrf-cookie` | CSRF cookie |

### Parent Portal (Sanctum Auth)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/parent/students` | Anak-anak |
| GET | `/api/parent/students/{id}` | Detail siswa |
| GET | `/api/parent/dashboard` | Ringkasan dashboard |
| GET | `/api/parent/grades` | Nilai |
| GET | `/api/parent/grade-components` | Komponen nilai per-TP |
| GET | `/api/parent/attendances` | Kehadiran |
| GET | `/api/parent/attendance-summary` | Rekap kehadiran |
| GET | `/api/parent/student-fees` | Tagihan aktif |
| GET | `/api/parent/payments` | Transaksi pembayaran |
| GET | `/api/parent/notifications` | Notifikasi |
| PATCH | `/api/parent/notifications/{id}/read` | Mark as read |
| GET | `/api/parent/articles` | Pengumuman |
| GET | `/api/parent/rapor-instances` | Rapor |
| GET | `/api/parent/rapor/{id}/download` | Download PDF |
| POST | `/api/parent/rapor/verify` | Verifikasi QR |
| GET | `/api/parent/extracurriculars` | Ekstrakurikuler |
| GET | `/api/parent/profile` | Profil orang tua |
| PUT | `/api/parent/profile` | Update profil |
| GET | `/api/parent/forgot-password` | Lupa password |
| POST | `/api/parent/verify-otp` | Verifikasi OTP |
| POST | `/api/parent/reset-password` | Reset password |

### Midtrans
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/dashboard/midtrans/snap-token/{payment}` | Get Snap token |
| GET | `/dashboard/midtrans/status/{chargeId}` | Check status |

---

## Security

### Encrypted Storage (P9-T2)
- **KVault** (AES-256 via Android Keystore / iOS Keychain)
- `SecureTokenManager` manages: bearer token, selected student ID, FCM token, biometric preference
- Token disediakan via `HttpClientProvider.tokenProvider` untuk auth header

### SSL Pinning (P9-T1)
- `CertificatePins` object with SHA-256 certificate hashes
- Android: OkHttp `CertificatePinner`
- iOS: Darwin engine configuration
- **Production:** Add actual hashes from `admin.sdm3.sch.id` before release

### Biometric Auth (P9-T3)
- `BiometricAuthenticator` expect/actual
- Android: `BiometricPrompt` with `BiometricManager`
- iOS: `LAContext` with `LocalAuthentication`

### Session Management (P9-T4)
- Bearer token stored in KVault
- Auto-clear on 419 Session Expired
- `onSessionExpired` callback triggers `SecureTokenManager.clearAllSecureData()`

---

## Setup & Build

### Prerequisites
- Android Studio Ladybug or later
- JDK 17+
- Android SDK 36
- Xcode 16+ (for iOS)

### Android
```bash
./gradlew :androidApp:assembleDebug
```
APK output: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`

### iOS
```bash
./gradlew :shared:linkDebugFrameworkIosArm64
```
Then open `iosApp/` in Xcode and build.

### Common checks
```bash
./gradlew :shared:compileAndroidMain          # Check common + Android code
./gradlew :shared:compileKotlinIosArm64       # Check iOS code
```

---

## Dependency Injection

Koin modules registered in `AppModule.kt`:

| Module | Contents |
|--------|----------|
| `platformModule` | Platform-specific (KVault on Android/iOS) |
| `securityModule` | SecureTokenManager |
| `networkModule` | HttpClientProvider (with token + SSL) |
| `apiModule` | 11 API service classes |
| `repositoryModule` | 11 Repository classes |
| `viewModelModule` | 22 ViewModels (Auth: 3, Home: 1, Nilai: 2, Rapor: 3, Pembayaran: 5, Notifikasi: 2, Profil: 2, Info Anak: 1, Kegiatan: 1, Kehadiran: 1, Security: 1) |

**Android init:** `MainActivity.kt` uses `KoinApplication { androidContext(this); modules(allAppModules) }`
**iOS init:** Platform-specific (via `Platform.kt` expect/actual)

---

## Data Flow

```
User Action → ViewModel.method()
                ↓
           launchSafely { repository.method() }
                ↓
           ApiResult.Success(data) → updateState { copy(data = data) }
           ApiResult.Error(error)  → updateState { copy(error = error.toUserMessage()) }
                ↓
           UiState StateFlow → Screen recomposes
```

### ApiResult sealed class
- `Success<T>(data: T)`
- `Error(error: ApiError)` where ApiError includes:
  - `NoInternet`, `Timeout`, `Unauthorized`, `Forbidden`, `NotFound`
  - `SessionExpired`, `Validation`, `RateLimited`, `ServerError`, `Unknown`

---

## Status

```
Phase 0 — Foundation    80% (4/5) ✅
Phase 1 — Auth         100% (5/5) ✅
Phase 2 — Dashboard    100% (3/3) ✅
Phase 3 — Nilai/Rapor  100% (6/6) ✅
Phase 4 — Pembayaran    83% (5/6) ✅  (Webhook backend)
Phase 5 — Kehadiran    100% (1/1) ✅
Phase 6 — Info Anak    100% (2/2) ✅
Phase 7 — Notifikasi   100% (3/3) ✅
Phase 8 — Profil       100% (3/3) ✅
Phase 9 — Security     100% (5/5) ✅
Phase 10 — Testing      20% (1/5) 🔄
Phase 11 — CI/CD        25% (1/4) 🔄
───────────────────────────────────
TOTAL                   81% (39/48)
```

### Remaining Items
- P0-T2: Laravel API endpoint setup (backend)
- P4-T4: Midtrans webhook handler (backend)
- P10-T2..T5: Testing (UI, Performance, Device Matrix, Accessibility)
- P11-T2..T4: CI/CD (Signing, Play Store, Environments)

---

## Design System

### Colors (SDM3 Brand)
| Token | Hex | Usage |
|-------|-----|-------|
| Primary | `#006B31` | School green, primary actions |
| Secondary | `#006B6B` | Secondary elements |
| StatusSuccess | `#4CAF50` | Lunas, hadir |
| StatusWarning | `#FF9800` | Pending |
| StatusError | `#F44336` | Error states |
| SurfaceWhite | `#FFFBFE` | Card backgrounds |
| OnSurfaceVariant | `#49454F` | Body text |

### Typography
- Headlines: `FontWeight.Bold`, 24sp / 20sp
- Titles: `FontWeight.SemiBold`, 18sp / 16sp
- Body: `FontWeight.Normal`, 14sp / 16sp
- Labels: `FontWeight.Medium`, 12sp / 14sp
- Font: System default (Material 3 dynamic)

### Components
- `Sdm3Button` — Primary (filled), Secondary (outlined), loading state
- `Sdm3TextField` — Material 3 outlined, with error state
- `Sdm3Card` — Elevated card with configurable padding
- `StatusChip` — Colored chip for status indicators
- `SDM3BottomNavBar` — 5-tab bottom navigation

---

## Key Decisions

### Material Icons
All icons use `material-icons-core` + `material-icons-extended` from JetBrains Compose Multiplatform (`org.jetbrains.compose.material:material-icons-core`, `org.jetbrains.compose.material:material-icons-extended`). Replaced all emoji with proper `Icon(Icons.Default.*)` composable calls across 15 screens (68+ icon usages).

### KoinApplication (Android)
Uses `KoinApplication(config = koinConfiguration { androidContext() ... })` pattern (Koin 4.2.1 API, avoids deprecated lambda overload).

### Compiler Flags
`-Xexpect-actual-classes` added to suppress Beta warnings for expect/actual classes (`BiometricAuth`, `FcmTokenProvider`).

### No OTP Screen
Deferred to Phase 9 Security. OTP-based forgot password flow not yet implemented.

### Bearer Token Auth
Uses Laravel Sanctum token auth (`POST /api/sanctum/token` → Bearer token). SPA cookie auth deferred.

### KVault
Android needs `Context` constructor param. Provided via `platformModule()` expect/actual with Koin `androidContext()`.

---

## Gradle Configuration

Key versions in `gradle/libs.versions.toml`:

```toml
kotlin = "2.4.0"
compose-multiplatform = "1.11.1"
agp = "9.0.1"
androidx-lifecycle = "2.11.0-beta01"
koin = "4.2.1"
ktor = "3.1.2"
sqldelight = "2.3.2"
kvault = "1.12.0"
coil = "3.1.0"
```

SDK versions: `compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`.

---

## File Count

| Category | Files |
|----------|-------|
| Core UI/Screens | 33 |
| ViewModels | 22 |
| API Services | 11 |
| DTOs | 13 |
| Repositories | 11 |
| Security | 5 |
| Notification | 3 |
| DI | 4 |
| Platform (androidMain) | 4 |
| Platform (iosMain) | 4 |
| Navigation | 2 |
| Design System | 10 |
| Domain Entities | 5 |
| Tests | 5 |
| CI/CD | 2 |
| Config/Docs | 5+ |
| **Total Kotlin** | **~130 files** |
