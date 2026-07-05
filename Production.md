# Production Checklist — SDM3 Parent Portal

> Panduan lengkap untuk merilis aplikasi ke **Google Play Store** dan **Apple App Store** tanpa revisi.
> Terakhir diperbarui: Juli 2026 · Versi app: **1.0.0** · Package: `com.sdm3.parent`

---

## Ringkasan Status

| Area | Status | Catatan |
|------|--------|---------|
| Kode fitur (27 layar) | ✅ Siap | Build Android + iOS berhasil |
| Unit tests (47) | ✅ Lulus | `:shared:testAndroidHostTest` |
| Keamanan HTTP (release) | ✅ Diperbaiki | Logging dimatikan di release |
| Backup Android | ✅ Diperbaiki | `allowBackup=false` + exclusion rules |
| R8 / ProGuard | ✅ Dikonfigurasi | Release minify enabled |
| Runtime permission notifikasi | ✅ Diperbaiki | Android 13+ diminta di `MainActivity` |
| Logout + hapus cache | ✅ Diperbaiki | SQLDelight + token dibersihkan |
| Penghapusan akun (in-app) | ⚠️ Butuh backend | API `DELETE /api/parent/account` wajib live |
| Firebase / Push | ✅ Terintegrasi | Ganti config placeholder → file asli dari Firebase Console |
| Android release signing | ⚠️ Template siap | Keystore harus dibuat & diisi |
| iOS bundle ID | ✅ Diperbaiki | `com.sdm3.parent` (isi `TEAM_ID` di Xcode) |
| iOS deployment target | ⚠️ Perlu cek | Saat ini mungkin masih tinggi di `pbxproj` |
| SSL Pinning | ❌ Kosong | Isi hash sertifikat production |
| Privacy policy URL | ⚠️ Verifikasi | `https://sdmuhammadiyah3smd.cloud/privacy` harus aktif |
| Data Safety / Nutrition Labels | ❌ Belum diisi | Isi di Play Console & App Store Connect |

---

## 1. Backend (Wajib Sebelum Rilis)

API production: `https://sdmuhammadiyah3smd.cloud`

### 1.1 Endpoint Kritis

| Endpoint | Method | Status | Digunakan oleh |
|----------|--------|--------|----------------|
| `/api/sanctum/token` | POST | Wajib live | Login |
| `/api/user` | GET | Wajib live | Splash, validasi sesi |
| `/api/parent/students` | GET | Wajib live | Pilih Anak, Home |
| `/api/parent/dashboard` | GET | Wajib live | Home |
| `/api/parent/midtrans/snap-token/{feeId}` | POST | Wajib live | Pembayaran — body: `{ payment_method }` |
| `/api/parent/midtrans/status/{orderId}` | GET | Wajib live | Konfirmasi pembayaran |
| `/api/parent/account` | DELETE | **WAJIB BARU** | Hapus akun — body: `{ reason }` |
| `/api/parent/fcm/register` | POST | Wajib jika push aktif | Login |
| `/api/parent/fcm/unregister` | POST | Wajib jika push aktif | Logout |

### 1.2 Kontrak Penghapusan Akun (Apple 5.1.1v & Google)

Backend **harus** mengimplementasikan:

```
DELETE /api/parent/account
Authorization: Bearer {token}
Body: { "reason": "string" }

Response (envelope):
{ "success": true, "data": null, "message": "Akun berhasil dihapus" }
```

**Aturan:**
- Hapus permanen data orang tua + cabut semua token Sanctum
- Cabut FCM token di server
- Retensi data transaksi pembayaran boleh dipertahankan sesuai hukum (jelaskan di privacy policy)
- App hanya menampilkan sukses jika server mengembalikan `success: true`

### 1.3 Kontrak Snap Token Pembayaran

Response **harus** menyertakan `payment_id` agar alur Midtrans benar:

```json
{
  "success": true,
  "data": {
    "snap_token": "...",
    "redirect_url": "https://...",
    "payment_id": "uuid-payment-record",
    "order_id": "ORDER-123"
  }
}
```

---

## 2. Android — Google Play Store

### 2.1 Persyaratan Google Play (2025–2026)

| Persyaratan | Nilai project | Status |
|-------------|---------------|--------|
| Target API level | 36 (Android 16) | ✅ Memenuhi (min API 35 untuk submit baru) |
| Min SDK | 24 (Android 7.0) | ✅ |
| 64-bit | ARM64 dari KMP | ✅ |
| Privacy policy URL | Di app (Profil) | ⚠️ Verifikasi URL live |
| Data Safety form | Belum diisi | ❌ Wajib di Play Console |
| Account deletion | In-app UI ada | ⚠️ Backend harus live |
| Families / Children | App sekolah dasar | ⚠️ Isi deklarasi target usia |

### 2.2 Release Signing (WAJIB)

**Langkah 1 — Buat keystore** (sekali saja, simpan aman):

```bash
keytool -genkey -v \
  -keystore sdm3-parent-release.jks \
  -alias sdm3-parent \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storetype JKS
```

**Langkah 2 — Tambahkan ke `local.properties`** (jangan commit ke git):

```properties
RELEASE_STORE_FILE=/path/to/sdm3-parent-release.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=sdm3-parent
RELEASE_KEY_PASSWORD=your_key_password
```

**Langkah 3 — Build release AAB:**

```bash
./gradlew :androidApp:bundleRelease
```

Output: `androidApp/build/outputs/bundle/release/androidApp-release.aab`

> `androidApp/build.gradle.kts` sudah dikonfigurasi untuk membaca properti di atas dan mengaktifkan R8.

### 2.3 Play Console — Checklist

- [ ] Buat aplikasi di [Google Play Console](https://play.google.com/console)
- [ ] Upload AAB release (bukan APK)
- [ ] **Store listing**: judul, deskripsi singkat/panjang, screenshot (min 2), feature graphic 1024×500
- [ ] **App icon**: 512×512 PNG (ganti icon Android default jika masih template)
- [ ] **Privacy policy URL**: `https://sdmuhammadiyah3smd.cloud/privacy`
- [ ] **Data Safety** — isi sesuai tabel di Bagian 5
- [ ] **App content** → Target audience → Usia (kemungkinan "Not designed for children" karena app orang tua, bukan anak)
- [ ] **App content** → Data deletion → Link ke halaman penghapusan akun
- [ ] **App access** → Jika login wajib, sediakan akun demo untuk reviewer Google
- [ ] **Content rating** → Isi kuesioner IARC
- [ ] **News app declaration** → Tidak berlaku (bukan app berita)
- [ ] Aktifkan **Play App Signing** (disarankan — Google pegang upload key)

### 2.4 Akun Demo untuk Reviewer

Siapkan di backend:

```
Email   : reviewer@sdm3.sch.id
Password: [password kuat, tidak expire]
```

Cantumkan di Play Console → App access → Instructions.

### 2.5 Firebase Cloud Messaging (Android) — ✅ Kode sudah terintegrasi

**Yang sudah ada di project:**
- `Sdm3FirebaseMessagingService` — terima push + refresh token
- `FcmTokenProvider` — ambil token dari Firebase
- `PushNotificationDisplay` — tampilkan notifikasi (foreground & background)
- Registrasi token ke backend saat login + saat token refresh
- Unregister saat logout

**Yang perlu Anda lakukan (sekali):**

1. Buat project di [Firebase Console](https://console.firebase.google.com)
2. Tambahkan app Android `com.sdm3.parent`
3. **Ganti** `androidApp/google-services.json` dengan file asli dari Firebase (placeholder saat ini hanya untuk compile)
4. Di Firebase Console → Cloud Messaging → pastikan API enabled
5. Upload **Server Key / FCM credentials** ke backend Laravel agar bisa kirim push

```bash
# Verifikasi build setelah ganti google-services.json
./gradlew :androidApp:assembleDebug
```

---

## 3. iOS — Apple App Store

### 3.1 Persyaratan Apple (2025–2026)

| Persyaratan | Nilai project | Status |
|-------------|---------------|--------|
| Bundle ID | `com.sdm3.parent` | ✅ Diperbaiki di `Config.xcconfig` |
| Team ID | Kosong | ❌ Isi `TEAM_ID` di `iosApp/Configuration/Config.xcconfig` |
| Account deletion in-app | UI ada | ⚠️ Backend wajib live |
| Privacy Nutrition Labels | Belum | ❌ Isi di App Store Connect |
| Privacy Manifest (`PrivacyInfo.xcprivacy`) | Belum | ❌ Wajib jika pakai SDK tertentu |
| Camera usage description | Ada | ✅ |
| Face ID usage description | Ada | ✅ |
| QR Scanner iOS | Belum implementasi | ⚠️ Sembunyikan atau implementasi AVFoundation |

### 3.2 Setup Xcode

**Langkah 1 — Apple Developer Account**
- Daftar di [developer.apple.com](https://developer.apple.com) ($99/tahun)
- Buat App ID: `com.sdm3.parent`
- Enable capabilities: Push Notifications, Associated Domains (jika perlu)

**Langkah 2 — Config.xcconfig**

```xcconfig
TEAM_ID=XXXXXXXXXX          # 10 karakter Team ID Anda
PRODUCT_BUNDLE_IDENTIFIER=com.sdm3.parent
MARKETING_VERSION=1.0.0
CURRENT_PROJECT_VERSION=1
```

**Langkah 3 — Deployment Target**
- Buka `iosApp.xcodeproj` → Target → General
- Set **Minimum Deployments** ke **iOS 15.0** atau **16.0** (jangan 18.5)
- Build & Archive via Xcode → Distribute App → App Store Connect

### 3.3 App Store Connect — Checklist

- [ ] Buat app baru di [App Store Connect](https://appstoreconnect.apple.com)
- [ ] Bundle ID: `com.sdm3.parent`
- [ ] **App Privacy** → Nutrition Labels (lihat Bagian 5)
- [ ] **Privacy Policy URL**: `https://sdmuhammadiyah3smd.cloud/privacy`
- [ ] **User Privacy Choices URL** (opsional tapi disarankan): halaman penghapusan akun
- [ ] Screenshot iPhone 6.7" dan 6.1" (min 3 per ukuran)
- [ ] App Preview video (opsional)
- [ ] **App Review Information**: akun demo + catatan untuk reviewer
- [ ] **Export Compliance**: app menggunakan HTTPS standar → biasanya "No" untuk encryption khusus
- [ ] **Age Rating**: isi kuesioner (kemungkinan 4+)

### 3.4 Push Notifications (iOS) — ✅ Kode sudah terintegrasi

**Yang sudah ada di project:**
- `AppDelegate` di `iOSApp.swift` — Firebase configure + FCM delegate
- `FcmBridge` — kirim token ke Kotlin
- `UIBackgroundModes: remote-notification` di Info.plist
- `Podfile` dengan FirebaseCore + FirebaseMessaging

**Yang perlu Anda lakukan (sekali):**

```bash
cd iosApp
# Salin GoogleService-Info.plist dari Firebase Console ke iosApp/iosApp/
cp /path/to/GoogleService-Info.plist iosApp/iosApp/
pod install
open iosApp.xcworkspace   # buka workspace, BUKAN .xcodeproj
```

1. Di Firebase Console → tambahkan app iOS `com.sdm3.parent`
2. Download `GoogleService-Info.plist` → `iosApp/iosApp/`
3. Upload **APNs Auth Key (.p8)** ke Firebase Console → Project Settings → Cloud Messaging
4. Di Xcode: Signing & Capabilities → **Push Notifications**
5. Build & run dari `iosApp.xcworkspace`

### 3.5 Privacy Manifest

Buat `iosApp/iosApp/PrivacyInfo.xcprivacy` jika menggunakan SDK yang mensyaratkan (Firebase, dll). Template minimal:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>NSPrivacyTracking</key>
    <false/>
    <key>NSPrivacyTrackingDomains</key>
    <array/>
    <key>NSPrivacyCollectedDataTypes</key>
    <array/>
    <key>NSPrivacyAccessedAPITypes</key>
    <array/>
</dict>
</plist>
```

---

## 4. Keamanan Aplikasi

### 4.1 Sudah Diterapkan di Kode

| Item | Implementasi |
|------|-------------|
| HTTPS only | `usesCleartextTraffic=false` |
| Token storage terenkripsi | KVault (`SecureTokenManager`) |
| HTTP logging release | `LogLevel.NONE` saat `!isDebugBuild()` |
| Backup disabled | `allowBackup=false` + XML exclusion |
| Session expiry | `SessionEventBus` + validasi token di Splash |
| Logout bersih | Hapus token + SQLDelight cache + FCM unregister |
| POST notifikasi runtime | `MainActivity.requestNotificationPermissionIfNeeded()` |
| R8 obfuscation | `isMinifyEnabled=true` di release |
| Camera optional | `android:required="false"` — tidak blokir device tanpa kamera |

### 4.2 Masih Perlu Dikerjakan

| Item | Prioritas | Cara |
|------|-----------|------|
| SSL Certificate Pinning | Tinggi | Isi `CertificatePins.pins` dengan SPKI hash `sdmuhammadiyah3smd.cloud` |
| iOS SSL Pinning | Tinggi | Implementasi TrustKit atau URLSession delegate |
| Biometric opt-in | Sedang | Jangan auto-enable; minta persetujuan user |
| Midtrans client key | N/A | Tidak ada di app (server-side only) ✅ |

**Cara dapat SPKI pin hash:**

```bash
openssl s_client -connect sdmuhammadiyah3smd.cloud:443 </dev/null 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform der \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64
```

Masukkan hasil ke `CertificateProvider.kt`:

```kotlin
object CertificatePins {
    val pins = listOf("sha256/AAAA...=")
}
```

---

## 5. Data Safety & Privacy Labels

Isi form di Play Console dan App Store Connect berdasarkan data yang **benar-benar** dikumpulkan:

### Data yang Dikumpulkan App

| Tipe Data | Contoh | Tujuan | Dibagikan ke pihak ketiga? |
|-----------|--------|--------|---------------------------|
| Nama, email, telepon | Profil orang tua | Akun & autentikasi | Tidak |
| Data anak (nama, NISN, kelas, foto) | Dashboard, nilai | Layanan sekolah | Tidak |
| Data akademik | Nilai, kehadiran, rapor | Layanan sekolah | Tidak |
| Data pembayaran | Tagihan SPP, bukti bayar | Pembayaran via Midtrans | Ya → Midtrans |
| Device ID / FCM token | Push notification | Notifikasi | Ya → Firebase/Google |
| Foto kamera | Scan QR rapor | Verifikasi dokumen | Tidak (hanya diproses lokal) |
| Data autentikasi biometrik | Face ID / sidik jari | Login cepat | Tidak (hanya di device) |

### Privacy Policy — Harus Mencakup

- [ ] Identitas pengontrol data (SD Muhammadiyah 3 Samarinda)
- [ ] Data apa yang dikumpulkan dan mengapa
- [ ] Penyimpanan data anak (orang tua sebagai pengguna, bukan anak langsung)
- [ ] Integrasi Midtrans untuk pembayaran
- [ ] Firebase untuk push notification
- [ ] Hak pengguna: akses, koreksi, penghapusan akun
- [ ] Kontak DPO / admin: email sekolah
- [ ] Retensi data setelah penghapusan akun

URL yang sudah dipakai di app: `https://sdmuhammadiyah3smd.cloud/privacy`
URL bantuan: `https://sdmuhammadiyah3smd.cloud/help`

---

## 6. Branding & Metadata Store

### 6.1 Informasi Aplikasi

| Field | Nilai disarankan |
|-------|-----------------|
| Nama app | SDM3 Parent / Portal Orang Tua SDM3 |
| Package / Bundle ID | `com.sdm3.parent` |
| Versi | 1.0.0 (versionCode 1) |
| Kategori Play Store | Pendidikan |
| Kategori App Store | Education |
| Bahasa | Indonesia (utama) |

### 6.2 Aset Visual yang Dibutuhkan

| Aset | Ukuran | Status |
|------|--------|--------|
| App icon Android | 512×512 PNG | ⚠️ Ganti jika masih icon default |
| App icon iOS | 1024×1024 PNG | ✅ Ada di `Assets.xcassets` |
| Screenshot phone | 1080×1920 min | ❌ Buat dari device/emulator |
| Feature graphic (Play) | 1024×500 | ❌ Buat |
| Promotional text | 170 karakter | ❌ Tulis |

### 6.3 Deskripsi Singkat (contoh)

> Portal resmi orang tua SD Muhammadiyah 3 Samarinda. Pantau nilai, kehadiran, pembayaran SPP, rapor digital, dan pengumuman sekolah dalam satu aplikasi.

---

## 7. Testing Sebelum Submit

### 7.1 Checklist QA Manual

- [ ] Login email/password → sukses
- [ ] Login biometrik (setelah login pertama) → sukses
- [ ] Splash dengan token valid → masuk Main
- [ ] Splash dengan token expired → redirect Login
- [ ] Pilih anak → dashboard load
- [ ] Logout → kembali Login, data cache hilang
- [ ] Hapus akun → **hanya sukses jika backend live**
- [ ] Bayar SPP end-to-end (staging Midtrans)
- [ ] Notifikasi push (setelah Firebase)
- [ ] Scan QR rapor (Android)
- [ ] Buka privacy policy & help dari Profil
- [ ] Mode offline → error message yang benar, bukan crash

### 7.2 Perintah Build

```bash
# Debug (development)
./gradlew :androidApp:assembleDebug

# Release AAB (Play Store)
./gradlew :androidApp:bundleRelease

# Unit tests
./gradlew :shared:testAndroidHostTest

# iOS (dari Xcode)
# Product → Archive → Distribute
```

---

## 8. Urutan Rilis yang Disarankan

```
Minggu 1 — Backend
  ├── Deploy DELETE /api/parent/account
  ├── Verifikasi snap-token mengembalikan payment_id
  ├── Aktifkan privacy policy & help page
  └── Siapkan akun demo reviewer

Minggu 2 — Mobile Infrastructure
  ├── Setup Firebase (Android + iOS)
  ├── Buat keystore Android
  ├── Setup Apple Developer + provisioning
  └── Isi SSL certificate pins

Minggu 3 — Store Submission
  ├── Upload AAB ke Play Console (internal testing dulu)
  ├── Upload IPA ke TestFlight
  ├── Isi Data Safety + Privacy Labels
  └── QA dengan akun demo

Minggu 4 — Production
  ├── Promote internal → closed → open testing
  ├── Submit for review (Play + App Store)
  └── Monitor crash reports
```

---

## 9. Risiko Penolakan Review & Mitigasi

| Risiko | Platform | Mitigasi |
|--------|----------|----------|
| Hapus akun hanya logout | Apple + Google | Backend `DELETE /api/parent/account` wajib live |
| Push diiklankan tapi tidak jalan | Keduanya | Integrasi Firebase atau sembunyikan toggle push |
| Privacy policy 404 | Keduanya | Pastikan URL aktif sebelum submit |
| `com.example` bundle ID | Apple | ✅ Sudah diganti ke `com.sdm3.parent` |
| QR scan iOS tidak jalan | Apple | Implementasi AVFoundation atau sembunyikan tombol scan |
| Data Safety tidak akurat | Google | Isi sesuai tabel Bagian 5 |
| Tidak ada akun demo | Keduanya | Sediakan kredensial di form review |
| App icon generic/template | Google | Ganti dengan branding SDM3 |

---

## 10. File Konfigurasi Penting

| File | Fungsi |
|------|--------|
| `androidApp/build.gradle.kts` | Signing, R8, version, BuildConfig |
| `androidApp/proguard-rules.pro` | Aturan obfuscation release |
| `androidApp/src/main/AndroidManifest.xml` | Permissions, backup, security |
| `androidApp/src/main/res/xml/backup_rules.xml` | Exclusion backup |
| `iosApp/Configuration/Config.xcconfig` | Bundle ID, Team ID, version |
| `iosApp/iosApp/Info.plist` | Permission descriptions |
| `shared/.../Platform.kt` | `APP_VERSION_NAME`, `isDebugBuild()` |
| `shared/.../CertificateProvider.kt` | SSL pins (isi sebelum rilis) |
| `local.properties` | Keystore credentials (JANGAN commit) |
| `Prompt/Blueprints/BackendAPIRequirements.md` | Spesifikasi API lengkap |

---

## 11. Environment & Secrets

**Jangan pernah commit ke git:**

- `local.properties` (sudah di `.gitignore`)
- `*.jks` / `*.keystore`
- `google-services.json`
- `GoogleService-Info.plist`
- Password Midtrans / Sanctum keys

**Simpan di:**
- Password manager tim (1Password, Bitwarden)
- CI/CD secrets (GitHub Actions Secrets, Firebase App Distribution)

---

## Kontak & Eskalasi

| Peran | Tanggung jawab |
|-------|----------------|
| Backend developer | API, account deletion, Midtrans webhook |
| Mobile developer | Firebase, signing, store submission |
| Admin sekolah | Privacy policy, akun demo, konten store listing |
| Apple/Google review | 1–7 hari kerja setelah submit |

---

*Dokumen ini dibuat berdasarkan audit keamanan kode, persyaratan Google Play (API 35+, Data Safety, account deletion) dan Apple App Store (Guideline 5.1.1v, Privacy Nutrition Labels) per Juli 2026.*
