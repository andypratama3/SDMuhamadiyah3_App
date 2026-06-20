# SDM3 PARENT PORTAL — MASTER DEVELOPMENT BLUEPRINT
### Kotlin Multiplatform · Compose Multiplatform · Laravel REST API · Midtrans · Clean Architecture 2026

---

> **Dokumen ini adalah satu-satunya sumber kebenaran (Single Source of Truth)** untuk seluruh tim pengembangan SDM3 Parent Portal. Setiap agent, role, dan kontributor **WAJIB** merujuk dokumen ini sebelum menulis satu baris kode pun.

---

## DAFTAR ISI

1. [Project Overview & Vision](#1-project-overview--vision)
2. [Team Agents & Roles](#2-team-agents--roles)
3. [Design System Reference](#3-design-system-reference)
4. [Screen Inventory & Component Map](#4-screen-inventory--component-map)
5. [Architecture Blueprint](#5-architecture-blueprint)
6. [Data Models & API Contract](#6-data-models--api-contract)
7. [Phase Roadmap](#7-phase-roadmap)
8. [Phase 0 — Foundation & Setup](#phase-0--foundation--setup)
9. [Phase 1 — Auth & Onboarding](#phase-1--auth--onboarding)
10. [Phase 2 — Dashboard & Core Navigation](#phase-2--dashboard--core-navigation)
11. [Phase 3 — Nilai & Rapor Module](#phase-3--nilai--rapor-module)
12. [Phase 4 — Pembayaran SPP Module](#phase-4--pembayaran-spp-module)
13. [Phase 5 — Kehadiran Module](#phase-5--kehadiran-module)
14. [Phase 6 — Info Anak & Kegiatan Module](#phase-6--info-anak--kegiatan-module)
15. [Phase 7 — Notifikasi & Pengumuman Module](#phase-7--notifikasi--pengumuman-module)
16. [Phase 8 — Profil & Pengaturan Module](#phase-8--profil--pengaturan-module)
17. [Phase 9 — Security Hardening](#phase-9--security-hardening)
18. [Phase 10 — Testing & QA](#phase-10--testing--qa)
19. [Phase 11 — CI/CD & Deployment](#phase-11--cicd--deployment)
20. [Progress Tracker Master](#20-progress-tracker-master)
21. [Global Quality & Security Rules](#21-global-quality--security-rules)
22. [Master Prompt untuk Setiap Agent](#22-master-prompt-untuk-setiap-agent)

---

## 1. PROJECT OVERVIEW & VISION

### Identitas Proyek

| Field | Detail |
|---|---|
| **Nama Aplikasi** | SDM3 Parent Portal |
| **Target Platform** | Android (Primary), iOS (Secondary)|
| **Stack Utama** | Kotlin Multiplatform + Compose Multiplatform |
| **Backend** | Laravel REST API (Sanctum Auth + MySQL + File Storage + Pusher/Reverb Broadcasting) |
| **Payment** | Midtrans (Virtual Account, E-Wallet, QRIS via Midtrans Snap) |
| **Push Notification** | Firebase Cloud Messaging (FCM) — via Laravel notification system |
| **Realtime** | Pusher / Laravel Reverb (WebSocket broadcasting) |
| **Analytics** | Posthog |
| **Error Tracking** | Sentry |
| **School** | SD Muhammadiyah 3 Samarinda, Kalimantan Timur |

### Visi Produk

Portal orang tua berbasis mobile yang memungkinkan wali murid SD Muhammadiyah 3 Samarinda untuk memantau perkembangan akademik anak, mengelola pembayaran SPP secara digital, dan mendapatkan informasi sekolah secara real-time — dalam satu platform yang aman, cepat, dan modern.

### User Persona

**Primary User:** Orang tua / wali murid (ayah/ibu, usia 28–55 tahun)
- Melek teknologi menengah
- Menggunakan smartphone Android mid-range
- Membutuhkan informasi anak yang cepat dan dapat dipercaya
- Menghindari antrian pembayaran manual di sekolah

---

## 2. TEAM AGENTS & ROLES

Setiap agent bekerja dalam lingkup yang terdefinisi dan **tidak boleh melampaui batas domain-nya** tanpa koordinasi.

---

### 🏗️ AGENT 1 — ARCHITECT AGENT

**Tanggung Jawab:**
- Mendefinisikan dan menjaga arsitektur Clean Architecture + MVVM
- Membuat keputusan tech stack final
- Mengelola module graph (shared, androidApp, iosApp)
- Mendesain package structure
- Menjaga konsistensi dependency injection (Koin)
- Code review untuk perubahan arsitektur level

**Domain File yang Dikuasai:**
```
shared/
├── core/
│   ├── network/
│   ├── database/
│   ├── di/
│   └── util/
build.gradle.kts (root)
settings.gradle.kts
libs.versions.toml
```

**Output yang Dihasilkan:**
- `ARCHITECTURE.md` — dokumen arsitektur hidup
- Koin module definitions
- Interface contracts antar layer
- Base classes (BaseViewModel, BaseUseCase, Result wrapper)

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — output arsitektur/config tidak terpotong

**Aturan Kerja:**
- Setiap keputusan breaking change WAJIB diumumkan ke semua agent
- Tidak diizinkan mengubah feature module tanpa koordinasi Feature Agent
- Harus menjaga dependency tidak ada circular import

---

### 🎨 AGENT 2 — DESIGN SYSTEM AGENT

**Tanggung Jawab:**
- Mengimplementasikan seluruh design token dari `DESIGN.md` SDM3
- Membangun shared design system di `shared/core/designsystem/`
- Membuat semua reusable Composable component
- Menjaga konsistensi visual antar screen
- Dark mode (jika diperlukan)
- Accessibility (min touch target 48dp, screen reader)

**Domain File yang Dikuasai:**
```
shared/core/designsystem/
├── theme/
│   ├── Color.kt
│   ├── Typography.kt
│   ├── Shape.kt
│   ├── Spacing.kt
│   ├── Elevation.kt
│   └── SDM3Theme.kt
├── component/
│   ├── button/
│   ├── card/
│   ├── chip/
│   ├── input/
│   ├── navigation/
│   ├── dialog/
│   └── feedback/
```

**Output yang Dihasilkan:**
- Semua component Composable yang dapat di-preview
- Design token constants
- Component documentation dengan @Preview

**Skill Opencode yang Digunakan:**
- `stitch-design-taste` — semantic design system specialist
- `minimalist-ui` — warm monochrome, typographic contrast
- `high-end-visual-design` — premium look, bukan generic
- `design-taste-frontend` — anti-slop UI
- `imagegen-frontend-mobile` — generate referensi visual mobile

**Aturan Kerja:**
- WAJIB preview setiap component sebelum release
- Tidak boleh ada hardcoded color di luar `Color.kt`
- Setiap komponen harus punya parameter enabled, isLoading state
- Gunakan Material 3 sebagai fondasi, extend jangan override

---

### ⚡ AGENT 3 — FEATURE AGENT (Android/iOS UI)

**Tanggung Jawab:**
- Mengimplementasikan setiap screen sesuai dengan desain Stitch
- Mengintegrasikan ViewModel dengan UI
- Mengimplementasikan Navigation 3
- Menangani state UI (Loading, Success, Error, Empty)
- Animasi dan transisi antar screen

**Domain File yang Dikuasai:**
```
shared/feature/
├── auth/
│   ├── presentation/
│   │   ├── screen/
│   │   └── viewmodel/
├── home/
├── nilai/
├── pembayaran/
├── kehadiran/
├── infoanak/
├── kegiatan/
├── notifikasi/
├── rapor/
└── profil/
```

**Output yang Dihasilkan:**
- Semua screen Composable
- ViewModel per feature
- Navigation graph per module
- UI state data classes

**Skill Opencode yang Digunakan:**
- `imagegen-frontend-mobile` — generate referensi screen sebelum coding
- `full-output-enforcement` — screen code tidak terpotong
- `high-end-visual-design` — UI tidak generic
- `design-taste-frontend` — anti-slop layout

**Aturan Kerja:**
- Hanya boleh menggunakan komponen dari Design System Agent
- WAJIB implement semua state: Loading, Success, Error, Empty
- WAJIB implement pull-to-refresh pada semua list screen
- Navigation harus type-safe menggunakan Navigation 3

---

### 🗄️ AGENT 4 — BACKEND/DATA AGENT

**Tanggung Jawab:**
- Merancang dan mengelola koneksi ke Laravel REST API
- Membuat Ktor HTTP client dengan Sanctum token auth
- Membuat Repository implementations
- Mengelola SQLDelight untuk local cache
- Mengimplementasikan polling/realtime subscriptions via Pusher/Reverb

**Domain File yang Dikuasai:**
```
shared/data/
├── remote/
│   ├── api/
│   ├── dto/
│   └── datasource/
├── local/
│   ├── database/
│   └── datasource/
├── repository/
└── mapper/
```

**Output yang Dihasilkan:**
- Ktor API client implementations (Laravel REST API)
- Sanctum token management
- DTO data classes sesuai response Laravel
- SQLDelight schema (.sq files)
- Repository implementations

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — Repository/DataSource code tidak terpotong

**Aturan Kerja:**
- WAJIB handle Sanctum token expiry & refresh
- Tidak boleh simpan token di plain SharedPreferences
- Semua API response WAJIB di-wrap dengan Result<T>
- WAJIB implement offline-first dengan SQLDelight cache

---

### 💳 AGENT 5 — PAYMENT AGENT

**Tanggung Jawab:**
- Integrasi Midtrans Snap API (Virtual Account, E-Wallet, QRIS)
- Mengelola payment flow end-to-end via Laravel Midtrans API
- Menangani payment status via polling ke Laravel API
- Verifikasi dan rekonsiliasi pembayaran
- Mengelola riwayat transaksi

**Domain File yang Dikuasai:**
```
shared/feature/pembayaran/
shared/data/remote/api/PaymentApi.kt
```

**Output yang Dihasilkan:**
- Payment flow Composable screens
- Midtrans Snap WebView integration
- Payment status polling
- Receipt/bukti bayar screen

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — payment flow code tidak terpotong
- `imagegen-frontend-mobile` — referensi screen payment flow

**Aturan Kerja:**
- WAJIB validasi server-side untuk semua transaksi (via Laravel API)
- Payment logic sepenuhnya di server (Midtrans Snap token dari Laravel)
- Tidak boleh process payment logic di client
- WAJIB implement idempotency key untuk payment request

---

### 🔒 AGENT 6 — SECURITY AGENT

**Tanggung Jawab:**
- Mengimplementasikan autentikasi (OTP + Google Sign-In)
- SSL Pinning
- Encrypted local storage (KVault)
- Session management & token refresh
- Biometric authentication
- Data masking untuk data sensitif
- Security audit & penetration testing guidance
- OWASP Mobile Top 10 compliance

**Domain File yang Dikuasai:**
```
shared/core/security/
├── crypto/
├── session/
├── biometric/
└── pinning/
androidApp/src/main/res/xml/network_security_config.xml
```

**Output yang Dihasilkan:**
- Security layer implementations
- Session manager
- Encrypted preference store
- Network security config (Android)
- Security testing checklist

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — security implementation code tidak terpotong

**Aturan Kerja:**
- Tidak boleh log data sensitif (nomor HP, password, token)
- WAJIB encrypt semua data di local storage
- WAJIB implement certificate pinning production
- Session expired harus redirect ke login tanpa data leak

---

### 🧪 AGENT 7 — QA/TESTING AGENT

**Tanggung Jawab:**
- Unit test untuk semua UseCase
- Integration test untuk Repository
- UI test dengan Compose UI Test
- Mock data dan test fixtures
- Performance testing
- Accessibility testing

**Domain File yang Dikuasai:**
```
shared/src/commonTest/
androidApp/src/androidTest/
```

**Output yang Dihasilkan:**
- Test suite per feature
- Mock implementations
- Test coverage report
- Bug reports dengan reproducible steps

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — test class lengkap, tidak ada placeholder

**Aturan Kerja:**
- Minimum 80% code coverage untuk domain layer
- Setiap bug WAJIB ada regression test
- WAJIB test semua edge case (empty state, error state, no internet)

---

### 🚀 AGENT 8 — DEVOPS/CI-CD AGENT

**Tanggung Jawab:**
- GitHub Actions pipeline
- Android build & signing
- iOS build & signing (fastlane)
- Versioning strategy (semantic versioning)
- Play Store deployment
- App Store deployment
- Environment management (dev, staging, prod)
- Firebase App Distribution untuk internal testing

**Domain File yang Dikuasai:**
```
.github/workflows/
fastlane/
androidApp/src/main/
iosApp/Configuration/
```

**Skill Opencode yang Digunakan:**
- `full-output-enforcement` — YAML workflow dan konfigurasi tidak terpotong

**Output yang Dihasilkan:**
- CI/CD workflow YAML files
- Fastfile
- Signing configurations
- Release notes automation

---

## 3. DESIGN SYSTEM REFERENCE

### 3.1 Color Palette (dari DESIGN.md SDM3)

```kotlin
// Color.kt — SDM3 Design System

// Primary (School Green)
val Primary = Color(0xFF006B31)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF00873F)
val OnPrimaryContainer = Color(0xFFF7FFF3)
val InversePrimary = Color(0xFF6FDD8A)

// Secondary (Trust Blue)
val Secondary = Color(0xFF006398)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFF80C5FF)
val OnSecondaryContainer = Color(0xFF00517D)

// Tertiary (Amber — untuk tagihan)
val Tertiary = Color(0xFF805200)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFA16900)
val OnTertiaryContainer = Color(0xFFFFFBFF)
val TertiaryFixed = Color(0xFFFFDDB4)     // Background tagihan

// Surface
val Surface = Color(0xFFF8F9FB)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceContainer = Color(0xFFEDEEF0)
val SurfaceContainerLow = Color(0xFFF2F4F6)
val SurfaceContainerHigh = Color(0xFFE7E8EA)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceDim = Color(0xFFD9DADC)

// On Surface
val OnSurface = Color(0xFF191C1E)
val OnSurfaceVariant = Color(0xFF3E4A3E)
val Outline = Color(0xFF6E7A6E)
val OutlineVariant = Color(0xFFBDCABB)

// Status
val StatusSuccess = Color(0xFF22C55E)
val StatusWarning = Color(0xFFF59E0B)
val StatusDanger = Color(0xFFEF4444)
val Error = Color(0xFFBA1A1A)

// Brand Extended
val SchoolGreenDark = Color(0xFF1A4D2E)
val SchoolGreenVibrant = Color(0xFF33B962)
```

### 3.2 Typography

```kotlin
// Typography.kt
val SDM3Typography = Typography(
    // Display
    displayLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 40.sp
    ),
    // Headlines (Poppins)
    headlineLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp
    ),
    // Title
    titleLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp
    ),
    // Body (Inter)
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),
    // Label
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 3.3 Shapes

```kotlin
// Shape.kt
val SDM3Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // chips, badges
    small = RoundedCornerShape(8.dp),         // buttons, inputs
    medium = RoundedCornerShape(12.dp),       // buttons (52dp height)
    large = RoundedCornerShape(16.dp),        // cards
    extraLarge = RoundedCornerShape(24.dp),   // bottom sheet, modal
)
```

### 3.4 Spacing Scale

```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp      // margin mobile standard
    val lg = 24.dp      // margin tablet
    val xl = 32.dp
    val gutter = 16.dp
}
```

### 3.5 Component Specifications

#### Button Primary
- Height: `52.dp`
- Shape: `RoundedCornerShape(12.dp)`
- Background: `Secondary (#006398)` — digunakan untuk CTA utama
- Text: `White`, `LabelLarge`, `FontWeight.SemiBold`
- Active state: `scale(0.95)` + `opacity(0.9)`

#### Button Outline
- Height: `52.dp`
- Shape: `RoundedCornerShape(12.dp)`
- Border: `1.5.dp`, warna `Secondary`
- Background: `Transparent`
- Text: `Secondary`

#### Card Standard
- Background: `SurfaceWhite (#FFFFFF)`
- Shape: `RoundedCornerShape(16.dp)`
- Shadow: `4dp blur, 2dp Y, 5% opacity`
- Border: `1dp OutlineVariant/10`
- Padding: `16.dp` internal

#### Status Chip (Pill)
- Shape: `RoundedCornerShape(full/9999dp)`
- Hadir: `bg StatusSuccess/10`, `text StatusSuccess`
- Sakit: `bg StatusWarning/10`, `text StatusWarning`
- Izin: `bg Secondary/10`, `text Secondary`
- Alpa: `bg StatusDanger/10`, `text StatusDanger`
- Lunas: `bg StatusSuccess/10`, `text StatusSuccess`
- Belum Bayar: `bg StatusDanger/10`, `text StatusDanger`

#### Input Field
- Height: `52.dp`
- Shape: `RoundedCornerShape(12.dp)`
- Border default: `1.dp OutlineVariant`
- Border focused: `2.dp Secondary`
- Icon size: `24.dp` Material Symbols Outlined
- Padding leading: `16.dp`

#### Bottom Navigation
- Background: `Surface`
- Active item: `SecondaryContainer` pill background + filled icon
- Inactive item: `OnSurfaceVariant` + outlined icon
- Height: `56.dp` + safe area bottom
- Shape top: `RoundedCornerShape(topStart=16.dp, topEnd=16.dp)`

#### Billing Card (Amber)
- Background: `TertiaryFixed (#FFDDB4)`
- Shape: `RoundedCornerShape(16-24.dp)`
- Amount text: `Tertiary`, `displayLarge`
- Status badge: `StatusDanger/10` bg + `StatusDanger` text

---

## 4. SCREEN INVENTORY & COMPONENT MAP

Total screen yang akan diimplementasikan: **28 screens + 3 dialogs + 2 bottom sheets**

### 4.1 Auth Flow

| Screen ID | Nama Screen | File Referensi | Status Awal | Components |
|---|---|---|---|---|
| `SCR-001` | Splash Screen | `splash_screen/` | ⬜ TODO | Logo, Animation, Version |
| `SCR-002` | Onboarding 1 | `onboarding_1/` | ⬜ TODO | Illustration, Title, Pager, Button |
| `SCR-003` | Onboarding 2 | `onboarding_2/` | ⬜ TODO | Illustration, Title, Pager |
| `SCR-004` | Onboarding 3 | `onboarding_3/` | ⬜ TODO | Illustration, Title, CTA Button |
| `SCR-005` | Login Screen | `login_screen/` | ⬜ TODO | PhoneInput, PasswordInput, GoogleSSO, CTA |
| `SCR-006` | Verifikasi OTP | `verifikasi_otp/` | ⬜ TODO | OTPField x6, Countdown, Resend |
| `SCR-007` | Pilih Anak (Initial) | `pilih_anak/` | ⬜ TODO | ChildCard list, SelectButton |

### 4.2 Main Navigation (Bottom Nav)

| Screen ID | Nama Screen | File Referensi | Status Awal | Components |
|---|---|---|---|---|
| `SCR-008` | Dashboard Beranda | `dashboard_beranda/` | ⬜ TODO | TopAppBar, ChildChip, BillingCard, MenuGrid, NilaiScroll, AnnouncementList |
| `SCR-009` | Nilai & Rapor List | `nilai_rapor/` | ⬜ TODO | SubjectCard list, Tabs (Sumatif/Formatif/Projek) |
| `SCR-010` | Nilai Kosong | `nilai_rapor_kosong/` | ⬜ TODO | EmptyState illustration |
| `SCR-011` | Pembayaran SPP | `pembayaran_spp/` | ⬜ TODO | ActiveBillCard, FilterChips, PaymentHistoryList |
| `SCR-012` | Pembayaran Lunas | `pembayaran_lunas/` | ⬜ TODO | StatusCard (paid), HistoryList |
| `SCR-013` | Rapor Resmi | `halaman_rapor/` | ⬜ TODO | ReportCard, DownloadButton, QRCode |
| `SCR-014` | Profil Akun | `profil_akun/` | ⬜ TODO | ProfileHeader, ChildList, SettingsList, LogoutButton |

### 4.3 Detail Screens

| Screen ID | Nama Screen | File Referensi | Status Awal | Components |
|---|---|---|---|---|
| `SCR-015` | Detail Nilai Mapel | `detail_nilai_mapel/` | ⬜ TODO | SubjectHeader, TP List, ScoreChart |
| `SCR-016` | Tab Formatif | `tab_formatif/` | ⬜ TODO | TabBar, SubjectSelector, TPList |
| `SCR-017` | Kehadiran Siswa | `kehadiran_siswa/` | ⬜ TODO | MonthPicker, SummaryGrid, MiniCalendar, DayList |
| `SCR-018` | Detail Info Anak | `detail_info_anak/` | ⬜ TODO | ProfileCard, StatsHub, AcademicSummary |
| `SCR-019` | Kegiatan Program | `kegiatan_program/` | ⬜ TODO | TabSwitcher, EkskulCard, ProgramCard |
| `SCR-020` | Pengumuman Sekolah | `pengumuman_sekolah/` | ⬜ TODO | AnnouncementCard list, CategoryFilter |
| `SCR-021` | Notifikasi | `notifikasi/` | ⬜ TODO | FilterChips, NotifCard list, PromoCard |
| `SCR-022` | Notifikasi Kosong | `notifikasi_kosong/` | ⬜ TODO | EmptyState |

### 4.4 Payment Flow

| Screen ID | Nama Screen | File Referensi | Status Awal | Components |
|---|---|---|---|---|
| `SCR-023` | Pilih Metode Bayar | `pilih_metode_bayar/` | ⬜ TODO | BillSummary, PaymentMethodList, CTAButton |
| `SCR-024` | Proses Pembayaran | `proses_pembayaran/` | ⬜ TODO | Loading, VirtualAccount/QRDisplay, Countdown |
| `SCR-025` | Pembayaran Berhasil | `pembayaran_berhasil/` | ⬜ TODO | SuccessAnimation, SummaryCard |
| `SCR-026` | Detail Bukti Bayar | `detail_bukti_bayar/` | ⬜ TODO | Receipt, ItemizedBreakdown, StudentInfo |

### 4.5 Rapor Flow

| Screen ID | Nama Screen | File Referensi | Status Awal | Components |
|---|---|---|---|---|
| `SCR-027` | Preview Rapor PDF | `preview_rapor_pdf/` | ⬜ TODO | PDFViewer, ZoomControls, ShareButton |
| `SCR-028` | Verifikasi QR Rapor | `verifikasi_qr_rapor/` | ⬜ TODO | VerifStatus, StudentInfo, PDFButton |

### 4.6 Dialogs & Sheets

| ID | Nama | File Referensi | Status |
|---|---|---|---|
| `DLG-001` | Dialog Konfirmasi Keluar | `dialog_konfirmasi_keluar/` | ⬜ TODO |
| `DLG-002` | Error Koneksi Jaringan | `error_koneksi_jaringan/` | ⬜ TODO |
| `SHT-001` | Pilih Anak Bottom Sheet | `pilih_anak_bottom_sheet/` | ⬜ TODO |
| `SHT-002` | Pengaturan Notifikasi Sheet | `pengaturan_notifikasi/` | ⬜ TODO |

---

## 5. ARCHITECTURE BLUEPRINT

### 5.1 Module Structure

```
SDM3ParentPortal/
├── androidApp/                    # Android entry point
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── MainActivity.kt
│   │   └── res/
│   │       └── xml/network_security_config.xml
│   └── build.gradle.kts
│
├── iosApp/                        # iOS entry point
│   ├── iosApp.xcodeproj/
│   ├── ContentView.swift
│   └── Configuration/
│
├── shared/                        # KMP Shared module
│   ├── src/
│   │   ├── commonMain/kotlin/com/sdm3/parent/
│   │   │   ├── core/
│   │   │   │   ├── designsystem/    → Design System Agent
│   │   │   │   ├── network/         → Data Agent
│   │   │   │   ├── database/        → Data Agent
│   │   │   │   ├── security/        → Security Agent
│   │   │   │   ├── di/              → Architect Agent
│   │   │   │   └── util/            → Architect Agent
│   │   │   ├── domain/
│   │   │   │   ├── entity/          → Architect Agent
│   │   │   │   ├── repository/      → Architect Agent (interface)
│   │   │   │   └── usecase/         → Feature Agent
│   │   │   ├── data/
│   │   │   │   ├── remote/          → Data Agent
│   │   │   │   ├── local/           → Data Agent
│   │   │   │   ├── repository/      → Data Agent
│   │   │   │   └── mapper/          → Data Agent
│   │   │   └── feature/
│   │   │       ├── auth/            → Feature Agent
│   │   │       ├── home/            → Feature Agent
│   │   │       ├── nilai/           → Feature Agent
│   │   │       ├── pembayaran/      → Payment Agent
│   │   │       ├── kehadiran/       → Feature Agent
│   │   │       ├── infoanak/        → Feature Agent
│   │   │       ├── kegiatan/        → Feature Agent
│   │   │       ├── rapor/           → Feature Agent
│   │   │       ├── notifikasi/      → Feature Agent
│   │   │       └── profil/          → Feature Agent
│   │   ├── androidMain/kotlin/      → Android-specific implementations
│   │   ├── iosMain/kotlin/          → iOS-specific implementations
│   │   └── commonTest/kotlin/       → QA Agent
│   └── build.gradle.kts
│
├── .github/workflows/             → DevOps Agent
├── fastlane/                      → DevOps Agent
├── gradle/libs.versions.toml      → Architect Agent
└── build.gradle.kts               → Architect Agent
```

### 5.2 Data Flow Pattern

```
UI (Compose Screen)
    ↕ UiState / Events
ViewModel (StateFlow<UiState>)
    ↕ Kotlin Result<T>
UseCase (domain logic, single responsibility)
    ↕ domain Entity
Repository Interface (domain layer)
    ↕
Repository Implementation (data layer)
    ↕ DTO / Cache
Remote DataSource (Ktor → Laravel REST API)
Local DataSource (SQLDelight)

Realtime: Pusher/Reverb WebSocket → Laravel Broadcasting
          (Atau polling interval untuk data non-kritis)
```

### 5.3 Navigation Graph

```
NavHost (root)
├── authGraph (not authenticated)
│   ├── splash → (auto-navigate)
│   ├── onboarding (pager: 3 pages)
│   ├── login
│   ├── verifikasiOtp
│   └── pilihAnak
│
└── mainGraph (authenticated)
    ├── home (BottomNav destination)
    │   └── pengumuman → detail
    ├── nilai (BottomNav destination)
    │   ├── nilaiList
    │   ├── detailNilaiMapel
    │   └── tabFormatif
    ├── pembayaran (BottomNav destination)
    │   ├── pembayaranSpp
    │   ├── pilihMetodeBayar
    │   ├── prosesPembayaran
    │   ├── pembayaranBerhasil
    │   └── detailBuktiBayar
    ├── rapor (BottomNav destination)
    │   ├── halamanRapor
    │   ├── previewRaporPdf
    │   └── verifikasiQrRapor
    └── profil (BottomNav destination)
        ├── profilAkun
        ├── detailInfoAnak
        ├── kehadiranSiswa
        ├── kegiatanProgram
        ├── notifikasi
        └── pengaturanNotifikasi
```

---

## 6. DATA MODELS & API CONTRACT

### 6.1 Database Schema (Laravel MySQL/PostgreSQL)

```sql
-- USERS & AUTH — Laravel Sanctum users table
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,             -- UUID
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    email_verified_at TIMESTAMP NULL,
    password VARCHAR(255) NOT NULL,
    two_factor_secret TEXT NULL,
    two_factor_recovery_codes TEXT NULL,
    remember_token VARCHAR(100) NULL,
    avatar VARCHAR(2048) NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    gauth_id VARCHAR(255) NULL,
    gauth_type VARCHAR(255) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- PERSONAL ACCESS TOKENS (Sanctum)
CREATE TABLE personal_access_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tokenable_type VARCHAR(255) NOT NULL,
    tokenable_id CHAR(36) NOT NULL,       -- UUID → users.id
    name VARCHAR(255) NOT NULL,
    token VARCHAR(64) UNIQUE NOT NULL,
    abilities TEXT NULL,
    last_used_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENTS (SISWA) — anak terhubung ke parent via user_id
CREATE TABLE students (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,  -- FK ke parent user
    name VARCHAR(255) NOT NULL,
    nisn VARCHAR(20) UNIQUE NOT NULL,
    nis VARCHAR(20) NULL,
    gender ENUM('Laki-laki','Perempuan') NOT NULL,
    birth_place VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    religion VARCHAR(50) NULL,
    spp INTEGER NULL,                     -- Nominal SPP
    dpp INTEGER NULL,                     -- Nominal DPP
    uniform_fee INTEGER DEFAULT 0,
    photo VARCHAR(100) NULL,
    guardian_type ENUM('orang_tua','wali') DEFAULT 'orang_tua',
    father_name VARCHAR(255) NULL,
    mother_name VARCHAR(255) NULL,
    father_education VARCHAR(255) NULL,
    mother_education VARCHAR(255) NULL,
    father_occupation VARCHAR(255) NULL,
    mother_occupation VARCHAR(255) NULL,
    guardian_name VARCHAR(255) NULL,
    guardian_occupation VARCHAR(255) NULL,
    guardian_address VARCHAR(255) NULL,
    phone VARCHAR(20) NULL,               -- Nomor HP siswa
    parent_phone VARCHAR(255) NULL,       -- Nomor HP orang tua
    parent_email VARCHAR(255) NULL,
    address TEXT NULL,
    entry_year VARCHAR(10) NULL,
    entry_date DATE NULL,
    scholarship VARCHAR(255) NULL,
    status VARCHAR(255) DEFAULT 'active',
    is_inklusi BOOLEAN DEFAULT FALSE,
    shadow_teacher_id CHAR(36) NULL REFERENCES employees(id) ON DELETE SET NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- CLASSROOMS (KELAS)
CREATE TABLE classrooms (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,           -- "4-A (Ibnu Sina)"
    code VARCHAR(50) NULL,
    classroom_type VARCHAR(100) NOT NULL, -- "Reguler", etc.
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENT-CLASSROOM PIVOT (many-to-many dengan academic year)
CREATE TABLE student_classrooms (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    classroom_type VARCHAR(100) NULL,
    status ENUM('active','transferred','graduated','retained','dropped') DEFAULT 'active',
    enrolled_at TIMESTAMP NULL,
    left_at TIMESTAMP NULL,
    notes TEXT NULL,
    enrolled_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_enrollment (student_id, classroom_id, academic_year_id(8))
);

-- ACADEMIC YEARS
CREATE TABLE academic_years (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,     -- "2025/2026"
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- TEACHERS (GURU)
CREATE TABLE teachers (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT NULL,
    graduation VARCHAR(255) NULL,
    employee_id CHAR(36) NULL REFERENCES employees(id) ON DELETE SET NULL,
    staff_position_id CHAR(36) NULL,
    photo VARCHAR(255) NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- TEACHER-CLASSROOM PIVOT (dengan flag homeroom)
CREATE TABLE teacher_classrooms (
    teacher_id CHAR(36) NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    is_homeroom BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (teacher_id, classroom_id)
);

-- TEACHER-SUBJECT PIVOT
CREATE TABLE teacher_subjects (
    teacher_id CHAR(36) NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    PRIMARY KEY (teacher_id, subject_id)
);

-- SUBJECTS (MATA PELAJARAN)
CREATE TABLE subjects (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- CLASSROOM-SUBJECT PIVOT
CREATE TABLE classroom_subjects (
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    PRIMARY KEY (classroom_id, subject_id)
);

-- SUBJECT TPS (Tujuan Pembelajaran)
CREATE TABLE subject_tps (
    id CHAR(36) PRIMARY KEY,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    grade_level TINYINT UNSIGNED NOT NULL,    -- 1–6
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    semester VARCHAR(20) DEFAULT 'ts1',       -- 'ts1', 'ts2', etc.
    tp_number TINYINT UNSIGNED NOT NULL,
    tp_name VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_tp (subject_id, grade_level, semester, tp_number)
);

-- GRADES (NILAI SEMESTER — aggregate per subject)
CREATE TABLE grades (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    academic_year VARCHAR(50) NULL,          -- Denormalized "2025/2026"
    semester VARCHAR(20) NOT NULL DEFAULT 'ganjil',
    score DECIMAL(5,2) NULL,                 -- Nilai akhir
    narrative TEXT NULL,                     -- Kurikulum Merdeka narrative
    predicate VARCHAR(5) NULL,               -- "A", "B", "C", etc.
    is_manual_override BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_grade (student_id, classroom_id, subject_id, semester)
);

-- GRADE COMPONENTS (NILAI PER-TP / PER-KOMPONEN)
CREATE TABLE grade_components (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    semester VARCHAR(20) DEFAULT 'ganjil',
    component_type VARCHAR(50) NOT NULL,      -- 'sumatif', 'formatif', 'projek'
    component_subtype VARCHAR(50) NULL,       -- 'Formatif', 'Sumatif', 'Akhir Semester'
    score DECIMAL(5,2) NULL,
    tp_name VARCHAR(500) NULL,                -- Nama TP / Tujuan Pembelajaran
    tp_number TINYINT UNSIGNED NULL,
    is_max_tp BOOLEAN DEFAULT FALSE,
    is_min_tp BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_component (student_id, classroom_id, subject_id, semester, component_type)
);

-- GRADE COMPONENT WEIGHTS (Bobot nilai per komponen)
CREATE TABLE grade_component_weights (
    id CHAR(36) PRIMARY KEY,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    subject_id CHAR(36) NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    semester VARCHAR(20) DEFAULT 'ganjil',
    component_label VARCHAR(100) NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    kkm DECIMAL(5,2) NULL DEFAULT 70,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENT ATTENDANCE (KEHADIRAN SISWA)
CREATE TABLE student_attendances (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    classroom_type VARCHAR(100) NOT NULL,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    date DATE NOT NULL,
    status ENUM('hadir','izin','pulang','sakit','alpa','present','absent','late','excused') DEFAULT 'hadir',
    notes TEXT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_attendance (student_id, classroom_id, date)
);

-- STUDENT ATTENDANCE SUMMARIES (Rekap bulanan/semester)
CREATE TABLE student_attendance_summaries (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NOT NULL REFERENCES academic_years(id) ON DELETE CASCADE,
    semester VARCHAR(20) NOT NULL,
    hadir SMALLINT UNSIGNED DEFAULT 0,
    sakit SMALLINT UNSIGNED DEFAULT 0,
    izin SMALLINT UNSIGNED DEFAULT 0,
    alpa SMALLINT UNSIGNED DEFAULT 0,
    created_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_summary (student_id, classroom_id, academic_year_id, semester)
);

-- PAYMENT TITLES (Jenis pembayaran: SPP, Ekskul, Buku, etc.)
CREATE TABLE payment_titles (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENT FEES (Tagihan per siswa)
CREATE TABLE student_fees (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    payment_title_id CHAR(36) NOT NULL REFERENCES payment_titles(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    due_date DATE NULL,
    status ENUM('belum_bayar','sebagian','lunas','dibebaskan') DEFAULT 'belum_bayar',
    academic_year VARCHAR(20) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- PAYMENTS (Transaksi pembayaran — Midtrans)
CREATE TABLE payments (
    id CHAR(36) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    classroom_type VARCHAR(100) NOT NULL,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    email VARCHAR(255) NULL,
    gross_amount DECIMAL(15,2) NULL,
    payment_type VARCHAR(50) NULL,
    payment_title_id CHAR(36) NULL REFERENCES payment_titles(id) ON DELETE CASCADE,
    session_id VARCHAR(255) NULL,
    payment_url TEXT NULL,
    transaction_id VARCHAR(255) NULL,
    va_number VARCHAR(255) NULL,
    status VARCHAR(50) DEFAULT 'pending',   -- pending, success, failed, expired, cancelled
    start_date DATE NULL,
    end_date DATE NULL,
    paid_at TIMESTAMP NULL,
    bulk_id VARCHAR(255) NULL,
    account_id VARCHAR(255) NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- ANNOUNCEMENTS / ARTICLES (Pengumuman sekolah)
CREATE TABLE articles (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT NULL,
    image VARCHAR(255) NULL,
    user_id CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    category VARCHAR(255) NULL,
    status VARCHAR(255) DEFAULT 'published', -- draft, published, archived
    published_at TIMESTAMP NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NULL,
    message TEXT NOT NULL,
    data JSON NULL,                         -- Bisa menyimpan reference_id, metadata
    read_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    failed_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- NOTIFICATION PREFERENCES
CREATE TABLE notification_preferences (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    push_notifications BOOLEAN DEFAULT FALSE,
    whatsapp_notifications BOOLEAN DEFAULT TRUE,
    frequency ENUM('immediate','daily','weekly') DEFAULT 'daily',
    quiet_hours_start TIME NULL,
    quiet_hours_end TIME NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- REPORTS SYSTEM — Rapor Instances (template-based)
CREATE TABLE rapor_templates (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    school_name VARCHAR(255) NULL,
    school_logo_path VARCHAR(255) NULL,
    school_address VARCHAR(255) NULL,
    school_phone VARCHAR(255) NULL,
    npsn VARCHAR(255) NULL,
    principal_name VARCHAR(255) NULL,
    principal_nbm VARCHAR(255) NULL,
    paper_size VARCHAR(10) DEFAULT 'A4',
    margin JSON NULL,
    font_family VARCHAR(255) DEFAULT 'Times New Roman',
    font_size VARCHAR(5) DEFAULT '10',
    show_watermark BOOLEAN DEFAULT FALSE,
    watermark_text VARCHAR(50) NULL,
    show_page_number BOOLEAN DEFAULT FALSE,
    show_qr_code BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'draft',
    is_default BOOLEAN DEFAULT FALSE,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE rapor_sections (
    id CHAR(36) PRIMARY KEY,
    rapor_template_id CHAR(36) NOT NULL REFERENCES rapor_templates(id) ON DELETE CASCADE,
    section_type VARCHAR(50) NOT NULL,
    label VARCHAR(255) NULL,
    sort_order INTEGER DEFAULT 0,
    is_enabled BOOLEAN DEFAULT TRUE,
    force_page_break_before BOOLEAN DEFAULT FALSE,
    config JSON NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE rapor_instances (
    id CHAR(36) PRIMARY KEY,
    rapor_template_id CHAR(36) NOT NULL REFERENCES rapor_templates(id),
    student_id CHAR(36) NOT NULL REFERENCES students(id),
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id),
    academic_year_id CHAR(36) NOT NULL REFERENCES academic_years(id),
    semester VARCHAR(20) NOT NULL DEFAULT 'ganjil',
    status VARCHAR(20) DEFAULT 'draft',
    generated_pdf_path VARCHAR(255) NULL,
    verification_code VARCHAR(20) UNIQUE NULL,
    document_number VARCHAR(255) NULL,
    approved_at TIMESTAMP NULL,
    approved_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    generated_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    generated_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_rapor (student_id, classroom_id, academic_year_id, semester, rapor_template_id)
);

-- STUDENT REPORT CARDS (legacy — per-teacher report cards)
CREATE TABLE student_report_cards (
    id CHAR(36) PRIMARY KEY,
    file VARCHAR(255) NULL,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    teacher_id CHAR(36) NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    report_card_template_id CHAR(36) NULL REFERENCES report_templates(id) ON DELETE SET NULL,
    status ENUM('draft','completed','finalized') DEFAULT 'draft',
    finalized_at TIMESTAMP NULL,
    finalized_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    narasi_text LONGTEXT NULL,
    narasi_generated_at TIMESTAMP NULL,
    narasi_model VARCHAR(255) DEFAULT 'claude-3-5-sonnet',
    narasi_prompt_version INTEGER DEFAULT 1,
    is_narasi_approved BOOLEAN DEFAULT FALSE,
    narasi_approved_at TIMESTAMP NULL,
    narasi_approved_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    distribution_status ENUM('not_distributed','pending','sent','failed') DEFAULT 'not_distributed',
    distributed_at TIMESTAMP NULL,
    period VARCHAR(20) NULL DEFAULT 'ganjil',
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- P5 PROJECT ASSESSMENTS (Projek Penguatan Profil Pelajar Pancasila)
CREATE TABLE p5_dimensions (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE p5_indicators (
    id CHAR(36) PRIMARY KEY,
    p5_dimension_id CHAR(36) NOT NULL REFERENCES p5_dimensions(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE student_p5_assessments (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id CHAR(36) NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    academic_year VARCHAR(255) NOT NULL,       -- "2025/2026"
    assessment_period VARCHAR(255) NOT NULL,   -- "ganjil"
    assessor_user_id CHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assessed_at TIMESTAMP NULL,
    notes LONGTEXT NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_p5_period (student_id, classroom_id, academic_year, assessment_period)
);

CREATE TABLE student_p5_scores (
    id CHAR(36) PRIMARY KEY,
    student_p5_assessment_id CHAR(36) NOT NULL REFERENCES student_p5_assessments(id) ON DELETE CASCADE,
    p5_dimension_id CHAR(36) NOT NULL REFERENCES p5_dimensions(id) ON DELETE CASCADE,
    score INTEGER NOT NULL,                    -- Skala 1–4
    evidence LONGTEXT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_p5_score (student_p5_assessment_id, p5_dimension_id)
);

-- EXTRACURRICULAR (EKSKUL)
CREATE TABLE extracurriculars (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    photo VARCHAR(255) NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENT EXTRACURRICULAR
CREATE TABLE student_extracurriculars (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    extracurricular_id CHAR(36) NOT NULL REFERENCES extracurriculars(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NULL REFERENCES academic_years(id) ON DELETE SET NULL,
    semester VARCHAR(10) NULL,
    grade VARCHAR(20) NULL,                    -- "A", "B+", "C", etc.
    description TEXT NULL,                     -- Narasi kegiatan
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    UNIQUE KEY unique_eskul (student_id, extracurricular_id, academic_year_id, semester)
);

-- SEKOLAH (school profile — single row)
CREATE TABLE sekolahs (
    id CHAR(36) PRIMARY KEY,
    npsn VARCHAR(255) NULL,
    nama VARCHAR(255) NULL,
    jenjang VARCHAR(255) NULL,
    alamat VARCHAR(255) NULL,
    kecamatan VARCHAR(255) NULL,
    kota VARCHAR(255) NULL,
    provinsi VARCHAR(255) NULL,
    kontak_pic VARCHAR(255) NULL,
    no_telepon VARCHAR(255) NULL,
    koordinat_lat DOUBLE NULL,
    koordinat_lng DOUBLE NULL,
    is_active BOOLEAN DEFAULT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

-- STUDENT INKLUSI (ABK report cards)
CREATE TABLE student_inklusi_report_cards (
    id CHAR(36) PRIMARY KEY,
    student_id CHAR(36) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    academic_year_id CHAR(36) NOT NULL REFERENCES academic_years(id) ON DELETE CASCADE,
    semester VARCHAR(255) NOT NULL,
    shadow_teacher_name VARCHAR(255) NOT NULL,
    shadow_teacher_nip VARCHAR(255) NULL,
    date DATE NOT NULL,
    status VARCHAR(255) DEFAULT 'draft',
    created_by CHAR(36) NULL REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE student_inklusi_assessment_items (
    id CHAR(36) PRIMARY KEY,
    inklusi_report_card_id CHAR(36) NOT NULL REFERENCES student_inklusi_report_cards(id) ON DELETE CASCADE,
    aspect_group VARCHAR(255) NOT NULL,
    aspect_name VARCHAR(255) NOT NULL,
    basic_achievement TEXT NULL,
    development_achievement TEXT NULL,
    expected_achievement TEXT NULL,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);
```

### 6.2 Authorization Strategy (Laravel API — bukan RLS)

Authorization pada API Laravel dilakukan melalui kombinasi:

```
1. Laravel Sanctum — Token-based authentication
   - Client request POST /api/sanctum/token dengan email + password
   - Mendapatkan plain-text token yang disimpan di client (KVault encrypted)
   - Token dikirim via Authorization: Bearer header

2. Spatie Permission — Role-based access control
   - Roles: superadmin, admin, admin_akademik, teacher, finance, hr
   - Untuk parent portal: role khusus "parent" ditambahkan
   - API endpoints menggunakan middleware role:admin|parent

3. Scope Filtering di setiap controller/query
   - Student data: scope by user_id = auth()->id() via students.user_id
   - Grades: scope by student belongs to authenticated parent
   - Payments: scope by student belongs to authenticated parent
   - Attendance: scope by student belongs to authenticated parent

4. Laravel Policy (opsional)
   - Untuk authorization granular per-model
```

**Key relationship:** Orang tua terhubung ke siswa melalui `students.user_id`.
```sql
-- Query dasar untuk mendapatkan anak-anak dari parent yang login:
SELECT * FROM students WHERE user_id = :auth_user_id;
```

Tidak ada tabel `parent_students` terpisah — hubungan langsung via `students.user_id`.

### 6.3 Domain Entities (Kotlin)

```kotlin
// entity/Parent.kt — User yang login sebagai parent
data class Parent(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val phoneNumber: String?       // Dari student.parent_phone jika ada
)

// entity/Student.kt
data class Student(
    val id: String,
    val userId: String?,             // FK ke parent user
    val nisn: String,
    val nis: String?,
    val name: String,                // "full_name" di blueprint → "name" di Laravel
    val className: String,
    val classroomId: String,
    val academicYear: String,        // Dari student_classrooms → academic_years
    val gender: Gender,
    val birthPlace: String,
    val birthDate: LocalDate,
    val photoUrl: String?,           // "avatar_url" di blueprint → "photo" di Laravel
    val status: StudentStatus,
    val fatherName: String?,
    val motherName: String?,
    val parentPhone: String?,
    val isInklusi: Boolean = false,
    val curriculum: String = "Merdeka Belajar"
)

enum class Gender { LakiLaki, Perempuan }
enum class StudentStatus { ACTIVE, INACTIVE, GRADUATED, TRANSFER_OUT, DROPPED }

// entity/Grade.kt — Nilai akhir semester per mata pelajaran
data class Grade(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val subjectName: String,
    val classroomId: String,
    val academicYear: String,
    val semester: String,            // "ganjil" / "genap"
    val score: Double?,              // Nilai akhir (nullable — belum diinput)
    val narrative: String?,          // Kurikulum Merdeka narrative
    val predicate: String?,          // "A", "B", "C", "D", "E"
    val isManualOverride: Boolean,
    val components: List<GradeComponent> = emptyList()
)

// entity/GradeComponent.kt — Nilai per-TP / per-komponen
data class GradeComponent(
    val id: String,
    val subjectId: String,
    val componentType: ComponentType,
    val componentSubtype: String?,
    val score: Double?,
    val tpName: String?,
    val tpNumber: Int?,
    val isMaxTp: Boolean,
    val isMinTp: Boolean
)

enum class ComponentType { SUMATIF, FORMATIF, PROJEK }

// entity/SubjectTp.kt — Tujuan Pembelajaran
data class SubjectTp(
    val id: String,
    val subjectId: String,
    val gradeLevel: Int,
    val semester: String,
    val tpNumber: Int,
    val tpName: String
)

// entity/Attendance.kt
data class Attendance(
    val id: String,
    val date: LocalDate,
    val status: AttendanceStatus,
    val note: String?
)

enum class AttendanceStatus {
    HADIR, SAKIT, IZIN, ALPA, PULANG,
    PRESENT, ABSENT, LATE, EXCUSED
}

// entity/AttendanceSummary.kt — Rekap kehadiran
data class AttendanceSummary(
    val hadir: Int,
    val sakit: Int,
    val izin: Int,
    val alpa: Int
)

// entity/PaymentTitle.kt — Jenis tagihan
data class PaymentTitle(
    val id: String,
    val name: String,                // "SPP", "Kegiatan Ekskul", etc.
    val code: String
)

// entity/StudentFee.kt — Tagihan per siswa
data class StudentFee(
    val id: String,
    val studentId: String,
    val paymentTitleId: String,
    val paymentTitleName: String,
    val amount: Long,                // in IDR (Rupiah)
    val dueDate: LocalDate?,
    val status: FeeStatus,
    val academicYear: String,
    val notes: String?
)

enum class FeeStatus { BELUM_BAYAR, SEBAGIAN, LUNAS, DIBEBASKAN }

// entity/Payment.kt — Transaksi pembayaran
data class Payment(
    val id: String,
    val orderId: String,
    val studentId: String,
    val paymentTitleName: String?,   // Dari relasi paymentTitle
    val grossAmount: Long,
    val status: PaymentStatus,
    val paymentType: String?,        // Dari Midtrans: "bank_transfer", "qris", etc.
    val vaNumber: String?,           // Virtual Account number
    val paymentUrl: String?,         // Midtrans Snap redirect URL
    val transactionId: String?,
    val paidAt: Instant?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val createdAt: Instant
)

enum class PaymentStatus { PENDING, SUCCESS, FAILED, EXPIRED, CANCELLED }

// entity/Notification.kt
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,             // "body" di blueprint → "message" di Laravel
    val type: String,                // String bebas, bukan enum
    val data: Map<String, Any>?,     // JSON data, bisa berisi reference_id
    val isRead: Boolean,
    val createdAt: Instant
)

// entity/ReportCard.kt — Rapor instance
data class ReportCard(
    val id: String,
    val studentId: String,
    val academicYear: String,
    val semester: String,
    val status: String,              // "draft", "submitted", "approved"
    val pdfUrl: String?,             // generated_pdf_path
    val verificationCode: String?,
    val documentNumber: String?,
    val approvedAt: Instant?,
    val publishedAt: Instant?
)

// entity/P5Assessment.kt — Projek Pancasila
data class P5Assessment(
    val id: String,
    val academicYear: String,
    val assessmentPeriod: String,
    val notes: String?,
    val scores: List<P5Score>
)

data class P5Score(
    val dimensionName: String,
    val score: Int,                  // Skala 1–4
    val evidence: String?
)

// entity/Extracurricular.kt
data class Extracurricular(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String?
)

data class StudentExtracurricular(
    val id: String,
    val extracurricularName: String,
    val grade: String?,              // "A", "B+", etc.
    val description: String?,        // Narasi kegiatan
    val semester: String?
)

// entity/Sekolah.kt — Profil sekolah
data class Sekolah(
    val npsn: String?,
    val nama: String?,
    val alamat: String?,
    val kecamatan: String?,
    val kota: String?,
    val provinsi: String?,
    val noTelepon: String?
)
```

### 6.4 API Endpoints (Ktor Client → Laravel REST API)

```kotlin
// === AUTHENTICATION STRATEGY ===
// PRIMARY: Laravel Sanctum SPA (Cookie-based) — paling aman
//   1. GET /sanctum/csrf-cookie → set XSRF-TOKEN cookie
//   2. POST /login → email + password → set session cookie (Laravel session)
//   3. Semua request berikutnya: cookie otomatis terkirim via Ktor HttpClient
//   Note: Ktor perlu enable cookies: install(HttpCookies)
//
// FALLBACK: Bearer token (untuk non-browser / WebView)
//   1. POST /api/sanctum/token → email + password + device_name → plainTextToken
//   2. Token disimpan di KVault (AES-256 encrypted local storage)
//   3. Dikirim via header: Authorization: Bearer {token}
//
// Format response: { success: bool, data: T, message: string, code: int }

object Endpoints {
    // ========================================================================
    // SANCTUM CSRF (wajib untuk SPA cookie auth)
    // ========================================================================
    const val SANCTUM_CSRF_COOKIE = "/sanctum/csrf-cookie"  // GET
    const val LOGIN = "/login"                              // POST: email + password
    const val LOGOUT = "/logout"                            // POST: revoke session

    // ========================================================================
    // SANCTUM TOKEN AUTH (fallback untuk mobile native)
    // ========================================================================
    const val API_TOKEN = "/api/sanctum/token"              // POST: email + password + device_name
    const val API_USER = "/api/user"                        // GET: authenticated user
    const val API_TOKENS = "/api/tokens"                    // GET: list tokens; DELETE /{id}: revoke

    // ========================================================================
    // PARENT PORTAL — ENDPOINT KHUSUS (PERLU DIBUAT DI LARAVEL)
    // Prefix: /api/parent/ — middleware: auth:sanctum + role:parent
    // ========================================================================
    const val PARENT_STUDENTS = "/api/parent/students"             // GET: anak-anak (scope by students.user_id)
    const val PARENT_STUDENT_DETAIL = "/api/parent/students/{id}"  // GET: detail siswa
    const val PARENT_GRADES = "/api/parent/grades"                 // GET: nilai (filter by ?student_id&?semester)
    const val PARENT_GRADE_COMPONENTS = "/api/parent/grade-components" // GET: komponen nilai per-TP
    const val PARENT_ATTENDANCES = "/api/parent/attendances"       // GET: kehadiran (?student_id&?month&?year)
    const val PARENT_ATTENDANCE_SUMMARY = "/api/parent/attendance-summary" // GET: rekap kehadiran
    const val PARENT_PAYMENTS = "/api/parent/payments"             // GET: transaksi pembayaran
    const val PARENT_STUDENT_FEES = "/api/parent/student-fees"     // GET: tagihan aktif
    const val PARENT_NOTIFICATIONS = "/api/parent/notifications"   // GET: notifikasi
    const val PARENT_NOTIFICATION_READ = "/api/parent/notifications/{id}/read" // PATCH: mark as read
    const val PARENT_ARTICLES = "/api/parent/articles"             // GET: pengumuman
    const val PARENT_RAPOR_INSTANCES = "/api/parent/rapor-instances"   // GET: rapor instances
    const val PARENT_RAPOR_DOWNLOAD = "/api/parent/rapor/{id}/download" // GET: download PDF (dari R2)
    const val PARENT_RAPOR_VERIFY = "/api/parent/rapor/verify"     // POST: verify QR code
    const val PARENT_EXTRACURRICULARS = "/api/parent/extracurriculars" // GET: ekskul anak
    const val PARENT_PROFILE = "/api/parent/profile"               // GET/PUT: profil orang tua
    const val PARENT_DASHBOARD = "/api/parent/dashboard"           // GET: ringkasan dashboard

    // ========================================================================
    // EXISTING API ENDPOINTS (admin/teacher — perlu scope filter untuk parent)
    // ========================================================================
    const val STUDENTS = "/api/students"                          // GET: daftar semua siswa (admin scope)
    const val CLASSROOMS = "/api/classrooms"                      // GET: daftar kelas
    const val GRADES = "/api/grades"                              // GET: nilai (admin/teacher scope)
    const val GRADE_TRANSCRIPT = "/api/grades/transcript/{student}"
    const val STUDENT_ATTENDANCES = "/api/student-attendances"
    const val PAYMENTS = "/api/payments"                          // GET: payments (admin scope)
    const val PAYMENT_DETAIL = "/api/payments/{id}"

    // ========================================================================
    // MIDTRANS PAYMENT
    // ========================================================================
    const val MIDTRANS_SNAP_TOKEN = "/dashboard/midtrans/snap-token/{payment}"  // POST: get Snap token
    const val MIDTRANS_STATUS = "/dashboard/midtrans/status/{chargeId}"         // GET: status
    const val MIDTRANS_PAYMENT_METHODS = "/dashboard/midtrans/payment-methods"  // GET: metode

    // ========================================================================
    // ANALYTICS (nilai, attendance, benchmarking)
    // ========================================================================
    const val ANALYTICS_DASHBOARD = "/api/analytics/dashboard-summary"
    const val ANALYTICS_CHART = "/api/analytics/student/{id}/chart-data"
    const val ANALYTICS_SUBJECT_PERFORMANCE = "/api/analytics/student/{id}/subject-performance"
    const val ANALYTICS_ATTENDANCE_TREND = "/api/analytics/student/{id}/attendance-trend"
    const val ANALYTICS_PEER_BENCHMARK = "/api/analytics/student/{id}/peer-benchmark"
}
```

> **CATATAN PENTING:** Endpoint dengan prefix `/api/parent/` merupakan endpoint baru yang **harus dibuat** di Laravel. Endpoint yang sudah ada (`/api/students`, `/api/grades`, dll) menggunakan middleware `role:admin|teacher` — belum bisa diakses oleh role parent. Implementasi endpoint parent:
> - Filter data berdasarkan `students.user_id = auth()->id()` (parent melihat anaknya sendiri)
> - Middleware: `auth:sanctum` + permission `access-parent-portal` (perlu dibuat)
> - Response format konsisten: `{ success: bool, data: T, message: string, code: int, timestamp: string }`

---

## 7. PHASE ROADMAP

```
PHASE 0 — Foundation & Setup          [Week 1]      Architect + DevOps
PHASE 1 — Auth & Onboarding           [Week 2-3]    Security + Feature + Design
PHASE 2 — Dashboard & Navigation      [Week 3-4]    Feature + Design
PHASE 3 — Nilai & Rapor Module        [Week 5-6]    Feature + Data
PHASE 4 — Pembayaran SPP Module       [Week 7-8]    Payment + Security
PHASE 5 — Kehadiran Module            [Week 8-9]    Feature + Data
PHASE 6 — Info Anak & Kegiatan        [Week 9-10]   Feature + Data
PHASE 7 — Notifikasi & Pengumuman     [Week 10-11]  Feature + Data
PHASE 8 — Profil & Pengaturan         [Week 11]     Feature
PHASE 9 — Security Hardening          [Week 12]     Security
PHASE 10 — Testing & QA               [Week 12-13]  QA
PHASE 11 — CI/CD & Deployment         [Week 13-14]  DevOps
```

**Legend Status:**
- ⬜ `TODO` — Belum dimulai
- 🟡 `IN PROGRESS` — Sedang dikerjakan
- 🔵 `IN REVIEW` — Menunggu code review
- ✅ `DONE` — Selesai dan di-merge
- 🔴 `BLOCKED` — Terhambat dependency lain
- ⏭️ `SKIPPED` — Ditunda ke sprint berikutnya

---

## PHASE 0 — FOUNDATION & SETUP

**Agent Utama:** Architect Agent + DevOps Agent
**Estimasi:** 5 hari kerja
**Dependensi:** Tidak ada

### P0-T1: Inisialisasi KMP Project

**Owner:** Architect Agent
**Status:** ✅ DONE

**Tasks:**
```
□ Buat project KMP baru dengan template JetBrains
□ Konfigurasi build.gradle.kts root
□ Setup libs.versions.toml dengan semua dependency
□ Konfigurasi gradle.properties (memory, KMP targets)
□ Test build Android & iOS berhasil
```

**Dependencies di libs.versions.toml:**
```toml
[versions]
kotlin = "2.1.0"
compose = "1.7.0"
koin = "3.6.0"
ktor = "3.0.0"
sqldelight = "2.0.2"
koin-android = "3.6.0"
kotlinx-serialization = "1.7.0"
kotlinx-datetime = "0.6.0"
navigation3 = "1.0.0-alpha01"
firebase = "33.6.0"
coil = "3.0.0"
firebase = "33.6.0"
napier = "2.7.1"
sentry = "7.0.0"
kvault = "1.3.0"
mokoPermissions = "0.18.0"

[libraries]
compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "compose" }
compose-ui = { module = "org.jetbrains.compose.ui:ui", version.ref = "compose" }
compose-foundation = { module = "org.jetbrains.compose.foundation:foundation", version.ref = "compose" }
compose-material3 = { module = "org.jetbrains.compose.material3:material3", version.ref = "compose" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-client-encoding = { module = "io.ktor:ktor-client-encoding", version.ref = "ktor" }
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-android = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }
navigation3-core = { module = "androidx.navigation3:navigation3-ui", version.ref = "navigation3" }
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network = { module = "io.coil-kt.coil3:coil-network-ktor3", version.ref = "coil" }
napier = { module = "io.github.aakira:napier", version.ref = "napier" }
kvault = { module = "com.liftric.kvault:kvault", version.ref = "kvault" }
```

**Acceptance Criteria:**
- [ ] Build Android berhasil di Gradle
- [ ] Build iOS berhasil di Xcode
- [ ] Tidak ada error dependency conflict

---

### P0-T2: Laravel API Endpoint Setup

**Owner:** Data Agent
**Status:** ⬜ TODO — endpoint parent portal perlu dibuat di Laravel

**Tasks:**
```
□ Buat API endpoint khusus parent portal di Laravel:
  routes/api.php → prefix: /api/parent/
  Middleware: auth:sanctum + permission:access-parent-portal
□ Implement ParentApiController (atau trait/scope):
  GET  /api/parent/students       → daftar anak (filter students.user_id)
  GET  /api/parent/dashboard      → ringkasan dashboard
  GET  /api/parent/grades         → nilai anak
  GET  /api/parent/grade-components → komponen nilai per-TP
  GET  /api/parent/attendances    → kehadiran
  GET  /api/parent/attendance-summary → rekap kehadiran
  GET  /api/parent/student-fees   → tagihan aktif
  GET  /api/parent/payments       → transaksi pembayaran
  GET  /api/parent/notifications  → notifikasi
  PATCH /api/parent/notifications/{id}/read → mark read
  GET  /api/parent/articles       → pengumuman
  GET  /api/parent/extracurriculars → ekstrakurikuler
  GET  /api/parent/rapor-instances → rapor
  GET  /api/parent/rapor/{id}/download → download PDF dari R2
  GET  /api/parent/profile        → profil orang tua
  PUT  /api/parent/profile        → update profil
□ Setup Sanctum SPA cookie auth (routes/web.php):
  GET  /sanctum/csrf-cookie
  POST /login
  POST /logout
□ Buat role "parent" di Spatie Permission
□ Buat permission "access-parent-portal"
□ Asign permission ke user yang punya anak (students.user_id != null)
□ Test semua endpoint dari KMP client dengan Bearer token
□ Test cookie-based auth dari mobile client
```

**Acceptance Criteria:**
- [ ] Semua endpoint parent portal berfungsi
- [ ] Admin/teacher endpoint tidak bisa diakses parent (403)
- [ ] Parent hanya bisa lihat data anaknya sendiri (scope benar)
- [ ] Sanctum SPA cookie auth berfungsi dari client
- [ ] Sanctum token auth berfungsi (fallback)
- [ ] Response format konsisten ({ success, data, message, code, timestamp })

---

### P0-T3: Core Network & DI Setup

**Owner:** Architect Agent
**Status:** ✅ DONE

**Tasks:**
```
□ Implement HttpClientProvider (Ktor + Laravel REST API)
□ Implement NetworkResultWrapper (Result<T>)
□ Setup Koin modules (network, database, feature modules)
□ Implement AppDispatchers (IO, Main, Default)
□ Implement BaseViewModel dengan error handling
□ Implement logging interceptor (Napier)
```

**File yang Dibuat:**
```
shared/core/network/
├── HttpClientProvider.kt
├── NetworkResult.kt
├── ApiException.kt
└── interceptor/
    └── AuthInterceptor.kt

shared/core/di/
├── NetworkModule.kt
├── DatabaseModule.kt
└── AppModule.kt

shared/core/util/
├── AppDispatchers.kt
└── extensions/
```

**Acceptance Criteria:**
- [ ] Koin berhasil inject di semua module
- [ ] Network request berhasil ke Laravel API
- [ ] Error handling konsisten di semua layer

---

### P0-T4: Design System Foundation

**Owner:** Design System Agent
**Status:** ✅ DONE

**Tasks:**
```
□ Implement Color.kt (semua token dari Section 3.1)
□ Implement Typography.kt (Poppins + Inter, Section 3.2)
□ Implement Shape.kt (Section 3.3)
□ Implement Spacing.kt (Section 3.4)
□ Implement SDM3Theme.kt (MaterialTheme wrapper)
□ Setup Google Fonts di res/font/
□ Implement SDM3Button (Primary + Outline + Text)
□ Implement SDM3TextField (default + focused + error state)
□ Implement SDM3Card (standard card)
□ Implement StatusChip (Hadir/Sakit/Izin/Alpa/Lunas/Belum Bayar)
□ @Preview untuk semua komponen
```

**Acceptance Criteria:**
- [ ] Semua color token sesuai DESIGN.md
- [ ] Font Poppins & Inter termuat
- [ ] Semua komponen ada Preview yang visible
- [ ] Tidak ada hardcoded color di luar Color.kt

---

### P0-T5: CI/CD Basic Setup

**Owner:** DevOps Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Inisialisasi GitHub repository
□ Setup branch protection (main, develop)
□ Buat GitHub Actions workflow: android-build.yml
□ Buat GitHub Actions workflow: android-test.yml
□ Setup secrets (API_BASE_URL, MIDTRANS_CLIENT_KEY, MIDTRANS_SERVER_KEY)
□ Setup Firebase App Distribution untuk Android
```

**Acceptance Criteria:**
- [ ] PR ke develop men-trigger build
- [ ] Merge ke main men-trigger deployment ke Firebase App Distribution
- [ ] Secrets tidak ada di code

---

## PHASE 1 — AUTH & ONBOARDING

**Agent Utama:** Security Agent + Feature Agent + Design System Agent
**Estimasi:** 8 hari kerja
**Dependensi:** Phase 0 selesai

### P1-T1: Splash Screen

**Screen:** `SCR-001`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `splash_screen/screen.png`

**UI Specification:**
```
Layout: Full screen, centered content
Background: Primary gradient (SchoolGreenDark → Primary)
Content:
  - Logo SDM3 (circular, white border, 96dp)
  - App name "SDM3 Parent" (Poppins SemiBold, white, 28sp)
  - Tagline "Portal Orang Tua Digital" (Inter Regular, white/80%, 14sp)
  - Version text (bottom, white/60%, 12sp)
Animation:
  - Logo: scale dari 0.5 → 1.0 (300ms, EaseOut)
  - Text: fade in (200ms delay, 300ms duration)
  - Auto-navigate setelah 2.5 detik
```

**Logic:**
```kotlin
// SplashViewModel.kt
sealed class SplashDestination {
    object Onboarding : SplashDestination()    // first time user
    object Login : SplashDestination()          // not logged in / session expired
    object Home : SplashDestination()           // logged in (session valid)
}

// Check: is first launch? (DataStore) → Onboarding
// Check: is authenticated? (GET /api/user → 200 = logged in) → Home
//         Kirim cookie/token yang tersimpan, jika 401/419 → Login
// Otherwise: Login
```

**Tasks:**
```
□ Implement SplashScreen composable
□ Implement SplashViewModel
□ Implement firstLaunch check (DataStore)
□ Implement auth session check
□ Implement navigation logic
□ Test auto-navigation ke 3 destination
```

**Acceptance Criteria:**
- [ ] Animasi smooth 60fps
- [ ] Auto-navigate ke tujuan yang benar
- [ ] Tidak ada back navigation dari splash

---

### P1-T2: Onboarding Flow (3 Screens)

**Screens:** `SCR-002`, `SCR-003`, `SCR-004`
**Owner:** Feature Agent + Design System Agent
**Status:** ✅ DONE

**Visual Reference:** `onboarding_1/`, `onboarding_2/`, `onboarding_3/`

**UI Specification:**
```
Layout: Full screen, swipeable horizontal pager
Pages: 3 halaman

Page 1 — "Pantau Perkembangan Anak"
  Illustration: Orang tua + anak dengan buku/grafik nilai
  Title: "Pantau Perkembangan Anak" (Poppins SemiBold 24sp)
  Subtitle: "Lihat nilai, kehadiran, dan rapor anak Anda kapan saja"

Page 2 — "Bayar SPP Lebih Mudah"
  Illustration: Smartphone dengan payment interface
  Title: "Bayar SPP Lebih Mudah"
  Subtitle: "Pembayaran digital via transfer bank atau QRIS, tanpa antri"

Page 3 — "Informasi Sekolah Real-Time"
  Illustration: Notifikasi/announcement
  Title: "Selalu Up-to-Date"
  Subtitle: "Dapatkan pengumuman dan notifikasi langsung dari sekolah"

Navigation:
  - Bottom: page indicators (dots)
  - Skip button (top right) — langsung ke Login
  - Next/Mulai button (bottom)
  - Page 3: Button teks "Mulai Sekarang" → Login
```

**Tasks:**
```
□ Implement OnboardingScreen dengan HorizontalPager
□ Implement OnboardingPage composable
□ Implement page indicator dots
□ Implement skip functionality
□ Simpan firstLaunch = false setelah onboarding
□ Buat 3 SVG/Lottie illustration
□ Test swipe gesture
□ Test skip navigation
```

**Acceptance Criteria:**
- [ ] Swipe halus antar halaman
- [ ] Dots indicator update sesuai page
- [ ] Skip dan Mulai Sekarang navigasi ke Login
- [ ] Setelah install ulang onboarding tidak muncul lagi (karena firstLaunch flag)

---

### P1-T3: Login Screen

**Screen:** `SCR-005`
**Owner:** Security Agent + Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `login_screen/screen.png`

**UI Specification:**
```
Layout: Centered card, max width 440dp
Background: Surface (#F8F9FB)

Header:
  - Logo SDM3 (64dp, rounded 12dp, shadow, white bg)
  - App name "SDM3 Parent" (Poppins Medium, SchoolGreenDark)

Title Section:
  - "Masuk ke Akun Anda" (Poppins SemiBold 22sp)
  - "Silakan masukkan email dan password wali murid untuk melanjutkan"
    (Inter Regular, 14sp, OnSurfaceVariant)

Login Form Card (white, rounded 16dp, shadow):
  - Label: "Email"
  - TextField: 52dp height, email icon (Material Symbols), rounded 12dp
    placeholder: "Email terdaftar"
    keyboardType: Email
    focus border: 2dp Secondary blue
  - Label: "Password"
  - TextField: 52dp height, lock icon, password toggle (eye icon)
  - Button Primary: "Masuk" (52dp, full width, Secondary bg)

Footer:
  - "Lupa password? Hubungi sekolah untuk reset" (link to WhatsApp)
  - "Belum punya akun? Hubungi sekolah" (link to WhatsApp)
```

**Logic:**
```kotlin
// LoginViewModel.kt
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Events:
// LoginWithEmail(email, password) → POST /login (SPA cookie) ATAU POST /api/sanctum/token (bearer)
// No Google Sign-In (tidak tersedia di Laravel backend)
// ForgotPassword → belum tersedia (hubungi sekolah via WhatsApp)
```

**Authentication Flow (Sanctum SPA — Cookie-based — Primary):**
```
1. GET /sanctum/csrf-cookie
   → Ktor menerima XSRF-TOKEN cookie dari response Set-Cookie
   → Cookie disimpan otomatis di Ktor HttpClient (enable HttpCookies)

2. POST /login
   → Body: { email, password }
   → Headers: Cookie dari step 1, X-XSRF-TOKEN dari step 1
   → Laravel set session cookie (laravel_session)
   → Semua request selanjutnya: cookie otomatis terkirim

3. GET /api/user
   → Cookie otomatis terkirim
   → Return data user yang login
```

**Fallback Auth (Sanctum Token — Bearer):**
```
1. POST /api/sanctum/token
   → Body: { email, password, device_name: "SDM3 Parent Android" }
   → Response: { data: { token: "1|abc123..." } }

2. Token disimpan di KVault (AES-256 encrypted local storage)

3. Semua request: header Authorization: Bearer {token}
```

**Tasks:**
```
□ Implement LoginScreen composable sesuai design
□ Implement LoginViewModel dengan StateFlow
□ Implement Sanctum SPA cookie auth (primary — paling aman)
□ Implement Sanctum token auth (fallback untuk native mobile)
□ Implement "Lupa Password" → hubungi sekolah (WhatsApp deep link)
□ Implement error handling (wrong credentials, no internet)
□ Input validation (email format, password min length 8)
□ Loading state pada button (CircularProgressIndicator)
□ WhatsApp deep link untuk "Hubungi sekolah"
```

**Security Requirements:**
```
□ Password field tidak di-log ke Napier
□ Token/cookie tidak disimpan di plain SharedPreferences (gunakan KVault)
□ Maksimal 5 percobaan login, setelah itu lockout 15 menit (server-side rate limiting)
□ Cookie: HttpOnly + Secure + SameSite=Lax untuk HTTPS
```

**Acceptance Criteria:**
- [ ] Login berhasil dengan email + password → navigate ke PilihAnak
- [ ] Cookie auth berfungsi via Ktor HttpCookies
- [ ] Error message jelas dan user-friendly
- [ ] Loading indicator saat proses login
- [ ] Keyboard IME action "Next" pindah ke password field

---

### P1-T4: Verifikasi OTP (Lupa Password) Screen

**Screen:** `SCR-006`
**Owner:** Security Agent + Feature Agent
**Status:** ⏭️ SKIPPED (Fase 1) — fitur lupa password via OTP akan diimplementasikan di fase berikutnya. **Login Fase 1 menggunakan email + password langsung** via Laravel Sanctum.

> **Catatan:** Laravel Sanctum tidak menyediakan OTP auth built-in. OTP flow untuk "Lupa Password" perlu implementasi custom di Laravel (mengirim OTP ke email terdaftar yang dimiliki parent di tabel `users`). Untuk Fase 1, user cukup menghubungi sekolah via WhatsApp jika lupa password.

**Visual Reference:** `verifikasi_otp/screen.png` (disediakan untuk referensi saat implementasi nanti)

**Implementasi Nanti (Fase 9 — Security):**
```
Flow: Lupa Password → input email → kirim OTP ke email → verifikasi OTP → reset password
API:
  POST /api/parent/forgot-password  → { email } → kirim OTP
  POST /api/parent/verify-otp       → { email, otp } → verifikasi
  POST /api/parent/reset-password   → { email, otp, password } → reset
```

---

### P1-T5: Pilih Anak Screen

**Screen:** `SCR-007`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `pilih_anak/screen.png`

**UI Specification:**
```
Layout: Full screen list

Header:
  - Logo + "SDM3 Parent"
  - Title: "Pilih Data Anak"
  - Subtitle: "Pilih anak yang ingin Anda pantau"

Child Cards:
  - White card, rounded 16dp, shadow
  - Avatar (48dp, rounded) — foto anak atau placeholder initial
  - Nama anak (Poppins Medium 16sp)
  - Info: "Kelas X-Y | TA 2025/2026"
  - Status chip: "Aktif" (green pill)
  - Chevron right icon
  - Active state: border 2dp Primary, checkmark icon

Footer:
  - Jika hanya 1 anak: auto-select dan navigate ke Home
  - Jika >1 anak: tampilkan list, tap → navigate ke Home
  - Simpan selected child ke DataStore / Session
```

**Data Source:**
```
GET /api/parent/students
→ Filter by students.user_id = auth()->id()
→ Return list anak dari parent yang login

Contoh response:
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "Ahmad Fathan",
      "nisn": "0012345678",
      "classroom_name": "4-A (Ibnu Sina)",
      "academic_year": "2025/2026",
      "photo": null,
      "status": "active"
    }
  ]
}
```

**Acceptance Criteria:**
- [ ] List anak muncul dari API (GET /api/parent/students)
- [ ] Tap anak → simpan ke session → navigate ke Home
- [ ] Loading state dengan skeleton card
- [ ] Error state jika gagal fetch
- [ ] Jika tidak ada anak terdaftar: tampilkan empty state + kontak sekolah

---

## PHASE 2 — DASHBOARD & CORE NAVIGATION

**Agent Utama:** Feature Agent + Design System Agent
**Estimasi:** 6 hari kerja
**Dependensi:** Phase 1 selesai, Data layer untuk home data

### P2-T1: Bottom Navigation Bar

**Owner:** Design System Agent
**Status:** ✅ DONE

**UI Specification:**
```
Posisi: Fixed bottom, full width
Background: Surface (#F8F9FB)
Top corners: rounded 16dp
Shadow: 0 -2dp 4dp rgba(0,0,0,0.05)
Safe area: padding-bottom = insets.navigationBar

Items (5 tab):
  1. Beranda    → icon: home       → route: /home
  2. Nilai      → icon: grade      → route: /nilai
  3. Bayar      → icon: payments   → route: /pembayaran
  4. Rapor      → icon: description → route: /rapor
  5. Profil     → icon: person     → route: /profil

Active item:
  - Background: SecondaryContainer pill
  - Icon: FILLED variant (font-variation-settings 'FILL' 1)
  - Text: OnSecondaryContainer

Inactive item:
  - No background
  - Icon: OUTLINED variant
  - Text: OnSurfaceVariant
```

**Tasks:**
```
□ Implement SDM3BottomNavBar composable
□ Implement active/inactive visual state
□ Integrate dengan Navigation 3 backstack
□ Handle system navigation bar insets
□ Test haptic feedback saat tap
```

---

### P2-T2: Dashboard Beranda Screen

**Screen:** `SCR-008`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `dashboard_beranda/screen.png`

**UI Specification:**
```
TopAppBar (fixed):
  - Avatar orang tua (40dp, circular, border Primary/20)
  - "Halo," (label-md, OnSurfaceVariant)
  - "Bapak/Ibu [Nama] 👋" (headline-md bold, SchoolGreenDark)
  - Notification bell button (right)
  - Badge jika ada notif belum dibaca

Child Selector Chip (sticky under header):
  - Pill shape, rounded full
  - Avatar anak (24dp, circular)
  - "[Nama Anak] — Kelas X [Kelas] | TA 2025/2026"
  - swap_horiz icon (Primary)
  - Tap → pilih_anak_bottom_sheet

Section: Tagihan Belum Dibayar (Amber Billing Card)
  - Background: TertiaryFixed (#FFDDB4)
  - Decorative circular blur (tertiary/5, blur-2xl)
  - Label "Tagihan Belum Dibayar" (uppercase, tracking-wider)
  - Amount: "Rp XXX.XXX" (headline-lg, Tertiary, bold)
  - Icon container: white/60%, payments icon (Tertiary)
  - Date: calendar icon + "Jatuh tempo: DD Bulan YYYY"
  - Button Outline: "Bayar Sekarang" (52dp, border Tertiary, text Tertiary)
  - Jika tidak ada tagihan: tampilkan "Semua Tagihan Lunas" (success card)

Section: Layanan Sekolah (2x2 Bento Grid)
  - Title: "Layanan Sekolah" (title-lg)
  - Grid 2 kolom, gap 12dp
  - Cards:
    1. 📊 Nilai & Rapor   (icon assessment, Primary/10 bg)
    2. 💳 Pembayaran SPP  (icon credit_card, Secondary/10 bg)
    3. 📅 Kehadiran       (icon event_available, StatusWarning/10 bg)
    4. 📋 Info Anak       (icon person_search, SchoolGreenVibrant/10 bg)
  - Tap → navigate ke masing-masing screen

Section: Nilai Terbaru (Horizontal Scroll)
  - Title "Nilai Terbaru" + "Lihat Semua" (Secondary, right)
  - Scroll horizontal, hide scrollbar
  - SubjectCard (min-width 140dp):
    - Subject name (label-md, OnSurfaceVariant)
    - Score: "92" (2xl bold) + "/100" (xs, muted)
    - Status chip: "A - SANGAT BAIK" (StatusSuccess/10)

Section: Pengumuman Sekolah
  - Title "Pengumuman Sekolah"
  - Horizontal card (image left 96dp + text right):
    - Tanggal (label-md, muted)
    - Judul (label-lg bold, 2-line clamp)
    - Tap → navigate ke detail pengumuman
```

**Data Required:**
- Tagihan aktif (student_fees status = 'belum_bayar' dengan due_date terbaru)
- Nilai terbaru (grades order by updated_at DESC)
- 3 pengumuman terbaru (articles order by published_at DESC)
- Info anak yang sedang dipilih
- Rekap kehadiran bulan ini (student_attendance_summaries)

**Data Source:**
```
GET /api/parent/dashboard?student_id={id}
  → Composite response:
  {
    "student": { ... },
    "active_fee": { "payment_title_name": "SPP", "amount": 350000, "due_date": "..." },
    "recent_grades": [ { "subject_name": "...", "score": 92, "predicate": "A" } ],
    "recent_announcements": [ { "title": "...", "published_at": "..." } ],
    "attendance_summary": { "hadir": 18, "sakit": 1, "izin": 0, "alpa": 0 }
  }
  → Atau multi-request terpisah
```

**Tasks:**
```
□ Implement DashboardScreen composable
□ Implement DashboardViewModel
□ Implement HomeRepository.getHomeData() (combined call)
□ Implement ChildSelectorChip
□ Implement BillingCard (amber, dengan state: ada tagihan / lunas)
□ Implement 2x2 menu grid
□ Implement horizontal nilai scroll
□ Implement announcement cards
□ Pull-to-refresh
□ Loading skeleton
□ Error state + retry
```

**Acceptance Criteria:**
- [ ] Data muncul dari API dalam <2 detik
- [ ] Child selector menampilkan nama anak yang benar
- [ ] Billing card muncul jika ada tagihan pending
- [ ] Tap Bayar Sekarang → navigate ke PembayaranSPP
- [ ] Pull to refresh update data
- [ ] Notification badge update real-time (Pusher/Reverb WebSocket atau polling)

---

### P2-T3: Child Selector Bottom Sheet

**Sheet:** `SHT-001`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `pilih_anak_bottom_sheet/screen.png`

**UI Specification:**
```
ModalBottomSheet (Material 3):
  - Drag handle di atas
  - Title: "Pilih Anak"
  - List anak dengan radio button style
  - Nama + kelas anak
  - Active child: checkmark Primary + bold text
  - "Simpan" button (Primary, 52dp)
```

**Tasks:**
```
□ Implement PilihAnakBottomSheet
□ Update active child di ViewModel/Session
□ Refresh home data setelah ganti anak
```

---

## PHASE 3 — NILAI & RAPOR MODULE

**Agent Utama:** Feature Agent + Data Agent
**Estimasi:** 8 hari kerja
**Dependensi:** Phase 2, Grades table populated

### P3-T1: Nilai & Rapor List Screen

**Screen:** `SCR-009`, `SCR-010`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**Visual Reference:** `nilai_rapor/`, `nilai_rapor_kosong/`

**UI Specification:**
```
TopAppBar:
  - Avatar + "SDM3 Parent" title
  - Notification bell

Tab Navigation (3 tabs, pill style):
  - Container: SurfaceContainer, rounded 12dp
  - Tabs: Sumatif | Formatif | Projek
  - Active: white bg, shadow, Primary text, bold
  - Inactive: OnSurfaceVariant text

Semester Selector:
  - Dropdown chip: "Semester 1 — 2025/2026"
  - Tap → bottom sheet pilih semester

Content per Tab:
  Sumatif tab:
    - Subject cards (LazyColumn)
    - Per card: mapel name, score badge, predicate chip, date
    - Average score di atas (KPI card style)

  Formatif tab:
    - Dropdown: pilih mata pelajaran
    - TP list (Tujuan Pembelajaran)
    - Per item: TP code, description, score chip

  Projek tab:
    - Project cards dengan tema P5
    - Score + narasi singkat

Empty State (nilai_rapor_kosong):
  - Illustration: empty clipboard
  - "Nilai belum tersedia"
  - "Belum ada nilai untuk semester ini"
  - Tombol refresh
```

**Tasks:**
```
□ Implement NilaiScreen composable
□ Implement tab navigation (Sumatif/Formatif/Projek)
□ Implement SumatifTab dengan SubjectCard
□ Implement FormatifTab dengan TP list
□ Implement ProjekTab
□ Implement semester selector bottom sheet
□ Implement NilaiViewModel
□ Implement GradesRepository.getGradesByStudent()
□ Implement empty state
□ Implement loading skeleton
```

**Acceptance Criteria:**
- [ ] Tab switching smooth tanpa reload
- [ ] Sumatif: semua mapel tampil dengan nilai
- [ ] Formatif: TP list sesuai mapel yang dipilih
- [ ] Empty state muncul jika tidak ada data
- [ ] Pull to refresh

---

### P3-T2: Detail Nilai Mapel Screen

**Screen:** `SCR-015`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**Visual Reference:** `detail_nilai_mapel/screen.png`

**UI Specification:**
```
TopAppBar:
  - Back button
  - Subject name sebagai title

Header Card:
  - Subject name (headline-lg, bold)
  - Final score: large number (display-lg)
  - Predicate chip: "SANGAT BAIK" (A)
  - Semester info

Score Breakdown:
  - Linear progress bar per komponen
  - Sumatif: 40%
  - Formatif: 40%
  - Projek: 20%

TP List (Accordion):
  - Setiap TP: code + description + score
  - Expandable untuk detail
  - Status color per score range:
    ≥90: StatusSuccess
    75-89: StatusWarning
    <75: StatusDanger

Teacher Comment:
  - Card dengan note dari guru
  - Avatar guru + nama
```

**Tasks:**
```
□ Implement DetailNilaiMapelScreen
□ Implement score progress bars
□ Implement expandable TP items
□ Implement teacher comment card
□ Fetch detail grades by subject and student
```

---

### P3-T3: Tab Formatif Screen

**Screen:** `SCR-016`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**Visual Reference:** `tab_formatif/screen.png`

**UI Specification:**
```
Tab bar: Sumatif | Formatif (active) | Projek
Subject dropdown: pilih mapel
Info banner: penjelasan formatif
TP List:
  - Card per TP, expandable
  - TP code (numbered badge)
  - TP description (2-line preview, expand on tap)
  - Score chip (predicate + color)
  - Detail tab: foto/scan pekerjaan anak (opsional)
```

---

### P3-T4: Rapor Resmi Screen

**Screen:** `SCR-013`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**Visual Reference:** `halaman_rapor/screen.png`

**UI Specification:**
```
Header:
  - "Rapor Resmi" (headline-lg)
  - "Tahun Ajaran 2025/2026" (subtitle)
  - Semester badge (SecondaryContainer)

Main Report Card:
  - White card, rounded 16dp
  - Gradient header (Primary green)
  - Report name: "Sumatif 1 / 2025-2026"
  - Status chip: "Tersedia" (StatusSuccess)
  - Approved date
  - Two buttons:
    □ "Unduh PDF" (Primary/Secondary blue, 52dp)
    □ "Lihat Online" (Outline, 52dp)

Verification footer:
  - Note tentang tanda tangan elektronik
  - QR Code thumbnail (64dp)
  - "Scan QR untuk verifikasi"

Secondary Grid (2 cols):
  - "Riwayat Rapor" (4 dokumen)
  - "Statistik Nilai" (chart icon)

Footer:
  - "Kesulitan membuka rapor? Hubungi Tata Usaha"
```

**Storage: Cloudflare R2 (S3-compatible)**
```
Rapor PDF disimpan di Cloudflare R2 bucket: sdmuhammadiyah3
Config: config/rapor.php → storage_disk = 'r2'
Akses download melalui:
  GET /api/parent/rapor/{id}/download
  → Laravel generate signed URL dari R2
  → Client download PDF via URL
```

**Tasks:**
```
□ Implement HalamanRaporScreen
□ Implement RaporRepository.getReports()
□ Implement PDF download (signed URL dari Laravel → Cloudflare R2)
□ Implement PDF viewer (in-app atau intent ke PDF app)
□ Implement QR code display
□ Report history list
```

---

### P3-T5: Preview Rapor PDF Screen

**Screen:** `SCR-027`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**UI Specification:**
```
Full screen PDF viewer:
  - Top bar: title + close + download + share
  - PDF content (WebView / custom renderer)
  - Pinch to zoom
  - Scroll vertical
  - Page indicator (1/8)
```

**PDF Access Flow:**
```
1. GET /api/parent/rapor/{id}/download → Laravel return { url: "signed-r2-url..." }
2. Client download PDF dari signed URL (langsung dari Cloudflare R2 — private bucket)
3. Tampilkan via PdfRenderer / Custom PDF viewer
4. Cache lokal untuk offline access
```

**Tasks:**
```
□ Implement PreviewRaporPdfScreen
□ Integrate PDF viewer (Android: PdfRenderer atau WebView)
□ Implement signed URL fetch dari Laravel
□ Implement download from R2
□ Implement zoom controls
□ Implement share functionality (share signed URL atau file)
```

---

### P3-T6: Verifikasi QR Rapor Screen

**Screen:** `SCR-028`
**Owner:** Feature Agent
**Status:** ⬜ TODO

**Visual Reference:** `verifikasi_qr_rapor/screen.png`

**UI Specification:**
```
Deep link URL: sdm3parent://verify?code=[QR_CODE]
(Screen ini bisa dibuka dari browser via QR scan)

Content:
  - Verification status icon (check_circle, 80dp, StatusSuccess/10)
  - "Rapor Terverifikasi" (headline-lg)
  - "Dokumen ini sah secara digital"

Student Info (Bento grid):
  - Nama siswa (dengan avatar)
  - Kelas
  - Semester
  - Tahun Akademik + "Approved" badge

Action button:
  - "Lihat PDF Rapor Lengkap" (Secondary blue)

School footer:
  - Nama sekolah, alamat, social links
```

**Tasks:**
```
□ Implement VerifikasiQrRaporScreen
□ Implement deep link handling
□ Implement Edge Function: verify-report-qr
□ Handle invalid/expired QR code
```

---

## PHASE 4 — PEMBAYARAN SPP MODULE

**Agent Utama:** Payment Agent + Security Agent
**Estimasi:** 10 hari kerja
**Dependensi:** Phase 2, Midtrans account aktif

### P4-T1: Pembayaran SPP List Screen

**Screen:** `SCR-011`, `SCR-012`
**Owner:** Payment Agent
**Status:** ✅ DONE

**Visual Reference:** `pembayaran_spp/`, `pembayaran_lunas/`

**UI Specification:**
```
TopAppBar:
  - Avatar + "Pembayaran" title
  - Notification bell

Active Bill Card (Amber, rounded 24dp):
  - Decorative circles (tertiary/10 blur)
  - Label "Nama Tagihan" (uppercase, muted)
  - Bill name: "SPP Juli 2026" (headline-lg-mobile bold)
  - Status badge: "Belum Dibayar" (StatusDanger/10 pill)
  - "Total Tagihan" label
  - Amount: "Rp 350.000" (display-lg bold)
  - Button: "Bayar Sekarang" (Secondary blue, 52dp, rounded 12dp)
  - Jika lunas: card warna hijau StatusSuccess/10

Payment History:
  - Title "Riwayat Pembayaran" + "Lihat Semua"
  - Filter chips horizontal:
    Semua | Lunas | Menunggu
  - History card per item:
    - Icon circle (check/pending)
    - Nama tagihan + tanggal
    - Jumlah + status badge (Lunas/Menunggu)
    - Tap → DetailBuktiBayar
```

**Data Required:**
- Tagihan aktif (student_fees where status = 'belum_bayar' untuk active child)
- Riwayat pembayaran (payments untuk active child, filter by status)

**Data Source:**
```
GET /api/parent/student-fees?student_id={id}
  → [{ payment_title_name: "SPP", amount: 350000, due_date, status: "belum_bayar" }]

GET /api/parent/payments?student_id={id}&status=success
  → [{ order_id, gross_amount, status: "success", paid_at, payment_title_name }]
```

**Tasks:**
```
□ Implement PembayaranSppScreen
□ Implement PembayaranViewModel
□ Implement PaymentRepository.getPendingPayment()
□ Implement PaymentRepository.getPaymentHistory()
□ Implement filter chips functionality
□ Implement payment status polling
□ Loading skeleton
□ Empty state (semua lunas)
```

---

### P4-T2: Pilih Metode Bayar Screen

**Screen:** `SCR-023`
**Owner:** Payment Agent
**Status:** ✅ DONE

**Visual Reference:** `pilih_metode_bayar/screen.png`

**UI Specification:**
```
TopAppBar:
  - Back + "Detail Tagihan & Pilih Metode"

Bill Summary Card:
  - Nama tagihan, bulan, jumlah
  - Itemized: SPP + Iuran + Admin Fee
  - Total

Payment Method Section:
  - NOTE: Metode pembayaran ditampilkan oleh Midtrans Snap WebView
  - Client memanggil POST /dashboard/midtrans/snap-token/{payment} untuk mendapatkan Snap token
  - Buka Midtrans Snap dalam WebView — Snap menampilkan semua metode yang tersedia:
    □ Virtual Account (BSI, BRI, BNI, Mandiri, BCA)
    □ QRIS
    □ E-Wallet (GoPay, OVO, Dana, ShopeePay)
    □ Convenience Store (Indomaret, Alfamart)
  - Setelah pembayaran selesai, Snap redirect ke callback URL
  - Client polling status via GET /dashboard/midtrans/status/{chargeId}

Continue Button:
  - "Lanjutkan Pembayaran" (52dp, Secondary, full width)
  - Disabled jika belum pilih metode
```

**Tasks:**
```
□ Implement PilihMetodeBayarScreen
□ Implement bank VA list
□ Implement QRIS option
□ Implement method selection state
□ Navigate ke ProsesPembayaran dengan parameter
```

---

### P4-T3: Proses Pembayaran Screen

**Screen:** `SCR-024`
**Owner:** Payment Agent + Security Agent
**Status:** ✅ DONE

**Visual Reference:** `proses_pembayaran/screen.png`

**UI Specification:**
```
Jika Bank Transfer (Virtual Account):
  - TopAppBar: Back + "Instruksi Pembayaran"
  - Bank logo
  - "Nomor Virtual Account"
  - VA Number (large, monospace, copyable)
  - Copy button
  - Countdown timer (24 jam)
  - Bill amount
  - Step-by-step instructions:
    1. Buka mobile banking / ATM
    2. Pilih Transfer Virtual Account
    3. Masukkan nomor VA
    4. Konfirmasi pembayaran

Jika QRIS:
  - TopAppBar: Back + "Scan QR Code"
  - QR Code image (besar, 240dp)
  - "Scan dengan aplikasi e-wallet atau m-banking"
  - Refresh QR button (QR berlaku 15 menit)
  - Timer countdown

Payment Status Polling:
  - Cek status setiap 5 detik
  - Saat paid: navigate ke PembayaranBerhasil
  - Saat expired: navigate kembali dengan error message
```

**Backend: Laravel MidtransService (existing di server)**
```php
// MidtransService.php sudah ada di Laravel:
// 1. getSnapToken(Payment $payment) → Snap token untuk WebView
// 2. createTransaction(Payment $payment) → Midtrans transaksi
// 3. processCallback(array $data) → Handle webhook
// 4. getTransactionStatus(string $orderId) → Cek status
```

**Payment Flow via Midtrans Snap:**
```
1. Client request Midtrans Snap token → POST /dashboard/midtrans/snap-token/{payment}
2. Laravel return snap_token + redirect_url
3. Client buka Midtrans Snap WebView (Snap.js) — user pilih metode bayar
4. Midtrans handle pembayaran (VA, QRIS, E-Wallet) via Snap UI
5. User selesai bayar → Midtrans redirect ke callback URL
6. Midtrans kirim webhook ke POST /midtrans/notification → Laravel update status
7. Client polling GET /dashboard/midtrans/status/{chargeId} for status update
8. Atau realtime via Pusher/Reverb jika di-setup
```

**Tasks:**
```
□ Implement ProsesPembayaranScreen
□ Implement Midtrans Snap WebView (load Snap.js URL)
□ Implement Virtual Account display (fallback jika tanpa Snap)
□ Implement QRIS display (fallback)
□ Implement VA number copy functionality
□ Implement countdown timer
□ Implement status polling (coroutine + delay) ke endpoint Laravel
□ Test end-to-end payment flow (sandbox)
```

**Security:**
```
□ Payment amount validation server-side (Laravel sudah handle)
□ Idempotency key untuk prevent double payment
□ Verify payment ownership (student belongs to parent)
□ Tidak expose Midtrans server key ke client (hanya client_key untuk Snap.js)
```

---

### P4-T4: Midtrans Webhook Handler

**Owner:** Payment Agent
**Status:** ⬜ TODO

**Backend: Laravel MidtransController (sudah ada)**
```php
// MidtransController.php (route: POST /midtrans/notification — tanpa auth)
// 1. Verify Midtrans signature (hash SHA512)
// 2. Parse notification body dari Midtrans
// 3. Update payment status di DB
// 4. Trigger notification ke parent (database + FCM)
// 5. Return 200 OK
```

**Tasks:**
```
□ Webhook endpoint sudah ada di Laravel (POST /midtrans/notification)
□ Pastikan Midtrans dashboard dikonfigurasi dengan webhook URL yang benar
□ Implement payment status polling di client (GET /dashboard/midtrans/status/{chargeId})
□ Handle edge cases: duplicate notifications, unknown order
□ Test dengan Midtrans sandbox simulator
```

---

### P4-T5: Pembayaran Berhasil Screen

**Screen:** `SCR-025`
**Owner:** Payment Agent
**Status:** ✅ DONE

**Visual Reference:** `pembayaran_berhasil/screen.png`

**UI Specification:**
```
Full screen success:
  - Success animation (Lottie checkmark atau animated drawable)
  - "Pembayaran Berhasil!" (headline-lg, Primary)
  - "Terima kasih atas pembayaran Anda"
  - Summary card:
    - Nama tagihan
    - Jumlah
    - Metode pembayaran
    - Tanggal & waktu
    - Transaction ID
  - Two buttons:
    □ "Lihat Bukti Bayar" (Primary)
    □ "Kembali ke Beranda" (Outline)
```

---

### P4-T6: Detail Bukti Bayar Screen

**Screen:** `SCR-026`
**Owner:** Payment Agent
**Status:** ✅ DONE

**Visual Reference:** `detail_bukti_bayar/screen.png`

**UI Specification:**
```
TopAppBar:
  - Back + "SDM3 Payment"
  - Avatar + notification

Transaction status:
  - check_circle icon (80dp, StatusSuccess/10)
  - "Pembayaran Berhasil"
  - "Terima kasih atas pembayaran Anda"

Receipt Card (white, rounded 16dp):
  - Transaction ID: "SDM3-XXXXXXXX"
  - Status badge: "Berhasil"
  - Tanggal & Waktu
  - Metode: "Bank Transfer (BSI)"
  - Dashed divider
  - Itemized breakdown:
    - SPP item + harga
    - Iuran Gedung + harga
    - Admin Fee + harga
  - Dashed divider
  - Total: "Rp XXX.XXX" (headline-lg, SchoolGreenDark)

Student info chip:
  - Avatar anak
  - "Pembayaran untuk:" label
  - Nama anak
  - Kelas + NIS

Action buttons:
  - Download PDF Bukti
  - Bagikan (Share)
  - Kembali ke Beranda
```

**Tasks:**
```
□ Implement DetailBuktiBayarScreen
□ Generate PDF receipt (PDFDocument)
□ Implement share functionality (System share sheet)
□ Implement download to device
□ Fetch payment detail by ID
```

---

## PHASE 5 — KEHADIRAN MODULE

**Agent Utama:** Feature Agent + Data Agent
**Estimasi:** 5 hari kerja
**Dependensi:** Phase 2, Attendance table populated

### P5-T1: Kehadiran Siswa Screen

**Screen:** `SCR-017`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `kehadiran_siswa/screen.png`

**UI Specification:**
```
TopAppBar:
  - Avatar + "Kehadiran" title
  - Student name (subtitle under title)
  - Notification bell

Month Selector:
  - Pill button: calendar icon + "Oktober 2023" + chevron_down
  - Warna legend: ● green ● amber ● blue ● red

Summary Grid (2x2 Bento):
  - Hadir: check_circle (StatusSuccess), border-left 4dp
  - Sakit: medication (StatusWarning), border-left 4dp
  - Izin: event_available (Secondary), border-left 4dp
  - Alpa: cancel (StatusDanger), border-left 4dp
  - Setiap card: label + angka hari + icon

Mini Calendar:
  - Title "Ringkasan Bulan Ini" + prev/next nav
  - Grid 7 kolom (M S S R K J S)
  - Setiap hari sekolah: dot indicator warna status
  - Hari Minggu: merah muted (libur)
  - Today: Primary/10 bg

Detail Log (optional expand):
  - Tap pada tanggal → detail (nama siswa, status, catatan guru)

Attendance Legend:
  - ● Hadir (hijau)
  - ● Sakit (kuning)
  - ● Izin (biru)
  - ● Alpa (merah)
  - ○ Libur
```

**Tasks:**
```
□ Implement KehadiranSiswaScreen
□ Implement month picker (DatePicker bottom sheet)
□ Implement 2x2 summary grid
□ Implement mini calendar composable
□   - Calculate attendance per day
□   - Color code per status
□   - Navigate month prev/next
□ Implement AttendanceRepository.getMonthlyAttendance()
□ Implement AttendanceViewModel
□ Implement day detail dialog
□ Export attendance report (opsional)
```

**Acceptance Criteria:**
- [ ] Calendar akurat menampilkan data attendance
- [ ] Summary total sesuai data
- [ ] Month navigation bekerja
- [ ] Hari libur/weekend dimunculkan berbeda
- [ ] Tap hari → detail popup

---

## PHASE 6 — INFO ANAK & KEGIATAN MODULE

**Agent Utama:** Feature Agent + Data Agent
**Estimasi:** 6 hari kerja
**Dependensi:** Phase 2

### P6-T1: Detail Info Anak Screen

**Screen:** `SCR-018`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `detail_info_anak/screen.png`

**UI Specification:**
```
TopAppBar:
  - Back button + "Detail Anak"
  - Share button (right)

Profile Header Card:
  - Decorative background: SecondaryContainer/20 circle blur
  - Avatar anak (96dp circular, border-4 white, shadow)
  - Status indicator dot (bottom-right: StatusSuccess green)
  - Nama anak (headline-md bold)
  - Kelas: "Kelas 4-A (Ibnu Sina)" (Secondary)
  - NISN chip + Status chip

Info Grid (2 kolom):
  - Tempat, Tgl Lahir
  - Jenis Kelamin
  - Wali Kelas
  - ID Pelajar

Quick Nav Hub (2x2 grid):
  - Nilai (grade icon, Secondary)
  - Rapor (description icon, Primary)
  - Kehadiran (calendar icon, StatusWarning)
  - Kesehatan (health_and_safety icon, Tertiary)

Academic Summary Card:
  - Header: Primary bg + "Ringkasan Akademik" + semester
  - Fase Kurikulum: "Fase B — Merdeka Belajar"
  - Rata-rata nilai: score + predicate
  - Jumlah mapel
  - Status kelulusan/kenaikan kelas
```

**Tasks:**
```
□ Implement DetailInfoAnakScreen
□ Implement StudentRepository.getStudentDetail()
□ Implement academic summary aggregation
□ Quick nav to child screens
□ Share student info functionality
```

---

### P6-T2: Kegiatan Program Screen

**Screen:** `SCR-019`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `kegiatan_program/screen.png`

**UI Specification:**
```
TopAppBar:
  - Avatar + "SDM3 Parent"
  - Notification bell

Hero header:
  - "Eksplorasi & Bakat" (headline-lg)
  - "Pantau perkembangan minat dan program keagamaan ananda."

Custom Tab Switcher:
  - Pill container (SurfaceContainerLow)
  - "Ekstrakurikuler" | "Program Unggulan"
  - Active: Primary bg pill (animated slide)

Ekstrakurikuler Tab:
  - Card per ekskul:
    - Icon 48dp (sport/art icon, SecondaryContainer/30)
    - Nama ekskul (title-lg)
    - Pelatih (label-md, muted)
    - Grade chip (A/B+)
    - Coach note (italic, bordered left Primary)

Program Unggulan Tab:
  - Program 1: Tahfiz Quran
    - Juz progress indicator
    - Hafalan surat terakhir
    - Ustadz/ah pembimbing
  - Program 2: Sholat Berjamaah
    - Persentase kehadiran jamaah
    - Progress bar
  - Program 3: Bahasa Arab Dasar
    - Level saat ini
    - Score akhir
```

**Tasks:**
```
□ Implement KegiatanProgramScreen
□ Implement animated tab switcher
□ Implement EkskulCard composable
□ Implement ProgramCard composable
□ Implement ExtracurricularRepository
□ Implement ProgramRepository
□ Test tab animation
```

---

## PHASE 7 — NOTIFIKASI & PENGUMUMAN MODULE

**Agent Utama:** Feature Agent + Data Agent
**Estimasi:** 5 hari kerja

### P7-T1: Notifikasi Screen

**Screen:** `SCR-021`, `SCR-022`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `notifikasi/`, `notifikasi_kosong/`

**UI Specification:**
```
TopAppBar:
  - Back button + "Notifikasi" title
  - "done_all" mark-all-read button (right)

Filter Chips (horizontal scroll):
  Semua | Nilai | Pembayaran | Pengumuman
  Active: Secondary bg, white text, rounded-full
  Inactive: white bg, border OutlineVariant

Notification List:
  Unread notification:
    - border-left 4dp (warna sesuai type)
    - White card + shadow
    - Icon circle (52dp)
    - Title (body-lg bold)
    - Timestamp (label-md, muted, right)
    - Body preview (body-md, 2-line clamp)
    - Unread dot: 10dp Secondary circle (top-right)

  Read notification:
    - Same card tanpa unread dot
    - Title & body: opacity 70%

  Date separator: "Kemarin", "Minggu lalu" (label-lg, outline)

  Promo/Announcement Bento Card:
    - Gradient bg (Primary → SchoolGreenDark)
    - Program badge + title
    - Description
    - CTA Button (white, rounded 12dp)

Empty State:
  - Bell illustration
  - "Belum ada notifikasi"
  - Refresh button
```

**Data Source:**
```
GET /api/parent/notifications → { data: [ { id, type, title, message, data: { reference_id }, read_at, created_at } ] }
PATCH /api/parent/notifications/{id}/read → mark as read
Filter: ?type=nilai|pembayaran|pengumuman&unread_only=true
```

**Realtime:**
```
Untuk notifikasi realtime, gunakan Pusher/Laravel Reverb WebSocket.
Laravel broadcasting events:
  - App\\Events\\PaymentStatusUpdated → pembayaran
  - App\\Events\\NewGradePosted → nilai baru
  - App\\Events\\NewNotification → notifikasi umum
Client: Ktor WebSocket atau Pusher Kotlin SDK
Fallback: polling setiap 30 detik
```

**Tasks:**
```
□ Implement NotifikasiScreen
□ Implement filter chips dengan animation
□ Implement NotificationCard (read/unread state)
□ Implement DateSeparator composable
□ Implement PromoCard (Bento style)
□ Implement mark-as-read on tap
□ Implement mark-all-read
□ Implement NotificationRepository (Laravel REST API)
□ Implement Pusher/Reverb WebSocket untuk notif realtime
□ FCM handling: background + foreground
□ Empty state
□ Notification badge update di BottomNav
```

---

### P7-T2: Pengumuman Sekolah Screen

**Screen:** `SCR-020`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `pengumuman_sekolah/screen.png`

**UI Specification:**
```
TopAppBar:
  - Back + "Pengumuman Sekolah"
  - Search icon

Category filter chips:
  Semua | Umum | Akademik | Keuangan | Kegiatan

Announcement cards (vertical list):
  - Image (96dp left, rounded 16dp)
  - Date (label-md, muted)
  - Title (label-lg bold, 2-line clamp)
  - Category badge (small pill)
  - Tap → detail screen (WebView atau text detail)
```

**Tasks:**
```
□ Implement PengumumanSekolahScreen
□ Implement category filter
□ Implement AnnouncementCard
□ Implement AnnouncementRepository
□ Implement search functionality
□ Implement detail view (simple text/WebView)
```

---

### P7-T3: Push Notification Setup

**Owner:** Data Agent + Security Agent
**Status:** ⬜ TODO

**Push Notification Architecture:**
```
FCM (Firebase Cloud Messaging) terintegrasi dengan Laravel notification system:
1. Laravel mengirim notifikasi via FCM channel (laravel-notification-channels/fcm)
2. Atau via firebase-php (kreait/firebase-php)
3. Client menerima FCM di androidApp → handle foreground/background

Alternatif: Firebase Cloud Functions (jika ingin terpisah dari Laravel)
```

**Tasks:**
```
□ Setup Firebase project
□ Integrate FCM SDK di androidApp
□ Implement FCM token registration → kirim ke Laravel untuk disimpan
□ Implement foreground notification handler
□ Implement background notification handler (tap → navigate)
□ Implement notification channels (Android):
  - channel_nilai: "Nilai Siswa"
  - channel_pembayaran: "Pembayaran"
  - channel_pengumuman: "Pengumuman Sekolah"
  - channel_kehadiran: "Kehadiran"
□ Test FCM di physical device
```

---

## PHASE 8 — PROFIL & PENGATURAN MODULE

**Agent Utama:** Feature Agent
**Estimasi:** 4 hari kerja

### P8-T1: Profil Akun Screen

**Screen:** `SCR-014`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `profil_akun/screen.png`

**UI Specification:**
```
TopAppBar:
  - Avatar + "SDM3 Parent"
  - Notification bell

Parent Header Card:
  - Avatar foto orang tua (80dp circular, border-2 Primary)
  - Verified badge (green checkmark, bottom-right)
  - Nama orang tua (headline-lg-mobile)
  - Nomor HP (body-md, muted)
  - Edit button (SecondaryContainer, rounded-full)

Data Anak Section:
  - Title "Data Anak" + "Lihat Semua"
  - Child cards (list):
    - Avatar 48dp + nama + kelas
    - Status chip "Aktif"
    - Chevron right
    - Tap → DetailInfoAnak

Settings & Info Section (white card list):
  - Notifikasi (notifications_active icon) → NotifikasiSettings
  - Bahasa (language icon) → "Bahasa Indonesia" (sementara tidak bisa ganti)
  - Tentang Aplikasi (info icon)
  - Kebijakan Privasi (security icon)
  - Hubungi Sekolah (contact_support icon) → WhatsApp
  - Versi Aplikasi (right-aligned: "2.x.x (Build XXX)")

Logout Button:
  - "Keluar Akun" (52dp, border-2 StatusDanger, text StatusDanger)
  - Tap → Dialog Konfirmasi Keluar

Footer: "© 2024 SDM3 Parent Portal"
```

**Tasks:**
```
□ Implement ProfilAkunScreen
□ Implement edit profil (nama, avatar upload)
□ Avatar upload ke Laravel (FileUploadService → public disk) atau R2
□ Child list dari GET /api/parent/students (filter by students.user_id)
□ Settings navigation
□ Logout dengan confirmation dialog
□ Clear session & token on logout
```

---

### P8-T2: Dialog Konfirmasi Keluar

**Dialog:** `DLG-001`
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `dialog_konfirmasi_keluar/screen.png`

**UI Specification:**
```
AlertDialog (Material 3):
  - Title: "Keluar Akun?"
  - Content: "Anda akan keluar dari SDM3 Parent Portal. Apakah Anda yakin?"
  - Actions:
    - "Batal" (TextButton, Secondary)
    - "Keluar" (FilledButton, StatusDanger bg, white text)
```

---

### P8-T3: Pengaturan Notifikasi

**Screen:** `SCR-020` (sheet atau full screen)
**Owner:** Feature Agent
**Status:** ✅ DONE

**Visual Reference:** `pengaturan_notifikasi/screen.png`

**UI Specification:**
```
TopAppBar: Back + "Pengaturan Notifikasi"

Toggle sections:
  - Notifikasi Push (master toggle)
  - Nilai Baru (toggle)
  - Tagihan Baru (toggle)
  - Pengumuman Sekolah (toggle)
  - Kehadiran (toggle)
  - Rapor Tersedia (toggle)
  
  Setiap toggle: icon + label + switch (Material 3 Switch)
  Disabled jika master toggle off
```

---

## PHASE 9 — SECURITY HARDENING

**Agent Utama:** Security Agent
**Estimasi:** 5 hari kerja
**Dependensi:** Semua feature phase selesai

### P9-T1: SSL Certificate Pinning

**Owner:** Security Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Extract SSL certificate fingerprint (domain: admin.sdm3.sch.id)
□ Implement Network Security Config (Android):
  android:networkSecurityConfig
  <domain-config cleartextTrafficPermitted="false">
    <domain includeSubdomains="true">admin.sdm3.sch.id</domain>
    <pin-set>
      <pin digest="SHA-256">AAAA...base64</pin>
      <pin digest="SHA-256">BBBB...base64</pin> (backup pin)
    </pin-set>
  </domain-config>
□ Implement certificate pinning di Ktor (OkHttp CertificatePinner)
□ Test pinning rejection dengan wrong cert
□ Implement pin rotation strategy (backup pin + update mechanism)
```

---

### P9-T2: Encrypted Local Storage

**Owner:** Security Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Implement KVault wrapper untuk semua sensitive data
□ Encrypt: JWT token, refresh token, user_id
□ Encrypt: FCM token
□ Encrypt: selected child session
□ Implement SecurePreferences (Android EncryptedSharedPreferences fallback)
□ Implement data wipe on logout
□ Implement data wipe after 5 failed login attempts
□ Test storage tidak terbaca tanpa decryption
```

---

### P9-T3: Biometric Authentication

**Owner:** Security Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Implement BiometricManager (Android BiometricPrompt)
□ Opsi enable/disable biometric di profil
□ Prompt biometric saat app resume dari background (setelah 5 menit)
□ Fallback ke PIN/password jika biometric gagal
□ Store biometric enabled state di EncryptedPreferences
```

---

### P9-T4: Session Management

**Owner:** Security Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Implement TokenManager (KVault)
□ Implement session expiry check: cookie session lifetime (config/session.php → lifetime)
□ Untuk cookie auth: auto-redirect ke login jika session expired (419 error)
□ Untuk token auth: generate token baru via POST /api/sanctum/token jika perlu
□ Implement session expiry check on app resume
□ Implement force logout jika refresh token expired
□ Implement concurrent session detection (opsional)
□ Log security events ke Sentry (tanpa sensitive data)
```

---

### P9-T5: OWASP Mobile Security Checklist

**Owner:** Security Agent
**Status:** ⬜ TODO

**Checklist:**
```
□ M1 - Improper Credential Usage
  □ No hardcoded credentials
  □ No credentials in logs
  □ Credentials tidak di-bundle dalam APK

□ M2 - Inadequate Supply Chain Security
  □ Semua dependency dari trusted source
  □ Checksums verified di Gradle

□ M3 - Insecure Authentication
  □ OTP 6 digit dengan expiry
  □ Rate limiting login attempts
  □ Secure session management

□ M4 - Insufficient Input/Output Validation
  □ Phone number format validation
  □ Amount validation server-side
  □ SQL injection prevention (Laravel Eloquent parameterized queries)

□ M5 - Insecure Communication
  □ HTTPS only
  □ Certificate pinning
  □ No cleartext traffic

□ M6 - Inadequate Privacy Controls
  □ Minimal data collection
  □ Privacy policy screen
  □ Data deletion capability

□ M7 - Insufficient Binary Protection
  □ ProGuard/R8 obfuscation enabled
  □ Kotlin reflection disabled in release
  □ APK signing verified

□ M8 - Security Misconfiguration
  □ Debug mode disabled in release
  □ Logging disabled in release
  □ No debug endpoints accessible

□ M9 - Insecure Data Storage
  □ Sensitive data di EncryptedSharedPreferences
  □ No sensitive data di SharedPreferences plain
  □ No sensitive data di external storage

□ M10 - Insufficient Cryptography
  □ AES-256 untuk local encryption
  □ No deprecated crypto algorithms
  □ Secure random number generation
```

---

## PHASE 10 — TESTING & QA

**Agent Utama:** QA Agent
**Estimasi:** 6 hari kerja

### P10-T1: Unit Tests (Domain Layer)

**Owner:** QA Agent
**Status:** ⬜ TODO

**Test Coverage Target:** ≥80% domain layer

```kotlin
// Example test structure
// NilaiUseCaseTest.kt
class GetGradesByStudentUseCaseTest {
    @Test
    fun `returns grades list on success`()
    @Test
    fun `returns empty list when no grades`()
    @Test
    fun `returns error on network failure`()
    @Test
    fun `filters grades by type correctly`()
    @Test
    fun `calculates average score correctly`()
}

// PaymentUseCaseTest.kt
class CreatePaymentUseCaseTest {
    @Test
    fun `creates payment with correct amount`()
    @Test
    fun `validates amount is positive`()
    @Test
    fun `prevents duplicate payment for same billing`()
    @Test
    fun `returns error on Midtrans failure`()
}

// AttendanceUseCaseTest.kt
class GetMonthlyAttendanceUseCaseTest {
    @Test
    fun `returns attendance for given month`()
    @Test
    fun `counts hadir, sakit, izin, alpa correctly`()
    @Test
    fun `handles empty month`()
}
```

**Tasks:**
```
□ Write tests untuk semua UseCase (100% coverage target)
□ Write tests untuk Repository implementations
□ Write tests untuk mappers (DTO → Entity)
□ Write tests untuk validators
□ Setup MockK untuk mocking
□ Setup test fixtures (sample data classes)
```

---

### P10-T2: UI Tests (Compose)

**Owner:** QA Agent
**Status:** ⬜ TODO

**Key UI Test Scenarios:**
```
□ LoginScreen: input phone, password, tap Masuk
□ OTPScreen: input 6 digit, auto-submit, resend timer
□ DashboardScreen: tampil data anak, tap Bayar Sekarang
□ PembayaranScreen: pilih metode, confirm payment
□ KepuasanScreen: navigate to all bottom nav tabs
□ Empty states: nilai kosong, notif kosong
□ Error states: network error, retry functionality
□ Pull-to-refresh
□ Bottom sheet functionality
```

---

### P10-T3: Performance Testing

**Owner:** QA Agent
**Status:** ⬜ TODO

**Targets:**
```
□ Cold start: <2 detik ke splash
□ Dashboard load: <3 detik dengan cache
□ Navigation transition: <150ms
□ PDF preview open: <3 detik
□ Payment create: <5 detik (server round-trip)
□ Memory usage: <150MB normal operation
□ Network: semua request <2 detik pada 4G
```

**Tools:** Android Profiler, Compose Metrics

---

### P10-T4: Device Testing Matrix

**Owner:** QA Agent
**Status:** ⬜ TODO

| Device | Android Version | Resolution | Status |
|---|---|---|---|
| Samsung Galaxy A15 | Android 13 | 1080x2340 | ⬜ |
| Xiaomi Redmi Note 12 | Android 13 | 1080x2400 | ⬜ |
| Samsung Galaxy A54 | Android 14 | 1080x2340 | ⬜ |
| Oppo A98 | Android 13 | 1080x2400 | ⬜ |
| Realme C51 | Android 13 | 720x1600 | ⬜ |

---

### P10-T5: Accessibility Testing

**Owner:** QA Agent
**Status:** ⬜ TODO

```
□ Screen reader (TalkBack): semua elemen punya contentDescription
□ Minimum touch target 48dp pada semua interactive elements
□ Color contrast ratio ≥4.5:1 untuk text
□ Dynamic font size support
□ High contrast mode support
□ No animation untuk reduce-motion preference
```

---

## PHASE 11 — CI/CD & DEPLOYMENT

**Agent Utama:** DevOps Agent
**Estimasi:** 4 hari kerja

### P11-T1: GitHub Actions Pipelines

**Owner:** DevOps Agent
**Status:** ⬜ TODO

**Workflows:**
```yaml
# .github/workflows/android-pr.yml
# Trigger: PR ke develop
Steps:
  1. Checkout code
  2. Setup JDK 17
  3. Create local.properties (API keys)
  4. Run Gradle build (debug)
  5. Run unit tests
  6. Upload test results
  7. Comment PR dengan test coverage

# .github/workflows/android-release.yml
# Trigger: Push ke main
Steps:
  1. Checkout code
  2. Setup JDK 17
  3. Build release APK (signed with keystore dari secrets)
  4. Run all tests
  5. Upload to Firebase App Distribution (internal)
  6. Create GitHub release
  7. Notify Slack/Discord
```

---

### P11-T2: Signing Configuration

**Owner:** DevOps Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Generate release keystore
□ Store keystore di GitHub Secrets (base64 encoded)
□ Store STORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD di Secrets
□ Implement signing config di build.gradle.kts
□ Test signed APK installable
□ Setup ProGuard rules (retain Ktor, Koin, kotlinx-serialization)
```

---

### P11-T3: Play Store Preparation

**Owner:** DevOps Agent
**Status:** ⬜ TODO

**Tasks:**
```
□ Buat Google Play Developer account
□ Siapkan listing Play Store:
  □ App name: "SDM3 Parent - Portal Orang Tua"
  □ Short description (80 karakter)
  □ Full description
  □ Screenshots (phone: min 2, max 8)
  □ Feature graphic (1024x500)
  □ Icon 512x512
□ Konfigurasi content rating
□ Siapkan Privacy Policy URL
□ Submit ke Internal Testing track
□ Upgrade ke Closed Testing → Open Testing → Production
```

---

### P11-T4: Environment Configuration

**Owner:** DevOps Agent
**Status:** ⬜ TODO

**Environments:**

| Env | Laravel API URL | Midtrans | FCM |
|---|---|---|---|
| Development | `https://dev.admin.sdm3.sch.id` | Sandbox | Test key |
| Staging | `https://staging.admin.sdm3.sch.id` | Sandbox | Test key |
| Production | `https://admin.sdm3.sch.id` | Production | Prod key |

**Tasks:**
```
□ Implement BuildConfig fields per env
□ Flavors: dev, staging, prod
□ Sentry environment tagging
□ Posthog separate project per env
□ API base URL per flavor di build.gradle.kts
```

---

## 20. PROGRESS TRACKER MASTER

> Update tabel ini setiap akhir hari kerja. Format: Tanggal (YYYY-MM-DD), Agent yang mengerjakan, Status baru.

### Phase 0 — Foundation

| Task ID | Task Name | Agent | Status | Started | Completed | Notes |
|---|---|---|---|---|---|---|---|
| P0-T1 | KMP Project Init | Architect | ✅ DONE | 2026-06-20 | 2026-06-20 | KMP project, build.gradle.kts, libs.versions.toml, Gradle 9.0, Kotlin 2.4.0, Compose 1.11.1 |
| P0-T2 | Laravel API Endpoints Setup | Data | ⬜ TODO | - | - | Backend — perlu setup di Laravel |
| P0-T3 | Core Network & DI | Architect | ✅ DONE | 2026-06-20 | 2026-06-20 | HttpClientProvider, ApiResult, BaseViewModel, Koin modules, KVault Factory |
| P0-T4 | Design System Foundation | Design | ✅ DONE | 2026-06-20 | 2026-06-20 | Color, Typography, Shape, Spacing, SDM3Theme, Sdm3Button, Sdm3TextField, Sdm3Card, StatusChip |
| P0-T5 | CI/CD Basic | DevOps | ✅ DONE | 2026-06-20 | 2026-06-20 | GitHub Actions CI + Release workflows |

### Phase 1 — Auth

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P1-T1 | Splash Screen | SCR-001 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P1-T2 | Onboarding Flow | SCR-002,003,004 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P1-T3 | Login Screen | SCR-005 | Security+Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P1-T4 | Verifikasi OTP | SCR-006 | Security+Feature | ✅ DONE | 2026-06-20 | 2026-06-20 | Screen + ViewModel + AuthApi methods |
| P1-T5 | Pilih Anak | SCR-007 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 2 — Dashboard

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P2-T1 | Bottom Navigation | - | Design | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P2-T2 | Dashboard Beranda | SCR-008 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P2-T3 | Child Selector Sheet | SHT-001 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 3 — Nilai & Rapor

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P3-T1 | Nilai List | SCR-009,010 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P3-T2 | Detail Nilai | SCR-015 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P3-T3 | Tab Formatif | SCR-016 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P3-T4 | Rapor Resmi | SCR-013 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P3-T5 | Preview PDF | SCR-027 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P3-T6 | Verifikasi QR | SCR-028 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 4 — Pembayaran

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P4-T1 | Pembayaran List | SCR-011,012 | Payment | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P4-T2 | Pilih Metode | SCR-023 | Payment | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P4-T3 | Proses Bayar | SCR-024 | Payment+Security | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P4-T4 | Midtrans Webhook | Backend | Payment | ⬜ TODO | - | - |
| P4-T5 | Pembayaran Berhasil | SCR-025 | Payment | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P4-T6 | Detail Bukti Bayar | SCR-026 | Payment | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 5 — Kehadiran

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P5-T1 | Kehadiran Siswa | SCR-017 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 6 — Info Anak

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P6-T1 | Detail Info Anak | SCR-018 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P6-T2 | Kegiatan Program | SCR-019 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 7 — Notifikasi

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P7-T1 | Notifikasi | SCR-021,022 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P7-T2 | Pengumuman | SCR-020 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P7-T3 | FCM Setup | - | Data+Security | ✅ DONE | 2026-06-20 | 2026-06-20 | FcmTokenProvider expect/actual + DI |

### Phase 8 — Profil

| Task ID | Task Name | Screen | Agent | Status | Started | Completed |
|---|---|---|---|---|---|---|---|
| P8-T1 | Profil Akun | SCR-014 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P8-T2 | Dialog Keluar | DLG-001 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P8-T3 | Pengaturan Notif | SHT-002 | Feature | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 9 — Security

| Task ID | Task Name | Agent | Status | Started | Completed |
|---|---|---|---|---|---|
| P9-T1 | SSL Pinning | Security | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P9-T2 | Encrypted Storage | Security | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P9-T3 | Biometric Auth | Security | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P9-T4 | Session Management | Security | ✅ DONE | 2026-06-20 | 2026-06-20 |
| P9-T5 | OWASP Checklist | Security | ✅ DONE | 2026-06-20 | 2026-06-20 |

### Phase 10 — Testing

| Task ID | Task Name | Agent | Status | Started | Completed |
|---|---|---|---|---|---|
| P10-T1 | Unit Tests | QA | ✅ DONE | 2026-06-20 | 2026-06-20 | ApiResultTest, SecureTokenManagerTest, LoginViewModelTest |
| P10-T2 | UI Tests | QA | ⬜ TODO | - | - |
| P10-T3 | Performance | QA | ⬜ TODO | - | - |
| P10-T4 | Device Matrix | QA | ⬜ TODO | - | - |
| P10-T5 | Accessibility | QA | ⬜ TODO | - | - |

### Phase 11 — CI/CD

| Task ID | Task Name | Agent | Status | Started | Completed |
|---|---|---|---|---|---|
| P11-T1 | GitHub Actions | DevOps | ✅ DONE | 2026-06-20 | 2026-06-20 | CI + Release workflows |
| P11-T2 | Signing Config | DevOps | ⬜ TODO | - | - |
| P11-T3 | Play Store | DevOps | ⬜ TODO | - | - |
| P11-T4 | Environments | DevOps | ⬜ TODO | - | - |

---

### Ringkasan Progress Keseluruhan

| Phase | Total Tasks | Done | In Progress | Skipped | Progress |
|---|---|---|---|---|---|---|
| Phase 0 — Foundation | 5 | 4 | 0 | 0 | 80% |
| Phase 1 — Auth | 5 | 5 | 0 | 0 | 100% |
| Phase 2 — Dashboard | 3 | 3 | 0 | 0 | 100% |
| Phase 3 — Nilai & Rapor | 6 | 6 | 0 | 0 | 100% |
| Phase 4 — Pembayaran | 6 | 5 | 0 | 0 | 83% |
| Phase 5 — Kehadiran | 1 | 1 | 0 | 0 | 100% |
| Phase 6 — Info Anak | 2 | 2 | 0 | 0 | 100% |
| Phase 7 — Notifikasi | 3 | 3 | 0 | 0 | 100% |
| Phase 8 — Profil | 3 | 3 | 0 | 0 | 100% |
| Phase 9 — Security | 5 | 5 | 0 | 0 | 100% |
| Phase 10 — Testing | 5 | 1 | 0 | 0 | 20% |
| Phase 11 — CI/CD | 4 | 1 | 0 | 0 | 25% |
| **TOTAL** | **48** | **39** | **0** | **0** | **81%** |

> **Catatan:** Selain screen tasks di atas, Data Layer (35 files: API services, DTOs, repositories) dan 18 ViewModels telah selesai diintegrasikan dan terkompilasi bersih pada 2026-06-20. Lihat `data/` dan `feature/*/*ViewModel.kt` untuk detail implementasi.

---

## 21. GLOBAL QUALITY & SECURITY RULES

> Aturan berikut berlaku untuk **SEMUA AGENT** tanpa terkecuali. Baca sebelum memulai task apapun.

```
=== ANTI-DUPLIKASI ===
□ JANGAN buat ulang kode yang sudah ada — cek existing files di scope masing-masing
□ Jika pola yang sama muncul ≥2x, WAJIB diekstrak ke function/class bersama
□ JANGAN copy-paste code — selalu refactor ke shared utility
□ Setiap agent WAJIB cek domain folder masing-masing sebelum buat file baru

=== ERROR HANDLING ===
□ Semua API/IO operations WAJIB di-wrap dalam Result<T> atau try-catch
□ JANGAN throw exception mentah — selalu mapping ke error domain yang user-friendly
□ Setiap screen WAJIB handle: Loading, Success, Error, Empty — TIDAK ADA YANG BOLEH SKIP
□ Network error WAJIB ada retry mechanism (bisa manual via button atau auto-retry)

=== SECURITY ===
□ JANGAN log sensitive data: password, token, OTP, nomor HP, email
□ JANGAN simpan sensitive data di SharedPreferences — WAJIB KVault (AES-256)
□ JANGAN hardcode API keys, secrets, atau credentials
□ Semua komunikasi WAJIB HTTPS — cleartext diblokir di production
□ Session expired (HTTP 419) → redirect ke LoginScreen — jangan tampilkan data

=== CODE QUALITY ===
□ JANGAN ada TODO/FIXME/HACK di kode yang di-commit — buat issue tracker
□ Setiap public function WAJIB ada KDoc — kecuali Composable sederhana
□ JANGAN gunakan !! (double-bang) — gunakan safe call (?.) atau Elvis (?:)
□ JANGAN abaikan Kotlin warning — fix semua warning sebelum commit
□ Naming convention sesuai Lampiran A

=== PRE-FLIGHT (sebelum coding) ===
☐ Baca ulang section relevan di dokumen ini
☐ Cek existing code yang sudah ada — jangan buat ulang
☐ Koordinasi dengan agent lain jika ada dependency

=== POST-GENERATION (setelah coding) ===
☐ Build success (./gradlew build)
☐ Tidak ada warning baru
☐ Tidak ada duplikasi kode
☐ Tidak ada sensitive data di logs
☐ Unit test untuk logic baru
```

---

## 22. MASTER PROMPT UNTUK SETIAP AGENT

> Gunakan prompt berikut setiap kali memulai sesi dengan AI assistant untuk masing-masing role. Copy-paste prompt yang relevan + task yang ingin dikerjakan.

---

### 🏗️ PROMPT: ARCHITECT AGENT

```
Kamu adalah Kotlin Multiplatform Architect Agent untuk proyek SDM3 Parent Portal.

KONTEKS PROYEK:
- App: Mobile parent portal untuk SD Muhammadiyah 3 Samarinda
- Stack: KMP + Compose Multiplatform, Laravel REST API (Sanctum), Koin DI, Ktor, SQLDelight
- Target: Android (primary), iOS (secondary)
- Architecture: Clean Architecture + MVVM + Repository Pattern

PERAN KAMU:
- Mendefinisikan dan menjaga arsitektur proyek
- Membuat module structure, Koin DI modules, base classes
- Mengelola libs.versions.toml
- Memastikan tidak ada circular dependencies
- Membuat interface contracts antar layer
- Mencegah duplikasi kode arsitektural (base classes, utils, extensions)
- Melakukan code review untuk perubahan arsitektur level

ATURAN KETAT — ANTI-DUPLIKASI & QUALITY:
1. Selalu gunakan Result<T> wrapper untuk semua API calls — jangan buat custom wrapper sendiri
2. Domain layer TIDAK boleh import data layer (cek imports sebelum commit)
3. Presentation layer TIDAK boleh import data layer langsung — harus lewat UseCase
4. Semua ViewModel extends BaseViewModel — jangan buat ViewModel dari nol
5. Setiap module harus punya KoinModule-nya sendiri — jangan gabung module besar
6. Gunakan kotlinx.serialization untuk semua serialization — jangan pakai Gson/Moshi
7. WAJIB handle coroutine scope dengan viewModelScope — jangan pakai GlobalScope
8. TIDAK boleh ada duplikasi: jika pola yang sama muncul >2x, buat abstraksi/base class
9. Setiap interface WAJIB punya documentation KDoc — tujuan kontrak jelas
10. Wajib cek tidak ada circular import dengan dependency graph sebelum merge
11. Navigation 3: gunakan @Serializable sealed interface untuk type-safe routes — jangan string routes
12. Gunakan KoinApplication + koinViewModel<T> untuk Compose Multiplatform (bukan koin-android)

PRE-FLIGHT CHECKLIST (sebelum generate kode):
☐ Apakah ada base class / existing interface yang bisa di-reuse?
☐ Apakah perubahan ini akan membuat circular dependency?
☐ Apakah arsitektur yang dipilih sesuai Clean Architecture (domain tak tahu data layer)?
☐ Apakah dependency version di libs.versions.toml sudah ada / perlu ditambah?

POST-GENERATION CHECKLIST (sebelum mark done):
☐ Tidak ada file duplikat (2 file dengan fungsi identik)
☐ Tidak ada import dari layer terlarang
☐ Semua Koin module terdaftar di startKoin
☐ Build success tanpa warning

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan output kode arsitektur, Koin module, dan dokumentasi tidak terpotong/ditruncate

TASK SAAT INI:
[MASUKKAN TASK YANG INGIN DIKERJAKAN]

Hasilkan:
1. Kode Kotlin yang production-ready — zero warning
2. KDoc untuk setiap interface/class publik
3. Error handling yang komprehensif (Result<T> di semua operasi)
4. Unit test stubs untuk setiap class baru
```

---

### 🎨 PROMPT: DESIGN SYSTEM AGENT

```
Kamu adalah Design System Agent untuk SDM3 Parent Portal.

DESIGN SYSTEM SDM3:
Primary (Green): #006B31 | Secondary (Blue): #006398 | Tertiary (Amber): #805200
Surface: #F8F9FB | SurfaceWhite: #FFFFFF | OnSurface: #191C1E
StatusSuccess: #22C55E | StatusWarning: #F59E0B | StatusDanger: #EF4444
SchoolGreenDark: #1A4D2E | SchoolGreenVibrant: #33B962

Typography:
- Headlines: Poppins (SemiBold/Medium)
- Body & Labels: Inter (Regular/Medium)

Shapes: 4dp (badge), 8dp (small), 12dp (button/input), 16dp (card), 24dp (sheet)
Spacing: 4/8/16/24/32dp

COMPONENT SPECS:
- Button Primary: 52dp height, 12dp radius, Secondary (#006398) bg
- Input: 52dp height, 12dp radius, OutlineVariant border, Secondary focus
- Card: 16dp radius, white bg, subtle shadow, OutlineVariant/10 border
- BottomNav: Surface bg, SecondaryContainer active pill, filled icon active
- StatusChip: Pill shape, color/10 bg, full color text

PERAN KAMU:
- Implement Composable components berdasarkan specs di atas
- WAJIB @Preview untuk setiap komponen
- Gunakan Material 3 sebagai base — EXTEND jangan OVERRIDE
- Semua komponen harus support enabled/disabled state + isLoading state
- Jamin tidak ada duplikasi komponen (cek existing components sebelum buat baru)
- Accessibility: contentDescription, Role, min touch target 48dp

ATURAN ANTI-DUPLIKASI & QUALITY:
1. TIDAK ada hardcoded Color, semuanya dari SDM3ColorScheme
2. TIDAK ada hardcoded TextStyle, semuanya dari SDM3Typography
3. WAJIB gunakan LocalContentColor untuk icon colors
4. WAJIB minimum touch target 48dp pada semua interactive element
5. Ikuti naming: Sdm3Button, Sdm3Card, Sdm3TextField, dll.
6. CEK existing component folder sebelum buat baru — jika komponen mirip sudah ada, EXTEND jangan DUPLIKAT
7. Setiap komponen punya parameter: modifier, enabled, colors (default dari theme)
8. Jangan buat wrapper untuk Material3 component jika tidak ada modifikasi visual — pakai langsung
9. Dark mode: semua color harus punya light/dark variant di SDM3ColorScheme

PRE-FLIGHT CHECKLIST (sebelum buat komponen):
☐ Apakah komponen ini sudah ada? (cek shared/core/designsystem/component/)
☐ Apakah komponen bisa dibangun dari kombinasi Material3 yang sudah ada?
☐ Apakah parameter sudah sesuai pattern komponen lain? (modifier first, enabled, colors)
☐ Apakah sudah ada @Preview untuk light + dark mode?

POST-GENERATION CHECKLIST:
☐ Tidak ada hardcoded color — semuanya dari Color.kt
☐ Tidak ada hardcoded font — semuanya dari Typography.kt
☐ @Preview visible tanpa error
☐ Min touch target 48dp untuk semua clickable element
☐ contentDescription ada untuk semua icon/image

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `stitch-design-taste` — semantic design system skill, sangat relevan untuk membangun design token + komponen konsisten
- `minimalist-ui` — warm monochrome, typographic contrast, flat bento grids — sesuai gaya SDM3
- `high-end-visual-design` — untuk memastikan komponen terlihat premium, bukan generic
- `design-taste-frontend` — anti-slop, memastikan output tidak terlihat templated
- `imagegen-frontend-mobile` — generate referensi visual screen mobile jika dibutuhkan

TASK SAAT INI:
[MASUKKAN KOMPONEN YANG INGIN DIBUAT]

Hasilkan Composable dengan:
1. Parameter yang lengkap dan sensible defaults
2. @Preview dengan berbagai state (enabled, disabled, loading, error)
3. Dokumentasi KDoc
4. Accessibility semantics (contentDescription, Role)
```

---

### ⚡ PROMPT: FEATURE AGENT

```
Kamu adalah Feature Development Agent untuk SDM3 Parent Portal.

SCREEN YANG DIKERJAKAN:
[MASUKKAN SCREEN ID DAN NAMA - contoh: SCR-008 Dashboard Beranda]

DESIGN REFERENCE:
File HTML dari Stitch design tool telah dianalisis. UI menggunakan:
- Material 3 + SDM3 custom design system
- Bottom Navigation: Beranda | Nilai | Bayar | Rapor | Profil
- Color: Primary Green #006B31, Secondary Blue #006398, Amber #805200
- Font: Poppins (heading), Inter (body)
- Cards: 16dp radius, white bg, subtle shadow
- Buttons: 52dp height, 12dp radius

TECH STACK:
- UI: Compose Multiplatform (Jetpack Compose compatible)
- Navigation: Navigation 3 (type-safe, @Serializable sealed interface)
- State: MVVM + StateFlow<UiState> — BUKAN remember/mutableState untuk data
- DI: Koin (koinViewModel<T>)
- Images: Coil 3

STATE MANAGEMENT PATTERN — ANTI-DUPLIKASI:
```kotlin
data class [Screen]UiState(
    val isLoading: Boolean = false,
    val data: [DataType]? = null,
    val error: String? = null,
    val isEmpty: Boolean = false
)

sealed class [Screen]Event {
    // User actions — jangan pakai lambda di parameter composable
}
```

ATURAN KETAT — ANTI-DUPLIKASI & SECURITY:
1. Setiap screen WAJIB punya 4 state: Loading, Success, Error, Empty
2. WAJIB implement pull-to-refresh pada semua screen dengan list data
3. HANYA gunakan komponen dari Design System Agent — jangan buat style sendiri
4. Scaffold + TopAppBar + BottomNav pada main screens
5. Edge-to-edge dengan WindowInsets handling (statusBar + navigationBar)
6. WAJIB handle back navigation dengan predictive back gesture
7. Material 3 ModalBottomSheet untuk semua bottom sheet
8. JANGAN simpan data API di remember/mutableState — semua data via ViewModel StateFlow
9. JANGAN gunakan rememberSaveable untuk data bisnis — hanya untuk UI transient (scroll pos, tab index)
10. LazyColumn WAJIB pakai key() parameter — hindari recomposition tidak perlu
11. JANGAN ada duplikasi kode antar screen — extract composable bersama ke komponen bersama
12. Navigation routes: @Serializable sealed interface — jangan string-based routes
13. Semua input user WAJIB divalidasi sebelum dikirim ke ViewModel
14. JANGAN log sensitive data (password, token, OTP) — Napier hanya untuk debug non-sensitive
15. WAJIB loading state dengan skeleton shimmer — jangan CircularProgressIndicator doang
16. Dependency: WAJIB load skill `high-end-visual-design` dan `design-taste-frontend` untuk anti-slop UI

PRE-FLIGHT CHECKLIST (sebelum coding screen):
☐ Apakah ada screen serupa yang sudah dibuat? Bisa re-use sebagian code?
☐ Apakah komponen yang dibutuhkan sudah ada di design system?
☐ Apakah API endpoint sudah siap? (koordinasi dengan Data Agent)
☐ Apakah route sudah terdaftar di navigation graph?

POST-GENERATION CHECKLIST:
☐ Semua 4 state (Loading/Success/Error/Empty) terimplementasi
☐ Pull-to-refresh berfungsi
☐ Tidak ada hardcoded string — semua dari string resources
☐ Back navigation berfungsi (predictive back gesture)
☐ Keyboard handling (IME actions, focus management)
☐ Screen tidak recompose berlebihan (cek dengan Layout Inspector)

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `imagegen-frontend-mobile` — generate visual reference mobile screen sebelum coding, sebagai panduan layout
- `full-output-enforcement` — memastikan seluruh screen/ViewModel code tidak terpotong
- `high-end-visual-design` — untuk memastikan output UI tidak terlihat generic/templated
- `design-taste-frontend` — anti-slop, layout yang intentional bukan default

TASK SAAT INI:
[MASUKKAN TASK DETAIL]

Hasilkan:
1. [Screen]Screen.kt — Composable screen lengkap (Loading/Success/Error/Empty)
2. [Screen]ViewModel.kt — ViewModel dengan StateFlow + event handling
3. [Screen]UiState.kt — State dan event data classes
4. Navigation route definition (@Serializable sealed)
5. Koin module entry untuk ViewModel
```

---

### 🗄️ PROMPT: BACKEND/DATA AGENT

```
Kamu adalah Backend & Data Agent untuk SDM3 Parent Portal.

BACKEND: Laravel REST API (Sanctum Auth + MySQL + File Storage)
CLIENT: Ktor HTTP client + Laravel Sanctum (cookie/token auth)
LOCAL DB: SQLDelight (offline cache)

DATABASE SCHEMA (sudah didefinisikan di Section 6.1):
Tables: users, students, student_classrooms, classrooms, academic_years,
teachers, teacher_classrooms, teacher_subjects, subjects, subject_tps,
grades, grade_components, grade_component_weights, student_attendances,
student_attendance_summaries, payment_titles, student_fees, payments,
articles, notifications, notification_preferences, extracurriculars,
student_extracurriculars, rapor_templates, rapor_sections, rapor_instances,
student_report_cards, student_p5_assessments, student_p5_scores, p5_dimensions,
p5_indicators, sekolahs, student_inklusi_report_cards

AUTH: Parent terhubung ke siswa via students.user_id = auth()->id()
Tidak ada tabel parent_students terpisah.
Endpoint API prefix: /api/parent/ — perlu dibuat di Laravel

API RESPONSE FORMAT:
```json
{
    "success": true,
    "data": { ... },
    "message": "Success",
    "code": 200,
    "timestamp": "2026-06-17T10:00:00Z"
}
```

REPOSITORY PATTERN — ANTI-DUPLIKASI:
```kotlin
// Domain layer - interface
interface GradeRepository {
    suspend fun getGradesByStudent(
        studentId: String,
        semester: String = "ganjil"
    ): Result<List<Grade>>
}

// Data layer - implementation
class GradeRepositoryImpl(
    private val remoteDataSource: GradeRemoteDataSource,
    private val localDataSource: GradeLocalDataSource,
    private val mapper: GradeMapper
) : GradeRepository {
    override suspend fun getGradesByStudent(...): Result<List<Grade>> {
        // 1. Try local cache (SQLDelight)
        // 2. If stale or empty, fetch remote (Ktor → Laravel API)
        // 3. Save to cache
        // 4. Return mapped domain entity
    }
}
```

DTO PATTERN — @Serializable dengan @SerialName:
```kotlin
@Serializable
data class GradeDto(
    val id: String,
    @SerialName("student_id") val studentId: String,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("subject_name") val subjectName: String,
    @SerialName("classroom_id") val classroomId: String,
    val semester: String,
    val score: Double?,
    val predicate: String?,
    // ... Laravel snake_case column names — JANGAN camelCase di sini
)
```

AUTH INTERCEPTOR:
```kotlin
class SanctumAuthInterceptor(
    private val tokenManager: TokenManager,
    private val isCookieAuth: Boolean = true    // true = SPA cookie, false = Bearer
) {
    override suspend fun interceptRequest(request: HttpRequestBuilder) {
        if (isCookieAuth) {
            // Cookies otomatis dikirim oleh Ktor HttpCookies plugin — jangan manual
        } else {
            val token = tokenManager.getToken()
            request.header("Authorization", "Bearer $token")
        }
    }
}
```

ATURAN KETAT — ANTI-DUPLIKASI & SECURITY:
1. WAJIB offline-first: cek cache (SQLDelight) dulu, lalu network
2. Cache invalidation: 5 menit untuk data dinamis (nilai, absen), 1 jam untuk data static (siswa, kelas)
3. WAJIB gunakan Result<T> untuk semua operasi — jangan throw exception langsung
4. TIDAK boleh expose raw DTO ke domain layer — harus lewat mapper
5. Semua API request menggunakan filter scope (students.user_id = auth user)
6. WAJIB handle Laravel API error codes: 401 (unauthorized), 403 (forbidden), 404 (not found), 419 (session expired), 422 (validation), 429 (rate limit), 500 (server error)
7. Error 419 (session expired) → trigger token refresh atau redirect ke login
8. JANGAN simpan token di plain SharedPreferences — WAJIB KVault encrypted
9. JANGAN ada duplikasi kode HTTP client — setiap feature share 1 HttpClientProvider
10. Cek existing repository sebelum buat baru — jangan duplikat query pattern
11. Setiap DTO harus punya file mapper sendiri — jangan taruh mapping di repository
12. SQLDelight: .sq file WAjIB dalam package com.sdm3.parent.cache — kompile error jika salah folder
13. Polling: minimal interval 30 detik — jangan <30 detik untuk non-realtime data
14. Jangan retrofit — gunakan Ktor HttpClient.install(ContentNegotiation) + kotlinx.serialization

PRE-FLIGHT CHECKLIST (sebelum coding repository):
☐ Apakah API endpoint sudah ready di Laravel? (cek daftar endpoint Section 6.4)
☐ Apakah DTO sudah sesuai response Laravel? (snake_case fields)
☐ Apakah perlu SQLDelight cache? (data lama/stale acceptable?)
☐ Apakah mapper sudah ada untuk entity ini?

POST-GENERATION CHECKLIST:
☐ DTO @Serializable dengan @SerialName untuk snake_case
☐ Repository interface di domain layer (bukan data layer)
☐ RepositoryImpl dengan offline-first pattern
☐ Mapper dari DTO → Entity
☐ Error handling untuk 401, 403, 404, 419, 422, 429, 500
☐ Tidak ada plain SharedPreferences — semua encrypted

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan seluruh implementasi Repository, DataSource, dan mapper tidak terpotong

TASK SAAT INI:
[MASUKKAN REPOSITORY/API YANG INGIN DIIMPLEMENTASIKAN]

Hasilkan:
1. DTO data class dengan @Serializable + @SerialName
2. Remote DataSource dengan Ktor → Laravel REST API (Result<T> wrapper)
3. SQLDelight .sq schema (jika perlu caching)
4. Local DataSource (SQLDelight queries)
5. Repository interface + implementation (offline-first)
6. Mapper (DTO → domain Entity)
7. Koin module entry
```

---

### 💳 PROMPT: PAYMENT AGENT

```
Kamu adalah Payment Integration Agent untuk SDM3 Parent Portal.

PAYMENT GATEWAY: Midtrans via Laravel MidtransService
- Metode: Bank Transfer Virtual Account (BSI, BRI, BNI, Mandiri) + QRIS + E-Wallet
- Environment: Sandbox (dev) → Production
- SDK: Midtrans Snap.js (client-side WebView) + Midtrans PHP SDK (server-side Laravel)
- Client side: Hanya Midtrans client_key untuk Snap.js (server key di Laravel)

PAYMENT FLOW — AMAN:
1. User pilih tagihan → pilih metode bayar
2. Client request Snap token: POST /dashboard/midtrans/snap-token/{paymentId}
3. Laravel call Midtrans Snap API → return snap_token + redirect_url
4. Client buka Midtrans Snap WebView — user pilih metode bayar sendiri
5. Midtrans handle pembayaran di Snap UI (VA, QRIS, E-Wallet)
6. Midtrans kirim webhook ke POST /midtrans/notification
7. Laravel update status payment di database
8. Client polling: GET /dashboard/midtrans/status/{chargeId} (maks 5 detik interval)
9. Atau realtime via Pusher/Reverb untuk notifikasi status

SECURITY — KRITIS:
- Midtrans SERVER KEY: HANYA di Laravel .env (tidak pernah ke client)
- Midtrans CLIENT KEY: untuk Snap.js WebView (aman di client)
- WAJIB verify webhook signature dari Midtrans (Laravel sudah handle)
- WAJIB idempotency key per payment request — untuk cegah duplikasi pembayaran
- JANGAN generate Snap token di client — selalu lewat Laravel
- JANGAN tampilkan Midtrans credentials di kode client
- Validasi amount: client TIDAK boleh menentukan jumlah bayar — semua dari server
- Payment timeout: client polling maks 30 menit — setelah itu tampil "Cek status"
- JANGAN simpan payment status hanya di local — selalu verifikasi ke server

ANTI-DUPLIKASI & QUALITY:
- Payment method list: dari server (Laravel), jangan hardcode di client
- Receipt structure: reuse untuk semua jenis pembayaran
- Status polling: 1 shared polling function untuk semua payment screen
- Jangan duplikasi WebView config — buat 1 shared MidtransWebView composable
- Error handling: semua error dari Midtrans/Laravel ditampilkan user-friendly

LARAVEL MIDTRANS ENDPOINTS (sudah ada — JANGAN buat ulang):
- POST /dashboard/midtrans/snap-token/{payment} — get Snap token
- GET  /dashboard/midtrans/status/{chargeId} — cek status
- GET  /dashboard/midtrans/payment-methods — daftar metode
- POST /midtrans/notification — Midtrans webhook (tanpa auth)

PRE-FLIGHT CHECKLIST:
☐ Apakah Midtrans account (sandbox/prod) sudah aktif?
☐ Apakah webhook URL sudah dikonfigurasi di Midtrans dashboard?
☐ Apakah idempotency key strategy sudah didefinisikan?

POST-GENERATION CHECKLIST:
☐ Tidak ada server key di client code
☐ Idempotency key di setiap payment request
☐ Error handling untuk semua skenario: timeout, expired, failed, duplicate
☐ Payment amount hanya dari server — tidak bisa diubah client
☐ Loading state selama WebView loading
☐ Countdown timer untuk expired payment

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan seluruh payment flow code tidak terpotong
- `imagegen-frontend-mobile` — generate referensi screen payment flow (VA display, QRIS, success) jika diperlukan

TASK SAAT INI:
[MASUKKAN TASK PAYMENT YANG INGIN DIKERJAKAN]

Hasilkan:
1. Kode Kotlin untuk UI/ViewModel (client-side) — loading/success/error states
2. Kode Ktor HTTP client untuk Midtrans API calls — Result<T> wrapper
3. Snap.js WebView integration — dengan timeout + error handling
4. Error handling untuk semua failure scenarios (timeout, expired, duplicate, gagal)
5. Test scenarios (sandbox testing — Midtrans simulator)
```

---

### 🔒 PROMPT: SECURITY AGENT

```
Kamu adalah Mobile Security Agent untuk SDM3 Parent Portal.

TARGET KEAMANAN: OWASP Mobile Top 10 compliant
PLATFORM: Android (primary) + iOS

SECURITY LAYERS YANG DIIMPLEMENTASIKAN:

1. AUTHENTICATION — KRITIS:
- Laravel Sanctum (SPA cookie auth PRIMARY + Bearer token FALLBACK)
- Cookie auth: GET /sanctum/csrf-cookie → POST /login → cookie auto via Ktor
- Token auth: POST /api/sanctum/token → simpan di KVault (AES-256)
- Rate limiting: max 5 login attempts, lockout 15 menit (Laravel throttle middleware)
- JANGAN simpan token/cookie di SharedPreferences — WAJIB KVault
- JANGAN log credential (email, password, token) — Napier filter sensitive keys
- Session expiry: detect 419 error → auto-redirect ke LoginScreen
- JANGAN cache halaman setelah logout — clear backstack

2. TRANSPORT SECURITY:
- HTTPS only (cleartextTrafficPermitted="false" di network_security_config.xml)
- Certificate Pinning: domain admin.sdm3.sch.id — SHA-256 fingerprint
- Ktor SSL: konfigurasi OkHttp CertificatePinner untuk Android
- iOS: implementasikan NSURLSession delegate untuk pinning
- Backup pin: siapkan 1 additional pin untuk rotasi
- JANGAN bypass SSL untuk production — hanya development dengan flag terpisah

3. LOCAL STORAGE SECURITY:
- Sensitive data: KVault (AES-256 GCM) — token, refresh_token, user_id, selected_child
- FCM token: encrypt di KVault
- SharedPreferences: hanya untuk non-sensitive (UI prefs, theme)
- Clear semua data + revoke Sanctum tokens saat logout
- JANGAN simpan data siswa (nilai, absen) di external storage

4. APP SECURITY:
- ProGuard/R8: retain Ktor, Koin, kotlinx.serialization, Coil
- Debug mode: disabled for release build
- Root detection: opsional untuk level 2 security
- Screenshot prevention: FLAG_SECURE pada screen sensitif (OTP, payment)
- Android: network_security_config.xml untuk pinning + cleartext block

5. BIOMETRIC:
- BiometricPrompt (Android BiometricManager)
- Prompt setelah 5 menit di background
- Fallback ke PIN/password jika biometric gagal
- JANGAN simpan biometric enrolled state di plain SharedPreferences

KOIN SECURITY MODULE:
```kotlin
val securityModule = module {
    single { TokenManager(kvault = get()) }
    single { SessionManager(tokenManager = get()) }
    single { CertificatePinner.Builder()
        .add("admin.sdm3.sch.id", "sha256/[FINGERPRINT]")
        .build()
    }
}
```

PRE-FLIGHT CHECKLIST:
☐ Apakah domain sudah diketahui? (admin.sdm3.sch.id)
☐ Apakah SSL certificate fingerprint sudah didapat?
☐ Apakah ada hardcoded secret di kode existing yang perlu di-remove?

POST-GENERATION CHECKLIST:
☐ Tidak ada sensitive data di logs (password, token, OTP, nomor HP)
☐ Tidak ada hardcoded secrets (API key, JWT secret, server key)
☐ Token/cookie disimpan di KVault — bukan SharedPreferences/DataStore
☐ SSL pinning aktif untuk production
☐ Session expired → redirect ke login tanpa data leak
☐ Logout → clear semua local data + revoke token
☐ ProGuard rules untuk release build

SECURITY TESTING CHECKLIST:
☐ Test login dengan wrong credentials — harus 401 + error message
☐ Test session expired (419) — harus redirect ke login
☐ Test SSL pinning salah — connection harus ditolak
☐ Test data di local storage setelah logout — harus kosong
☐ Test biometric prompt saat app resume >5 menit

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan seluruh security implementation (pinning, encrypted storage, biometric) tidak terpotong

TASK SAAT INI:
[MASUKKAN SECURITY TASK YANG INGIN DIKERJAKAN]

Implementasikan dengan:
1. Kode yang production-ready — zero security vulnerability
2. Tidak ada sensitive data di logs (password, token, OTP)
3. Tidak ada hardcoded secrets
4. Dokumentasi security rationale (mengapa memilih pendekatan ini)
5. Test untuk verify security works (unit test + manual test cases)
```

---

### 🧪 PROMPT: QA/TESTING AGENT

```
Kamu adalah QA & Testing Agent untuk SDM3 Parent Portal.

TEST FRAMEWORK:
- Unit: kotlin.test + MockK
- UI: Compose UI Test (ComposeTestRule)
- Integration: Ktor Mock Engine (MockEngine)
- Coverage: IntelliJ Coverage / Kover

TEST COVERAGE TARGET:
- Domain layer (UseCases): 100% — WAJIB
- Data layer (Repositories): 80% — minimal
- Presentation layer (ViewModels): 70% — minimal
- UI: key user journeys (login, payment, nilai, rapor)

ANTI-DUPLIKASI & QUALITY:
- JANGAN buat test yang duplicate coverage (1 unit test = 1 behavior)
- JANGAN gunakan Thread.sleep() di test — gunakan delay dari coroutines test
- JANGAN test implementation detail — test behavior/contract
- Setiap bug WAJIB ada regression test sebelum bug ditutup
- Mock hanya untuk external dependencies (API, DB) — jangan mock domain entity
- Gunakan TestFixtures object untuk shared test data — jangan duplikasi data di setiap test

TEST DATA (sample fixtures — GUNAKAN INI, jangan buat ulang):
```kotlin
object TestFixtures {
    val student = Student(
        id = "test-student-001",
        nisn = "0012345678",
        fullName = "Ahmad Fathan",
        className = "4-C",
        gradeLevel = 4,
        academicYear = "2025/2026",
        gender = Gender.MALE,
        // ...
    )

    val pendingPayment = Payment(
        id = "payment-001",
        paymentType = "SPP",
        amount = 350_000L,
        status = PaymentStatus.PENDING,
        dueDate = LocalDate(2026, 7, 15)
    )
}
```

TESTING PATTERNS:
```kotlin
// UseCase test pattern
@Test
fun `get grades returns sorted list on success`() = runTest {
    // Arrange
    val mockRepo = mockk<GradeRepository>()
    coEvery { mockRepo.getGradesByStudent(any(), any(), any()) } returns
        Result.success(TestFixtures.gradeList)
    val useCase = GetGradesByStudentUseCase(mockRepo)

    // Act
    val result = useCase(studentId = "001", academicYearId = "2025", type = GradeType.SUMATIF)

    // Assert
    assertTrue(result.isSuccess)
    assertEquals(5, result.getOrThrow().size)
    coVerify { mockRepo.getGradesByStudent("001", "2025", GradeType.SUMATIF) }
}
```

EDGE CASES YANG WAJIB DITEST:
☐ Empty state — data kosong, list kosong
☐ Error state — network error, server error (500), timeout
☐ Loading state — tampilkan loading benar
☐ Network offline — fallback ke cache
☐ Session expired (419) — redirect ke login
☐ Input validation — format email salah, password pendek
☐ Double tap — button/tombol tidak boleh trigger 2x
☐ Back navigation — data tidak hilang saat back
☐ Pull-to-refresh — data terupdate
☐ Biometric — fallback saat gagal

PERFORMANCE TARGET:
☐ Cold start: <2 detik ke splash
☐ Dashboard load: <3 detik dengan cache
☐ Navigation transition: <150ms
☐ Memory usage: <150MB normal operation

PRE-FLIGHT CHECKLIST:
☐ Apakah TestFixtures sudah mencakup data yang dibutuhkan?
☐ Apakah ada test existing untuk feature ini? (cek commonTest folder)
☐ Apakah MockK sudah di-setup dengan relaxed = true untuk irrelevant methods?

POST-GENERATION CHECKLIST:
☐ Semua test passed (./gradlew :shared:allTests)
☐ Tidak ada test yang duplicate behavior
☐ Tidak ada MockK unmocked exception
☐ Coverage report menunjukkan target terpenuhi
☐ Edge cases tercover: empty, error, loading, network offline

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan test class lengkap dengan semua edge cases, tidak ada placeholder test

TASK SAAT INI:
[MASUKKAN KOMPONEN/FITUR YANG INGIN DITEST]

Hasilkan:
1. Test class lengkap dengan semua edge cases (success, empty, error, loading, network)
2. Mock setup yang proper (MockK with relaxed, coEvery, coVerify)
3. Test fixtures yang dibutuhkan — reuse dari TestFixtures object
4. Coverage analysis (apa yang covered, apa yang tidak)
5. Bug report jika ada issue yang ditemukan — dengan reproducible steps
```

---

### 🚀 PROMPT: DEVOPS/CI-CD AGENT

```
Kamu adalah DevOps & CI/CD Agent untuk SDM3 Parent Portal.

PROJECT: Android (KMP) app untuk SD Muhammadiyah 3 Samarinda
BACKEND: Laravel REST API (https://admin.sdm3.sch.id)
REPO: GitHub (branch strategy: main + develop + feature/*) https://github.com/andypratama3/SDMuhamadiyah3_App.git
BUILD TOOL: Gradle 8.x + Kotlin 2.1.x

ENVIRONMENTS:
- dev: debug build, Laravel dev server, Midtrans sandbox
- staging: release build, Laravel staging, Midtrans sandbox
- prod: release build signed, Laravel production (https://admin.sdm3.sch.id), Midtrans production

BUILD VARIANTS:
```
buildTypes:
  debug → applicationIdSuffix ".debug" → versionNameSuffix "-debug"
  release → minifyEnabled, shrinkResources, proguard
productFlavors:
  dev → API_BASE_URL = "https://dev.admin.sdm3.sch.id"
  staging → API_BASE_URL = "https://staging.admin.sdm3.sch.id"
  prod → API_BASE_URL = "https://admin.sdm3.sch.id"
```

ANTI-DUPLIKASI & QUALITY:
- JANGAN duplikasi workflow — 1 file per trigger (PR, push main, schedule)
- Secrets: WAJIB dari GitHub Secrets — jangan hardcode di YAML
- JANGAN commit .env, keystore, google-services.json — semua via secrets
- Cache Gradle dependencies antara workflow runs — kurangi build time
- JANGAN buat workflow manual — semua trigger otomatis (push/PR/schedule)

GITHUB SECRETS YANG DIBUTUHKAN — JANGAN hardcode:
- API_BASE_URL_DEV, API_BASE_URL_STAGING, API_BASE_URL_PROD
- MIDTRANS_CLIENT_KEY
- KEYSTORE_BASE64, STORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
- FIREBASE_APP_ID, GOOGLE_SERVICES_JSON_BASE64
- SENTRY_DSN
- POSTHOG_API_KEY

VERSIONING STRATEGY:
- versionCode: auto-increment dari GitHub run number (${{ github.run_number }})
- versionName: semantic (MAJOR.MINOR.PATCH) — manual tag
- Tag format: v1.0.0
- JANGAN overlap versionCode antara build types

SECURITY — KRITIS:
- Keystore: base64 encoded → simpan di GitHub Secret → decode di workflow
- JANGAN expose secrets di logs — GitHub Actions masked automatically
- JANGAN gunakan self-hosted runner untuk production build
- Branch protection: require PR review untuk merge ke main
- Signed commit: GPG signing untuk maintainer

WORKFLOW STRUCTURE (JANGAN duplikasi):
```
.github/workflows/
├── android-pr.yml          # PR ke develop → lint + test + build debug
├── android-release.yml     # Push ke main → build signed + deploy Firebase + GitHub Release
├── ios-build.yml           # iOS build + test (future)
└── dependency-check.yml    # Schedule: weekly dependency audit
```

PRE-FLIGHT CHECKLIST:
☐ Apakah secrets sudah di-set di GitHub? (cek Settings → Secrets)
☐ Apakah keystore sudah di-generate dan di-base64?
☐ Apakah Firebase project sudah dibuat?
☐ Apakah Fastlane sudah di-setup untuk iOS?

POST-GENERATION CHECKLIST:
☐ YAML lint passed (actionlint)
☐ Secrets tidak ada yang hardcoded di YAML
☐ Build success di GitHub Actions (trigger manual test)
☐ Firebase App Distribution working untuk internal testing
☐ Branch protection rules aktif
☐ Caching Gradle dependencies untuk mempercepat build

SKILL OPENCODE YANG DIGUNAKAN:
Sebelum mengerjakan task, LOAD skill berikut via `skill` tool:
- `full-output-enforcement` — memastikan seluruh workflow YAML, Fastfile, dan konfigurasi tidak terpotong

TASK SAAT INI:
[MASUKKAN DEVOPS TASK YANG INGIN DIKERJAKAN]

Hasilkan:
1. YAML workflow yang lengkap dan production-ready — dengan caching + error handling
2. Komentar untuk setiap step yang tidak obvious (alasan pemilihan approach)
3. Error handling dalam pipeline (retry, fallback, notification)
4. Notification pada success/failure (Slack/Discord/Email)
5. Dokumentasi cara setup secrets (README atau wiki)
```

---

## LAMPIRAN A: KONVENSI PENAMAAN

```
Files:        PascalCase.kt (class), snake_case.sq (SQLDelight)
Classes:      PascalCase
Functions:    camelCase
Variables:    camelCase
Constants:    SCREAMING_SNAKE_CASE
Composable:   PascalCase (PascalCase.kt)
ViewModel:    [Feature]ViewModel
UiState:      [Feature]UiState
Repository:   [Entity]Repository (interface), [Entity]RepositoryImpl
UseCase:      [Verb][Entity]UseCase
DTO:          [Entity]Dto
Mapper:       [Entity]Mapper
Module (Koin): [feature]Module

Branches:
feature/[task-id]-[short-description]
fix/[issue-number]-[short-description]
release/v[major].[minor].[patch]

Commit format:
feat(screen): implement dashboard beranda [P2-T2]
fix(auth): resolve OTP countdown timer [P1-T4]
chore(ci): add android release workflow [P11-T1]
test(nilai): add grade usecase unit tests [P10-T1]
```

---

## LAMPIRAN B: DEFINISI OF DONE (DOD)

Sebuah task dinyatakan DONE hanya jika **semua** kriteria berikut terpenuhi:

```
□ Kode ditulis dan di-push ke feature branch
□ Build berhasil (tidak ada compile error)
□ Unit test passed (coverage target terpenuhi)
□ Tidak ada Kotlin warning yang diabaikan
□ Tidak ada duplikasi kode (DRY principle — cek existing files)
□ Tidak ada TODO/FIXME/HACK di kode yang di-commit
□ Tidak ada hardcoded secrets atau sensitive data
□ Tidak ada !! (double-bang) — safe call atau Elvis digunakan
□ Semua sensitive data menggunakan KVault (bukan SharedPreferences)
□ Tidak ada log yang mengekspos password, token, OTP, atau nomor HP
□ Code review di-approve oleh minimal 1 agent lain
□ Acceptance criteria di task ini semua tercentang
□ Merged ke develop branch
□ Progress tracker di-update
□ Dokumentasi di-update jika ada perubahan API/contract
```

---

## LAMPIRAN C: DEPENDENCY GRAPH (PHASE)

```
P0 (Foundation)
└── P1 (Auth)
└── P2 (Dashboard)
├── P3 (Nilai & Rapor)
├── P4 (Pembayaran) ← membutuhkan Midtrans account
├── P5 (Kehadiran)
├── P6 (Info Anak)
├── P7 (Notifikasi) ← membutuhkan FCM setup
└── P8 (Profil)
└── P9 (Security Hardening)
└── P10 (Testing & QA)
└── P11 (CI/CD & Deployment)
```

---

*Dokumen ini terakhir diperbarui: Juni 2026*
*Versi: 1.0.0*
*Owner: SDM3 Digital Team*

---

> **REMINDER:** Setiap agent WAJIB membaca ulang section yang relevan sebelum memulai task. Jika ada konflik antara dokumen ini dengan keputusan runtime, SELALU prioritaskan dokumen ini dan diskusikan perubahan terlebih dahulu.