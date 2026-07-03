# Remaining Work — SDM3 Parent Portal

> Checklist item yang **masih kurang** sebelum rilis Play Store & App Store.  
> Terakhir diperbarui: Juli 2026 · Versi app: **1.0.0**

Lihat juga: [`Production.md`](Production.md) untuk panduan setup lengkap.

---

## Ringkasan Cepat

| Kategori | Status |
|----------|--------|
| Kode fitur (27 layar) | ✅ Selesai |
| Unit tests (47) | ✅ Lulus |
| CI/CD GitHub Actions | ✅ Workflow siap |
| Backend kritis | ⚠️ Perlu diverifikasi / deploy |
| Signing & release | ⚠️ Keystore & secrets belum |
| Firebase Android | ✅ Lokal — `google-services.json` gitignored, tidak di-push |
| Firebase iOS | ✅ Lokal — `GoogleService-Info.plist` gitignored + `pod install` |
| Package ID selaras | ⚠️ Android ≠ iOS |
| SSL pinning | ❌ Kosong |
| Store submission | ❌ Belum |

---

## P0 — Wajib Sebelum Submit Store

### Backend

API production: `https://admin.sdm3.sch.id`

| Item | Kenapa penting |
|------|----------------|
| `DELETE /api/parent/account` harus live | Apple & Google **wajib** hapus akun nyata, bukan cuma logout |
| `POST /api/parent/fcm/register` + `unregister` | Push notification tidak jalan tanpa endpoint ini |
| Snap-token mengembalikan `payment_id` | Alur pembayaran Midtrans bisa gagal tanpa field ini |
| Privacy policy aktif | URL `https://admin.sdm3.sch.id/privacy` — kalau 404, review ditolak |
| Akun demo untuk reviewer | Wajib diisi di Play Console & App Store Connect |

**Kontrak hapus akun:**

```
DELETE /api/parent/account
Authorization: Bearer {token}
Body: { "reason": "string" }

Response:
{ "success": true, "data": null, "message": "Akun berhasil dihapus" }
```

**Kontrak snap-token (field wajib):**

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

### Signing & Release

| Item | Status |
|------|--------|
| Keystore Android (`.jks`) | ❌ Belum dibuat |
| GitHub secrets signing (4 buah) | ❌ Belum di-set |
| Test `bundleRelease` signed | ❌ Belum diverifikasi |

**Secrets yang perlu di GitHub → Settings → Secrets → Actions:**

| Secret | Isi |
|--------|-----|
| `ANDROID_KEYSTORE_BASE64` | `base64 -i release.keystore` |
| `ANDROID_KEYSTORE_PASSWORD` | Password keystore |
| `ANDROID_KEY_ALIAS` | Alias key |
| `ANDROID_KEY_PASSWORD` | Password key |

**Buat keystore:**

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias sdm3-parent \
  -keyalg RSA -keysize 2048 -validity 10000
```

### Package ID Tidak Selaras

| Platform | ID saat ini |
|----------|-------------|
| Android `applicationId` | `com.sdmuhammadiyah3smd` |
| iOS `PRODUCT_BUNDLE_IDENTIFIER` | `sdmuhammadiyah3smd` |
| Firebase Android (`google-services.json`) | `com.sdmuhammadiyah3smd` |

**Tindakan:** Seragamkan sebelum rilis — terutama jika push notification iOS juga diaktifkan.

---

## P1 — Mobile Infrastructure

### Firebase / Push Notification

| Platform | Yang kurang |
|----------|-------------|
| **Android** | Kode sudah ada — perlu **test di device fisik** (login → izin notif → kirim test dari Firebase Console) |
| **iOS** | `GoogleService-Info.plist` lokal — `pod install` + upload APNs key (.p8) ke Firebase Console |
| **Backend** | Service account JSON + kirim push via FCM HTTP v1 |
| **Deep link** | Tap notifikasi hanya buka app — belum navigasi ke layar spesifik |

**Yang sudah ada (Android):**

- `Sdm3FirebaseMessagingService`
- `PushNotificationDisplay` + notification channel
- `FcmTokenStore`, `FcmRegistrationCoordinator`
- Registrasi token saat login, unregister saat logout

**Yang belum:**

- `MainActivity` tidak membaca `notification_id` / `notification_type` dari intent
- Navigasi otomatis ke layar Notifikasi / Detail Pengumuman / Pembayaran

### iOS App Store

| Item | Status |
|------|--------|
| `TEAM_ID` di `iosApp/Configuration/Config.xcconfig` | ❌ Kosong |
| Apple Developer account + provisioning profile | ❌ Belum setup |
| QR scan iOS (`scanQrCode()`) | ❌ Return `null` — verifikasi QR rapor tidak jalan di iOS |
| Archive & upload TestFlight | ❌ Belum dilakukan |

**Langkah iOS Firebase:**

1. Tambah app iOS di Firebase Console (bundle ID yang sudah diseragamkan)
2. Download `GoogleService-Info.plist` → `iosApp/iosApp/`
3. Upload APNs Authentication Key (.p8) di Firebase → Project Settings → Cloud Messaging
4. `cd iosApp && pod install`
5. Build dari Xcode → Archive → Distribute

### Keamanan

| Item | Status | File |
|------|--------|------|
| SSL certificate pinning | ❌ Kosong | `shared/.../CertificateProvider.kt` |
| iOS SSL pinning | ❌ Belum | TrustKit atau `NSURLSessionDelegate` |

```kotlin
// CertificateProvider.kt — isi sebelum rilis production
object CertificatePins {
    val pins = listOf<String>(
        // SPKI hash untuk admin.sdm3.sch.id
    )
}
```

**Cara ambil pin hash:**

```bash
openssl s_client -connect admin.sdm3.sch.id:443 </dev/null 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform der \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64
```

---

## P2 — Store Listing & QA

| Item | Status |
|------|--------|
| Data Safety (Google Play Console) | ❌ Belum diisi |
| App Privacy Labels (App Store Connect) | ❌ Belum diisi |
| Screenshot & deskripsi store | ❌ Belum |
| App icon branding SDM3 | ⚠️ Perlu dicek (bukan template generic) |
| QA end-to-end dengan API production | ❌ Belum dilaporkan |
| `Production.md` package name | ⚠️ Masih menyebut `com.sdm3.parent` — perlu diselaraskan |

### Data Safety (Google) — poin utama

- Data dikumpulkan: nama, email/telepon, data siswa, pembayaran
- Data dienkripsi in transit (HTTPS)
- Pengguna bisa minta hapus akun (in-app)
- Tidak dijual ke pihak ketiga

### App Privacy (Apple) — poin utama

- Contact Info, Identifiers (FCM token), Financial Info (pembayaran SPP)
- Linked to user identity
- Used for app functionality

---

## Sudah Selesai

Tidak perlu dikerjakan ulang:

- [x] 27 layar UI + navigasi
- [x] Build Android debug & release (unsigned)
- [x] 47 unit test — `:shared:testAndroidHostTest`
- [x] R8 / ProGuard release
- [x] `allowBackup=false` + backup exclusion rules
- [x] Session expiry via `SessionEventBus`
- [x] HTTP logging dimatikan di release
- [x] Logout + clear cache + FCM unregister
- [x] FCM Android (service, channel, token fetch & register)
- [x] GitHub Actions CI (`ci.yml`) + Release (`release.yml`)
- [x] `google-services.json` + `GoogleService-Info.plist` (lokal, gitignored)
- [x] `GoogleService-Info.plist` iOS (lokal, gitignored)
- [x] Runtime permission notifikasi Android 13+
- [x] Penghapusan akun in-app (UI + API client — menunggu backend live)
- [x] Platform actions: clipboard, share, open URL, QR Android

---

## Urutan Kerja yang Disarankan

```
Minggu 1 — Backend & Signing
  ├── Deploy DELETE /api/parent/account
  ├── Verifikasi FCM register/unregister + snap-token payment_id
  ├── Aktifkan privacy policy URL
  ├── Buat keystore Android + isi GitHub secrets
  └── Sediakan akun demo reviewer

Minggu 2 — Mobile & Firebase
  ├── Selaraskan package ID Android ↔ iOS ↔ Firebase
  ├── Test push Android di device fisik
  ├── iOS: GoogleService-Info.plist + TEAM_ID + pod install
  ├── Implementasi deep link notifikasi
  └── Isi SSL certificate pins (opsional tapi disarankan)

Minggu 3 — Store Submission
  ├── Tag v1.0.0 → GitHub Release → download AAB
  ├── Upload AAB ke Play Console (internal testing)
  ├── Archive iOS → TestFlight
  ├── Isi Data Safety + Privacy Labels
  └── QA dengan akun demo

Minggu 4 — Production
  ├── Promote internal → closed → open testing (Play)
  ├── Submit for review (Play + App Store)
  └── Monitor crash reports & push delivery
```

---

## Risiko Penolakan Review

| Risiko | Platform | Mitigasi |
|--------|----------|----------|
| Hapus akun hanya logout | Apple + Google | Backend `DELETE /api/parent/account` wajib live |
| Push diiklankan tapi tidak jalan | Keduanya | Test FCM end-to-end atau sembunyikan toggle |
| Privacy policy 404 | Keduanya | Pastikan URL aktif sebelum submit |
| Package ID tidak konsisten | Firebase + Store | Seragamkan Android & iOS |
| QR scan iOS tidak jalan | Apple | Implementasi AVFoundation atau sembunyikan tombol |
| Data Safety tidak akurat | Google | Isi sesuai data yang benar-benar dikumpulkan |
| Tidak ada akun demo | Keduanya | Sediakan kredensial di form review |

---

## File Terkait

| File | Fungsi |
|------|--------|
| [`Production.md`](Production.md) | Panduan setup production lengkap |
| [`Prompt/Blueprints/BackendAPIRequirements.md`](Prompt/Blueprints/BackendAPIRequirements.md) | Spesifikasi API backend |
| [`.github/workflows/README.md`](.github/workflows/README.md) | Setup CI/CD & secrets |
| `androidApp/build.gradle.kts` | Signing, version, applicationId |
| `iosApp/Configuration/Config.xcconfig` | Bundle ID, TEAM_ID |
| `shared/.../CertificateProvider.kt` | SSL pins |
| `androidApp/google-services.json.example` | Template Firebase Android (commit) |
| `iosApp/iosApp/GoogleService-Info.plist.example` | Template Firebase iOS (commit) |
| `scripts/setup-firebase-config.sh` | Salin config asli dari Downloads |

---

*Dokumen ini melengkapi [`Production.md`](Production.md) dengan fokus pada gap yang masih terbuka per Juli 2026.*
