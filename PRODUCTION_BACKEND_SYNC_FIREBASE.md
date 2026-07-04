# SDM3 Parent Portal — Sinkronisasi Backend & Implementasi Firebase (Produksi)

Dokumen ini merangkum hasil audit sinkronisasi antara aplikasi **KMP (SDMuhammadiyah3Samarinda)** dan **backend Laravel (`~/Development/ProductSchool/src`)**, perbaikan yang sudah diterapkan, kebutuhan **Firebase Cloud Messaging (FCM)**, serta checklist rilis produksi (Google Play & App Store).

- Backend: Laravel 12 (`laravel/framework ^12.56`), repo `github.com/andypratama3/ProductsSchool`, branch `main`.
- Kontrak API mobile: prefix `/api/parent/*`, auth **Sanctum Bearer token**, guard `role:parent|admin|superadmin`.
- Tanggal audit: 2026-07-04.

---

## 1. Ringkasan Eksekutif

Mayoritas endpoint & DTO **sudah sinkron**. Ditemukan **6 masalah** yang membuat beberapa fitur gagal di produksi, plus **1 kesenjangan besar**: backend menyimpan `fcm_token` tetapi **tidak pernah mengirim push** (tidak ada implementasi Firebase sama sekali).

| # | Masalah | Dampak | Status |
|---|---------|--------|--------|
| 1 | Endpoint daftar rapor app `rapor-instances` ≠ backend `rapors` | Daftar rapor selalu 404 | ✅ Diperbaiki (app) |
| 2 | Verifikasi QR: app kirim `qr_data`, backend validasi `qrData` | Verifikasi rapor selalu 422 | ✅ Diperbaiki (backend) |
| 3 | Snap token: route `GET` (app `POST`), butuh `Payment` id (app kirim `StudentFee` id), respons buang `redirect_url`/`order_id` | Bayar SPP gagal total (405/404/alur buntu) | ✅ Diperbaiki (backend+app) |
| 4 | Cek status bayar mengembalikan objek Midtrans mentah, bukan `PaymentDto` | Polling status gagal di-decode | ✅ Diperbaiki (backend) |
| 5 | Endpoint hapus akun `DELETE /api/parent/account` tidak ada | Melanggar syarat wajib store | ✅ Diperbaiki (backend) |
| 6 | OTP lupa-password dibocorkan di respons API | Risiko keamanan produksi | ✅ Diperbaiki (backend, gated `APP_DEBUG`) |
| 7 | **Firebase push tidak diimplementasi** di backend | Notifikasi push tidak pernah terkirim | ✅ Scaffolding aman ditambahkan — perlu 2 langkah aktivasi (lihat §5) |

Semua perbaikan sudah lolos `php -l` (backend) dan `:shared:compileCommonMainKotlinMetadata` **BUILD SUCCESSFUL** (app).

---

## 2. Matriks Sinkronisasi Endpoint (App ↔ Backend)

Semua di bawah prefix `/api/parent` (kecuali auth pra-login). ✅ = sinkron, 🔧 = diperbaiki di audit ini.

| Fitur | Endpoint App | Method | Route Backend | Sinkron |
|-------|--------------|--------|---------------|:------:|
| Login (token) | `/api/sanctum/token` | POST | `sanctum/token` | ✅ |
| Ambil user | `/api/user` | GET | `user` | ✅ |
| Logout | `/api/parent/logout` | POST | `parent/logout` | ✅ |
| Profil / Me | `/api/parent/profile`, `/api/parent/me` | GET | `parent/profile`, `parent/me` | ✅ |
| Update profil | `/api/parent/profile` | PATCH | `parent/profile` (+`phone`) | 🔧 |
| **Hapus akun** | `/api/parent/account` | DELETE | `parent/account` | 🔧 (route baru) |
| Lupa password | `/api/forgot-password` | POST | `forgot-password` | 🔧 (OTP di-gate) |
| Verifikasi OTP | `/api/verify-otp` | POST | `verify-otp` | ✅ |
| Reset password | `/api/reset-password` | POST | `reset-password` | ✅ |
| Dashboard | `/api/parent/dashboard` | GET | `parent/dashboard` | ✅ |
| Daftar siswa | `/api/parent/students` | GET | `parent/students` | ✅ |
| Detail siswa | `/api/parent/students/{id}` | GET | `parent/students/{id}` | ✅ |
| Nilai (sumatif) | `/api/parent/grades` | GET | `parent/grades` | ✅ |
| Komponen nilai (TP) | `/api/parent/grade-components` | GET | `parent/grade-components` | ✅ |
| Transkrip | `/api/parent/grades/transcript/{id}` | GET | `parent/grades/transcript/{studentId}` | ✅ |
| Kehadiran | `/api/parent/attendances` | GET | `parent/attendances` | ✅ |
| Ringkasan hadir | `/api/parent/attendance-summary` | GET | `parent/attendance-summary` (legacy) | ✅ |
| Ekskul | `/api/parent/extracurriculars` | GET | `parent/extracurriculars` | ✅ |
| Program unggulan | `/api/parent/academic-programs` | GET | `parent/academic-programs` | ⚠️ data dummy (lihat §6) |
| **Daftar rapor** | `/api/parent/rapors` | GET | `parent/rapors` | 🔧 (dulu `rapor-instances`) |
| Unduh rapor | `/api/parent/rapor/{id}/download` | GET | `parent/rapor/{id}/download` | ✅ |
| **Verifikasi QR rapor** | `/api/parent/rapor/verify` | POST | `parent/rapor/verify` | 🔧 (terima `qr_data`) |
| Tagihan siswa (SPP) | `/api/parent/student-fees` | GET | `parent/student-fees` | ✅ |
| Daftar pembayaran | `/api/parent/payments` | GET | `parent/payments` | ✅ |
| Detail pembayaran | `/api/parent/payments/{id}` | GET | `parent/payments/{id}` | ✅ |
| **Snap token** | `/api/parent/midtrans/snap-token/{payment}` | POST | `midtrans/snap-token/{payment}` | 🔧 (GET+POST, fee→payment) |
| **Status bayar** | `/api/parent/midtrans/status/{chargeId}` | GET | `midtrans/status/{chargeId}` | 🔧 (bentuk `PaymentDto`) |
| Metode bayar | `/api/parent/midtrans/payment-methods` | GET | `parent/midtrans/payment-methods` | ✅ |
| Notifikasi | `/api/parent/notifications` | GET | `parent/notifications` | ✅ |
| Tandai terbaca | `/api/parent/notifications/{id}/read` | POST | `parent/notifications/{id}/read` | ✅ |
| Tandai semua terbaca | `/api/parent/notifications/read-all` | POST | `parent/notifications/read-all` | ✅ |
| Jumlah belum dibaca | `/api/parent/notifications/unread-count` | GET | `parent/notifications/unread-count` | ✅ |
| Artikel/pengumuman | `/api/parent/articles` (+`/{id}`,`/slug/{slug}`) | GET | `parent/articles` | ✅ |
| Daftar FCM token | `/api/parent/fcm/register` | POST | `parent/fcm/register` | ✅ |
| Hapus FCM token | `/api/parent/fcm/unregister` | POST | `parent/fcm/unregister` | ✅ |

> Format respons: semua endpoint memakai envelope `ApiResponse` → `{ "success": bool, "message": string, "data": ... }`. Client sudah menangani envelope maupun payload langsung.
>
> Catatan tipe: konfigurasi JSON client `isLenient = true` + `coerceInputValues = true`, sehingga `id` integer/UUID dari backend tetap ter-decode ke `String` di DTO. Aman.

---

## 3. Detail Perbaikan yang Diterapkan

### 3.1 Aplikasi (KMP — repo ini)

| File | Perubahan |
|------|-----------|
| `shared/.../data/remote/api/Endpoints.kt` | `PARENT_RAPOR_INSTANCES`: `/api/parent/rapor-instances` → **`/api/parent/rapors`** |
| `shared/.../feature/pembayaran/ProsesPembayaranViewModel.kt` | `mapStatus()` mengenali status backend **`completed`** (sukses) & `refunded` (gagal) |
| `shared/.../feature/pembayaran/ui/PembayaranSppScreen.kt` | Daftar riwayat: tambah `completed`/`refunded` ke status paid/failed |
| `shared/.../feature/pembayaran/ui/PembayaranBerhasilScreen.kt` | Label status audit mengenali `completed`/`refunded` |
| `shared/.../feature/pembayaran/ui/DetailBuktiBayarScreen.kt` | Badge status mengenali `completed`/`refunded` |

### 3.2 Backend (Laravel — `~/Development/ProductSchool/src`)

| File | Perubahan |
|------|-----------|
| `routes/api.php` | `snap-token` menerima **GET & POST**; route baru **`DELETE parent/account`** |
| `app/Http/Controllers/Api/Parent/RaporController.php` | `verify()` menerima `qr_data` \| `qrData` \| `code` (snake_case dari app) |
| `app/Http/Controllers/Api/Parent/PaymentController.php` | `snapToken()` menerima **Payment id ATAU StudentFee id** (auto find-or-create Payment), mengembalikan `snap_token` + `redirect_url` + `order_id` + `payment_id`; `paymentStatus()` mengembalikan bentuk **`PaymentResource`/`PaymentDto`** + sinkron status live Midtrans; helper `resolvePaymentForFee()` & `mapMidtransStatus()` |
| `app/Http/Controllers/Api/Parent/ProfileController.php` | `update()` mendukung `phone` (disimpan ke `student.parent_phone`); method baru **`destroy()`** (hapus akun: cabut token, hapus fcm_token, soft-delete user) |
| `app/Http/Controllers/Api/Parent/AuthController.php` | `forgotPassword()`: OTP hanya dikembalikan bila `APP_DEBUG=true`; respons seragam (anti user-enumeration) |
| `config/services.php` | Blok konfigurasi `firebase` (`credentials`, `project_id`) |
| `app/Services/FcmService.php` | **BARU** — pengirim FCM (guarded, inert bila paket/kredensial belum ada) |
| `app/Jobs/SendPushNotification.php` | **BARU** — job antrean kirim push per-user |
| `app/Observers/PushNotificationObserver.php` | **BARU** — kirim push otomatis saat `Notification` in-app dibuat |
| `app/Providers/AppServiceProvider.php` | Registrasi `Notification::observe(PushNotificationObserver::class)` |

> **Semua kode Firebase bersifat *guarded*** (mengecek `class_exists(\Kreait\Firebase\Factory::class)` + kredensial). Sebelum aktivasi (§5) kode ini **tidak berpengaruh** pada backend yang berjalan — meniru pola `MidtransService` yang sudah ada.

---

## 4. Cara Menguji Perbaikan (Sandbox)

```bash
# 1. Login → dapatkan token
curl -X POST https://API_HOST/api/sanctum/token \
  -H "Accept: application/json" \
  -d "email=orangtua@example.com&password=secret&device_name=mobile"

TOKEN=... # dari respons

# 2. Daftar rapor (dulu 404)
curl https://API_HOST/api/parent/rapors -H "Authorization: Bearer $TOKEN" -H "Accept: application/json"

# 3. Verifikasi QR (dulu 422)
curl -X POST https://API_HOST/api/parent/rapor/verify \
  -H "Authorization: Bearer $TOKEN" -H "Accept: application/json" \
  -d "qr_data=KODE-VERIFIKASI"

# 4. Bayar SPP: kirim StudentFee id → dapat snap_token + redirect_url + payment_id
curl -X POST https://API_HOST/api/parent/midtrans/snap-token/STUDENT_FEE_ID \
  -H "Authorization: Bearer $TOKEN" -H "Accept: application/json" \
  -d "payment_method=va_bca"

# 5. Polling status (kembali sebagai PaymentDto)
curl https://API_HOST/api/parent/midtrans/status/ORDER_OR_PAYMENT_ID \
  -H "Authorization: Bearer $TOKEN" -H "Accept: application/json"

# 6. Hapus akun (baru)
curl -X DELETE https://API_HOST/api/parent/account \
  -H "Authorization: Bearer $TOKEN" -H "Accept: application/json" \
  -d "reason=Tidak dipakai lagi"
```

> Setelah mengubah `routes/api.php`, jalankan `php artisan route:clear && php artisan config:clear` (atau `route:cache`/`config:cache` di produksi).

---

## 5. Firebase Cloud Messaging — Yang Perlu Diimplementasi

**Kondisi saat ini:** aplikasi (Android/iOS) sudah punya integrasi FCM di sisi klien (mendaftarkan token via `/api/parent/fcm/register`, menampilkan push). Backend **menyimpan** `fcm_token` per-user, tetapi **tidak punya pengirim** — jadi push tidak pernah dikirim.

Audit ini menambahkan **scaffolding pengiriman** yang aman. Untuk mengaktifkannya di produksi, lakukan **2 langkah** berikut.

### Langkah A — Pasang Firebase Admin SDK (PHP)

```bash
cd ~/Development/ProductSchool/src
composer require kreait/firebase-php:^7.0
```

### Langkah B — Pasang kredensial & env

1. Firebase Console → **Project Settings → Service accounts → Generate new private key** → simpan JSON, mis. `storage/app/firebase/service-account.json` (jangan commit; sudah di-cover `.gitignore` di `storage/`).
2. Tambahkan ke `.env` produksi:

```env
FIREBASE_CREDENTIALS=/var/www/app/storage/app/firebase/service-account.json
FIREBASE_PROJECT_ID=sdm3-parent-portal   # sesuaikan dengan project Firebase
```

3. Bersihkan cache config:

```bash
php artisan config:clear   # (produksi: config:cache)
```

Setelah kedua langkah ini, **setiap `Notification` in-app yang dibuat** untuk user dengan `fcm_token` akan otomatis mengirim push (via `PushNotificationObserver` → `SendPushNotification` → `FcmService`).

### Kontrak payload push (yang dibaca aplikasi)

Aplikasi (Android `PushNotificationDisplay`) membaca:

- Judul: `notification.title` **atau** `data.title`
- Isi: `notification.body` **atau** `data.message` **atau** `data.body`
- Deep-link: `data.type` (mis. `grade`, `payment`, `attendance`, `announcement`) & `data.notification_id`

`FcmService` mengirim **notification block** (title/body) **dan** **data block** (`type`, `notification_id`, `message`, + data asli notifikasi). Ini kompatibel dengan foreground & background di Android/iOS.

### Kirim push manual (contoh)

```php
// Di controller/service mana pun:
\App\Jobs\SendPushNotification::dispatch(
    userId: $user->id,
    title: 'Nilai Baru',
    body: 'Nilai Matematika ananda sudah tersedia',
    data: ['type' => 'grade', 'grade_id' => $grade->id],
);
```

### Pengaturan queue (disarankan)

`SendPushNotification` adalah `ShouldQueue`. Dengan `QUEUE_CONNECTION=sync` (default) ia berjalan inline (tetap aman). Untuk produksi, jalankan worker agar push tidak memblokir request:

```env
QUEUE_CONNECTION=redis   # atau database
```
```bash
php artisan queue:work --queue=default
```

### Sisi Aplikasi (sudah ada / pastikan)

- **Android:** `google-services.json` ada di `androidApp/` (Firebase project yang sama dgn service account). `Sdm3FirebaseMessagingService` & channel notifikasi sudah terdaftar.
- **iOS:** `GoogleService-Info.plist` sudah ada di `iosApp/`. Pastikan **APNs Auth Key (.p8)** diunggah ke Firebase Console (Project Settings → Cloud Messaging → Apple app configuration), aktifkan **Push Notifications** & **Background Modes → Remote notifications** di Xcode, serta entitlement `aps-environment=production`.

---

## 6. Keterbatasan & Rekomendasi Lanjutan (belum diubah — perlu keputusan)

1. **Program unggulan (`academic-programs`) masih data dummy** di `StudentController::academicPrograms()` (Tahfiz/Sholat/Bahasa Arab hardcoded). Ganti dengan data nyata dari DB bila fitur ini akan dirilis, atau sembunyikan di app.
2. **Tagihan tidak otomatis "lunas" setelah pembayaran.** `MidtransService::processCallback()` mengubah `Payment.status → completed` tetapi **tidak** memperbarui `StudentFee.status → lunas` (tidak ada FK langsung fee↔payment). Akibatnya kartu SPP di app bisa tetap tampil "belum lunas". **Rekomendasi:** di `processCallback` saat sukses, tandai `StudentFee` yang cocok (student_id + payment_title_id) menjadi `lunas`. (Tidak diubah otomatis karena berisiko salah-cocok tanpa FK — perlu verifikasi bisnis.)
3. **Update `phone`** hanya tersimpan bila user punya relasi `student` (via `users.id`). Untuk orang tua yang dipetakan lewat `parent_email` (bukan `user_id`), nomor tak tersimpan. Pertimbangkan menyimpan telepon orang tua di tabel `users` atau memetakan relasi student ke user secara konsisten.
4. **OTP lupa-password belum dikirim ke email/SMS.** Saat ini hanya di-generate & (mode debug) dikembalikan di respons. **Wajib** implementasikan pengiriman (Mail/Notification/SMS) sebelum fitur reset password dipakai publik.
5. **Alur Midtrans perlu uji end-to-end di sandbox.** Backend kini mengembalikan `redirect_url` (halaman Snap). App membuka `redirect_url` untuk metode non-VA; untuk VA, VA number berasal dari webhook/`payment.va_number`. Verifikasi dengan Midtrans Sandbox + webhook aktif (`/api/...midtrans callback`), pastikan `midtrans_server_key`/`client_key` terpasang.
6. **`Notification.data`** di app di-decode sebagai `Map<String,String>`. Pastikan payload `data` notifikasi berisi nilai skalar (string), bukan objek/array bertingkat, agar tidak gagal decode.

---

## 7. Checklist Rilis Produksi

### Backend
- [ ] `composer require kreait/firebase-php:^7.0`
- [ ] `FIREBASE_CREDENTIALS` & `FIREBASE_PROJECT_ID` diset; file service-account tidak ter-commit
- [ ] `APP_DEBUG=false` (memastikan OTP tidak bocor) & `APP_ENV=production`
- [ ] `midtrans.is_production` sesuai; server/client key produksi terisi
- [ ] Webhook Midtrans terpasang & signature diverifikasi (sudah ada di `MidtransService`)
- [ ] `php artisan config:cache route:cache` setelah deploy
- [ ] Queue worker berjalan (`queue:work`) bila `QUEUE_CONNECTION` bukan `sync`
- [ ] (Rekomendasi) Implementasi pengiriman OTP & penandaan `StudentFee` lunas (§6)

### Aplikasi
- [ ] `BASE_URL` mengarah ke host produksi (HTTPS)
- [ ] `google-services.json` (Android) & `GoogleService-Info.plist` (iOS) = project Firebase yang sama dgn service account backend
- [ ] APNs key (.p8) terunggah di Firebase; entitlement push produksi
- [ ] Uji: login, dashboard, nilai, rapor (list+verify+download), **bayar SPP end-to-end**, notifikasi push masuk, **hapus akun**

### Store compliance
- [ ] URL kebijakan privasi mencantumkan cara **hapus akun** (kini didukung in-app: Profil → Hapus Akun → `DELETE /api/parent/account`)
- [ ] Deklarasi data (Data Safety / App Privacy) sesuai data yang dikirim (nama, email, telepon, token perangkat)

---

_Disusun otomatis dari audit lintas-repo app↔backend. Perbaikan backend berada di repo `ProductsSchool` (belum di-commit); perbaikan app di repo ini (belum di-commit). Lakukan review & commit sesuai alur Anda._
