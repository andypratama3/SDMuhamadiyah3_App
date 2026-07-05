# Pending Tasks — SDM3 Parent Portal

> Daftar kerja yang masih belum selesai setelah cross-check terakhir.
> Fokus: rilis iOS/Android, Firebase push, store submission, dan verifikasi backend produksi.

## Ringkasan

| Area | Status | Catatan |
|------|--------|---------|
| Fitur inti app | ✅ Selesai |
| iOS icon & push permission | ✅ Sudah dirapikan |
| Firebase client Android/iOS | ✅ Kode ada |
| Backend endpoint utama | ⚠️ Perlu verifikasi production |
| iOS release readiness | ⚠️ Masih ada gap |
| Store submission | ❌ Belum |

## P0 — Wajib Sebelum Rilis

### Backend production

- [ ] Verifikasi endpoint `DELETE /api/parent/account` benar-benar live di production.
- [ ] Verifikasi endpoint `POST /api/parent/fcm/register` dan `POST /api/parent/fcm/unregister` live dan menerima token dari app.
- [ ] Verifikasi `POST /api/parent/midtrans/snap-token/{feeId}` mengembalikan `payment_id`.
- [ ] Pastikan `https://sdmuhammadiyah3smd.cloud/privacy` aktif dan tidak 404.
- [ ] Siapkan akun demo reviewer untuk Play Console dan App Store Connect.

### Firebase

- [ ] Upload service account Firebase ke backend Laravel production.
- [ ] Pastikan `FIREBASE_PROJECT_ID` dan `FIREBASE_CREDENTIALS` sudah benar di `.env` production.
- [ ] Upload APNs Auth Key `.p8` ke Firebase Console untuk push iOS.
- [ ] Ganti `GoogleService-Info.plist` placeholder dengan file asli dari Firebase Console jika project production berbeda.
- [ ] Test push end-to-end di device fisik Android dan iPhone.

### iOS release readiness

- [ ] Isi `TEAM_ID` di `iosApp/Configuration/Config.xcconfig`.
- [ ] Login Apple Developer account dan siapkan provisioning profile.
- [ ] Archive iOS dari `iosApp.xcworkspace` dan cek build signed.
- [ ] Buat `iosApp/iosApp/PrivacyInfo.xcprivacy` jika diperlukan oleh SDK yang dipakai.

## P1 — Penting

### Navigasi notifikasi

- [ ] Implementasi deep-link saat push notification ditekan agar tidak hanya membuka app.
- [ ] Pastikan payload push membawa `notification_id`, `type`, dan data target layar.
- [ ] Tambahkan routing untuk notifikasi pembayaran, pengumuman, dan detail notifikasi.

### QR / Kamera iOS

- [ ] Implementasi `scanQrCode()` di iOS.
- [ ] Jika belum dikerjakan, sembunyikan fitur QR scan di iOS agar tidak memunculkan tombol kosong.

### Keamanan

- [ ] Isi SSL certificate pinning untuk host production.
- [ ] Evaluasi pinning iOS dengan TrustKit atau `URLSessionDelegate`.

## P2 — Store & QA

- [ ] Isi Data Safety di Google Play Console.
- [ ] Isi App Privacy Labels di App Store Connect.
- [ ] Siapkan screenshot Android dan iPhone untuk store listing.
- [ ] Siapkan deskripsi pendek, deskripsi panjang, dan feature graphic Play Store.
- [ ] Jalankan QA end-to-end dengan akun demo.
- [ ] Pastikan push notification, logout, hapus akun, dan pembayaran berjalan di environment production.

## Yang Sudah Selesai

- [x] FCM client Android dan iOS sudah ada di app.
- [x] AppIcon iOS sudah dirapikan ke katalog yang valid.
- [x] Permission notifikasi iOS sudah diminta sebelum register FCM.
- [x] Bundle ID iOS sudah diselaraskan ke `com.sdm3.parent`.
- [x] Logout, register/unregister FCM, dan update endpoint backend untuk data kosong sudah diperbaiki.
- [x] Parser `toApiResult` menerima `data: null` / `{}` untuk `Unit` (logout, FCM, hapus akun, mark-read).
- [x] Backend: dashboard `active_fees` filter, StudentFee → `lunas` setelah bayar, OTP email (`OtpService`), metode bayar Midtrans, notifikasi `read_at`, kwitansi PDF, preflight command.
- [x] Backend: profil phone dari `parent_email`, rapor show/download hanya `approved`.
- [x] App: notifikasi `is_read` + `read_at`, artikel `photo` fallback, kehadiran `present`, password min 8.

## Urutan Kerja yang Disarankan

1. Verifikasi backend production dan Firebase Console.
2. Isi `TEAM_ID` dan selesaikan signing iOS.
3. Test push notification end-to-end di device fisik.
4. Selesaikan deep-link notifikasi dan QR scan iOS.
5. Isi Data Safety, App Privacy, dan aset store.
6. Build release final dan submit ke store.
