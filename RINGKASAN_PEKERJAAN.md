# Ringkasan Pekerjaan & Perbaikan — SDM3 Parent Portal

Dokumen ini merangkum **semua pekerjaan dan perbaikan** yang dilakukan untuk menyiapkan
aplikasi **Portal Wali Murid SD Muhammadiyah 3 Samarinda** (Kotlin Multiplatform –
Android & iOS) menuju rilis produksi, beserta perubahan backend Laravel yang menyertainya.

- **Aplikasi:** Kotlin Multiplatform (Compose Multiplatform) — Android + iOS
- **Backend:** Laravel (`~/Development/ProductSchool/src`)
- **Domain produksi:** `https://sdmuhammadiyah3smd.cloud`
- **Ringkasan perubahan app:** 116 berkas (+1.544 / −3.637 baris) + berkas baru
- **Terakhir diperbarui:** 4 Juli 2026

---

## Ringkasan Eksekutif

| Area | Status |
|------|--------|
| Konfigurasi produksi & domain | ✅ Selesai |
| Push notifikasi (FCM) Android + iOS | ✅ Selesai |
| Alur awal (splash → onboarding → login → pilih anak) | ✅ Diperbaiki |
| Input keyboard & font korup | ✅ Diperbaiki |
| Multi-anak (ganti siswa aktif di Home & Profil) | ✅ Ditambahkan |
| Rapor tidak ter-fetch | ✅ Diperbaiki (butuh deploy backend) |
| Kwitansi digital & format (tanggal/metode/kelas) | ✅ Dirapikan |
| Notifikasi | ✅ Diverifikasi & dirapikan |
| Pembersihan kode mati (`com.eduocto`) | ✅ Dihapus |

---

## 1. Konfigurasi Produksi & Domain

**Dikerjakan:**
- Semua base URL aplikasi diarahkan ke domain produksi `https://sdmuhammadiyah3smd.cloud`
  (`Platform.android.kt`, `Platform.ios.kt`) untuk semua build.
- URL "Pusat Bantuan" & "Kebijakan Privasi" di layar Profil diperbarui ke domain produksi.
- Host SSL pinning Android dikoreksi ke `sdmuhammadiyah3smd.cloud`.
- `network_security_config.xml` (Android) ditambahkan.
- `isDebugBuild()` diterapkan agar logging/URL sadar-build.

**Diperbaiki:**
- Bug **"failed to connect 127.0.0.1:8000"** di Android — `127.0.0.1` pada perangkat/emulator
  menunjuk ke perangkat itu sendiri, bukan host. Diselesaikan dengan menetapkan domain
  produksi HTTPS untuk semua build.

---

## 2. Push Notifikasi (Firebase Cloud Messaging)

**Dikerjakan (aplikasi):**
- `Sdm3FirebaseMessagingService`, `FcmTokenProvider`, dan `PushNotificationDisplay` (Android).
- Registrasi/penghapusan token FCM saat login/logout.
- Logging token FCM khusus build debug (`App.kt`) untuk memudahkan uji dari Firebase Console.

**Dikerjakan (backend Laravel — sudah di-commit sebelumnya):**
- `FcmService` (Firebase HTTP v1), `SendPushNotification` (job antrean),
  `PushNotificationObserver` (memicu push saat notifikasi in-app dibuat).
- Konfigurasi kredensial Firebase sadar-lingkungan (`config/services.php`, `.env`).

**Catatan operasional:**
- Butuh **queue worker** aktif di server agar push terkirim.
- iOS memerlukan **APNs Auth Key** diunggah ke Firebase Console.

---

## 3. Alur Awal Aplikasi (Splash → Onboarding → Login → Pilih Anak)

**Diperbaiki:**
- Bug "setelah splash tidak muncul onboarding & login seperti bertumpuk".
- Deteksi **fresh install** (`InstallState`) — di iOS, Keychain bertahan setelah uninstall
  sehingga token/onboarding lama "nyangkut". Kini data direset pada instalasi baru
  (`SecureTokenManager.resetForFreshInstall()`).
- Peringatan kompilasi `Condition is always 'true'` di `SplashScreen.kt` dibersihkan.

---

## 4. Input Keyboard & Font

**Diperbaiki:**
- Tidak bisa mengetik di form (Android & iOS Simulator).
- `Sdm3TextField`: `Modifier.height(56.dp)` → `heightIn(min = 56.dp)` agar layout tidak
  "menjepit" area input.
- Berkas font Inter yang korup (black/bold/medium/regular/semibold) diganti sehat, agar teks
  tidak tak terlihat.

---

## 5. Multi-Anak — Ganti Siswa Aktif (1 orang tua bisa banyak anak)

**Dikerjakan:**
- **Profil → Identitas Akademik:** kini menampilkan **anak yang benar-benar aktif dipilih**
  (bukan sekadar anak pertama). Bila anak lebih dari satu, kartu bisa ditekan dan muncul
  affordance **"N anak terdaftar · Ganti Siswa"** yang membuka `PilihAnakBottomSheet`.
- **Beranda (Home):** `HomeViewModel` memuat daftar anak; bila > 1 anak muncul **bar penukar**
  (avatar, nama, kelas, tombol "Ganti") yang membuka bottom sheet yang sama.
- **Mekanisme ganti:** menyimpan `selectedStudentId` lalu navigasi ke `Main(idBaru)` sehingga
  **seluruh tab** (Beranda, Nilai, Bayar, Rapor, Profil) ikut memakai anak baru. Karena
  `studentId` menjadi argumen route, state anak lama tidak "nyangkut".

**Diverifikasi:**
- Kwitansi menampilkan siswa **sesuai transaksi** (benar), bukan anak aktif global.

---

## 6. Rapor — Perbaikan Utama "Rapor Tidak Ter-fetch"

**Akar masalah (backend):**
- `RaporController::index` memfilter `->where('status', 'completed')`, padahal alur status
  `RaporInstance` adalah **`draft → submitted → approved`** (tidak pernah ada `completed`).
  Akibatnya daftar rapor **selalu kosong**.

**Diperbaiki (backend `RaporController.php`):**
- Filter diganti menjadi `->where('status', RaporInstance::STATUS_APPROVED)` — hanya rapor
  final/disetujui yang tampil untuk orang tua.

**Diperbaiki (aplikasi):**
- **Unduh PDF:** backend `download` mengembalikan objek `{id, url, student_name, semester}`,
  tetapi `getDownloadUrl` mem-parse `String` (selalu gagal). Ditambahkan `RaporDownloadDto`
  dan `url` diekstrak dengan benar.
- **Tombol Verifikasi:** dulu mem-prefill `raporId`, padahal backend mencocokkan
  `verification_code` (selalu "tidak valid"). Kini prefill memakai `verificationCode`
  (fallback ke id).
- **Format tampilan rapor:** tanggal "Dipublikasi pada" tidak lagi ISO mentah; label status
  memakai istilah ramah ("TERBIT"/"SIAP UNDUH"/"DIPROSES").

> ⚠️ **Tindakan wajib:** perbaikan utama ada di **backend** — perlu di-deploy ke server agar
> rapor mulai tampil. Pastikan minimal ada 1 rapor berstatus `approved` untuk siswa terkait.

---

## 7. Kwitansi Digital & Standarisasi Format

**Dikerjakan — util terpusat `core/util/Formatters.kt`:**
- `formatRupiah` — "Rp350.000"
- `nameInitials` — inisial avatar aman
- `formatTanggal` — "13 Jun 2026"
- `formatTanggalWaktu` — "13 Jun 2026, 18:52"
- `formatClassName` — cegah dobel "Kelas"
- `formatPaymentMethod` — "bank_transfer" → "Transfer Bank", "gopay" → "GoPay", dll.

**Diperbaiki (layar Kwitansi Digital & Pembayaran Berhasil):**
- **Waktu Bayar** tidak lagi mentah (`2026-06-13 18:52:23` → `13 Jun 2026, 18:52`).
- **Metode** tidak lagi kode mentah (`bank_transfer` → `Transfer Bank`).
- **Nomor Referensi** memakai `orderId` (huruf besar) & rata kanan yang rapi.
- **Kelas** tidak lagi dobel ("Kelas Kelas 1 Baghdad" → "Kelas 1 Baghdad").
- Teks "Bagikan Bukti" ikut memakai format ramah.

**Bug "Kelas Kelas" diperbaiki menyeluruh** di Profil, Home (sapaan & bar penukar),
`PilihAnakBottomSheet`, dan Kwitansi.

---

## 8. Notifikasi

**Diverifikasi & dirapikan:**
- Pengelompokan tanggal relatif ("Baru saja / X mnt lalu / Kemarin / RIWAYAT") sudah benar.
- Tandai-dibaca hanya diterapkan lokal setelah API sukses; hitungan belum-dibaca akurat.
- Filter kategori & empty/error state berfungsi baik.

---

## 9. Pembersihan Kode & Konsistensi

**Dikerjakan:**
- Menghapus **paket design system mati `com.eduocto`** (40+ berkas — sumber utama −3.637 baris).
- Konsolidasi util format & warna nav diambil dari `MaterialTheme`.
- Merapikan layanan/komponen: badge "SEGERA" untuk fitur belum tersedia, instruksi
  pembayaran adaptif, penyempurnaan pemilih semester pada layar Nilai.

---

## 10. Ringkasan Perubahan Backend (Laravel)

| Berkas | Perubahan |
|--------|-----------|
| `app/Http/Controllers/Api/Parent/RaporController.php` | Filter status `completed` → `approved` (fix rapor kosong) |
| `app/Services/FcmService.php` *(sebelumnya)* | Kirim FCM HTTP v1 |
| `app/Jobs/SendPushNotification.php` *(sebelumnya)* | Job antrean push |
| `app/Observers/PushNotificationObserver.php` *(sebelumnya)* | Trigger push saat notifikasi dibuat |
| `config/services.php`, `.env(.example)` *(sebelumnya)* | Konfigurasi kredensial Firebase |
| `app/Http/Controllers/Api/Parent/{Auth,Profile,Payment}Controller.php` *(sebelumnya)* | Hapus akun mandiri, hardening OTP, snap-token/status pembayaran |

---

## Verifikasi

- **Kompilasi shared (commonMain):** `BUILD SUCCESSFUL`.
- **Kompilasi shared (androidMain):** `BUILD SUCCESSFUL` (warning `FcmTokenProvider` bersifat
  bawaan SDK, tidak terkait perubahan).
- **PHP lint backend:** `No syntax errors detected`.
- Semua perubahan logika UI berada di `commonMain` sehingga otomatis berlaku untuk Android & iOS.

---

## Yang Masih Perlu Tindakan Manual

1. **Deploy backend** (`RaporController.php`) ke server produksi agar rapor tampil.
2. **Queue worker** aktif di server untuk pengiriman push notifikasi.
3. **APNs Auth Key** diunggah ke Firebase Console untuk push iOS.
4. **iOS:** isi `TEAM_ID` di `Config.xcconfig` dan build rilis via Xcode.
5. Pastikan data uji: minimal 1 rapor `approved` dan orang tua dengan >1 anak untuk menguji
   fitur multi-anak.
