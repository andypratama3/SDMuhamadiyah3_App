# Analisis & Perbaikan — SDM3 Parent Portal Blueprint
**Per: 19 Juni 2026 — Reviewer: Claude (Anthropic)**

Saya sudah baca seluruh dokumen blueprint kamu. Strukturnya (pembagian agent, phase roadmap, DoD, progress tracker) sudah **sangat bagus dan profesional** — ini level dokumentasi yang jarang ada di proyek sekolah. Tapi ada beberapa **kesalahan teknis yang akan bikin build gagal atau aplikasi ditolak Play Store/App Store** kalau tidak diperbaiki dulu. Saya bagi jadi 3 bagian:

1. Masalah arsitektur/teknis yang harus diperbaiki sebelum coding
2. Versi dependency — sudah saya cek ulang ke sumber resmi per Juni 2026
3. Syarat WAJIB Play Store & App Store khusus app sekolah (data anak + pembayaran)

---

## BAGIAN 1 — MASALAH TEKNIS KRITIS

### 1.1 ⚠️ `androidx.navigation3` versi `1.0.0-alpha01` — TIDAK BISA dipakai begitu saja di KMP

Ini temuan paling penting. Di Section 5.1 & 6.4 kamu menulis akan pakai **Navigation 3** dengan versi `1.0.0-alpha01`.

**Fakta terbaru (saya cek ulang):**
- Navigation 3 awalnya memang **Android-only**. Dukungan multiplatform (iOS, dll) baru masuk ke Compose Multiplatform mulai **versi 1.10.0 (awal 2026)**, lewat artifact terpisah `org.jetbrains.androidx.navigation3:navigation3-ui` — dan **statusnya masih Alpha**, bukan stable.
- Untuk produksi (apalagi app sekolah yang harus reliable), JetBrains sendiri merekomendasikan fork **stable** yang sudah dipakai banyak app production:
  ```
  org.jetbrains.androidx.navigation:navigation-compose:2.9.2
  ```
  Ini API-nya mirip `androidx.navigation` Android biasa, sudah type-safe via `@Serializable`, dan sudah teruji di Android + iOS + Desktop.

**Rekomendasi:** Pakai `navigation-compose:2.9.2` (stable) untuk MVP & rilis pertama ke Play Store/App Store. Evaluasi migrasi ke Navigation 3 multiplatform di versi 2.0 aplikasi nanti setelah dia keluar dari Alpha. Saya sudah buatkan contoh kodenya (lihat `kode-contoh/01-Navigation.kt`).

### 1.2 ⚠️ Firebase SDK (`firebase = "33.6.0"`) — Android only, tidak otomatis jalan di iOS

Di `libs.versions.toml` kamu cuma nulis 1 versi Firebase tanpa bedakan platform. **Firebase Android SDK resmi dari Google itu Android-only.** Untuk KMP yang juga target iOS, kamu butuh salah satu dari:
- **`dev.gitlive:firebase-*`** (GitLiveApp/firebase-kotlin-sdk) — wrapper KMP resmi-komunitas yang paling banyak dipakai, support Firestore/Auth/Messaging di Android+iOS+Desktop.
- Atau bikin `expect/actual` sendiri: `androidMain` pakai Firebase Android SDK asli, `iosMain` pakai Firebase iOS SDK via CocoaPods/SPM + binding Swift, lalu di-bridge ke Kotlin.

Karena prioritas kamu **Android dulu (primary), iOS belakangan (secondary)** — ini bisa ditunda, tapi **WAJIB didokumentasikan** supaya Feature Agent/Data Agent nggak kaget pas tiba waktunya build iOS.

### 1.3 Skema database — ada foreign key ke tabel yang TIDAK didefinisikan

Di Section 6.1, beberapa `REFERENCES` menunjuk ke tabel yang tidak ada definisinya di dokumen:
- `students.shadow_teacher_id REFERENCES employees(id)` → tabel `employees` tidak didefinisikan
- `teachers.employee_id REFERENCES employees(id)` → sama
- `student_report_cards.report_card_template_id REFERENCES report_templates(id)` → tabel `report_templates` tidak didefinisikan

Ini hampir pasti karena schema diambil dari sistem backend sekolah yang sudah ada (yang punya tabel `employees` & `report_templates` di migration lain), jadi kemungkinan **bukan bug**, tapi **WAJIB dicatat di dokumen** supaya Data Agent tahu bahwa tabel itu sudah ada di Laravel existing, bukan harus dibuat baru.

### 1.4 Index MySQL yang aneh di `student_classrooms`

```sql
UNIQUE KEY unique_enrollment (student_id, classroom_id, academic_year_id(8))
```

Notasi `(8)` adalah **prefix index** MySQL — biasanya dipakai untuk kolom `VARCHAR`/`TEXT` panjang agar index lebih ringan. Tapi `academic_year_id` di sini bertipe `CHAR(36)` (UUID), dan UUID **tidak boleh** diambil 8 karakter pertama saja untuk unique constraint karena 8 karakter pertama UUID v4 bisa duplikat antar baris → **constraint ini tidak benar-benar unik**. Hilangkan saja `(8)`-nya:
```sql
UNIQUE KEY unique_enrollment (student_id, classroom_id, academic_year_id)
```

### 1.5 Inkonsistensi status task: OTP Screen

- Section 4.1 (Screen Inventory) → `SCR-006 Verifikasi OTP` status `⬜ TODO`
- Section 20 (Progress Tracker) → `P1-T4` status `⬜ TODO`
- Tapi di body Phase 1 (`P1-T4`) kamu sendiri menulis status `⏭️ SKIPPED` dan menjelaskan fitur ini ditunda ke Phase 9.

**Perbaikan:** Update Section 4.1 dan Section 20 supaya konsisten — set status jadi `⏭️ SKIPPED (→ Phase 9)` di kedua tempat itu juga, supaya tidak ada agent yang salah kerja duluan.

### 1.6 `Posthog` & `Sentry` di KMP juga bukan "pasang-jalan" otomatis di iOS

Sama seperti Firebase — paket Android-nya (`posthog-android`, `sentry-android`) **tidak otomatis tersedia untuk iOS**. Sentry punya `sentry-kotlin-multiplatform` resmi yang lebih siap; PostHog KMP support relatif baru dan lebih terbatas. Tandai ini di task **P0-T3 (Core Network & DI)** agar Architect Agent menyiapkan `expect/actual` wrapper-nya dari awal, bukan ditambal belakangan.

---

## BAGIAN 2 — VERSI DEPENDENCY (dicek ulang ke sumber resmi, Juni 2026)

Versi di blueprint kamu (Kotlin 2.1.0, Compose 1.7.0, Koin 3.6.0, Ktor 3.0.0, dst.) itu adalah versi-versi yang **stable di akhir 2024**, jadi sudah lumayan tertinggal. Tabel di bawah saya ambil dari dokumentasi resmi Kotlin/JetBrains per Juni 2026:

| Dependency | Versi di blueprint kamu | Versi terbaru (Juni 2026) | Catatan |
|---|---|---|---|
| Kotlin | 2.1.0 | **2.4.0** | Stable. Compose compiler ikut versi Kotlin sejak Kotlin 2.0 |
| Compose Multiplatform (CMP) | 1.7.0 | **1.11.x** | Compose for iOS sudah Stable sejak CMP 1.8.0 (Mei 2025) |
| Android Gradle Plugin | — | **9.0.1** | |
| Koin | 3.6.0 | **4.2.1** | Koin 4 = stable annotations + KMP support penuh |
| Ktor | 3.0.0 | **3.5.0** | |
| SQLDelight | 2.0.2 | **2.3.2** | |
| kotlinx-coroutines | — | **1.11.0** | |
| kotlinx-datetime | 0.6.0 | **0.8.0** | |
| kotlinx-serialization | 1.7.0 | **1.8.x** | |
| Navigation (rekomendasi) | `androidx.navigation3:1.0.0-alpha01` | **`org.jetbrains.androidx.navigation:navigation-compose:2.9.2`** (stable) | lihat Bagian 1.1 |
| KVault | 1.3.0 | cek versi terbaru di repo `liftric/kvault` sebelum mulai | masih library yang tepat untuk encrypted storage cross-platform |

> ⚠️ **Catatan kejujuran:** Pengetahuan dasar saya "terkunci" di akhir Januari 2026, jadi angka-angka di atas saya ambil dari pencarian web hari ini (19 Juni 2026) ke dokumentasi resmi — tapi tetap **jalankan `./gradlew dependencyUpdates` atau cek Maven Central/Android Studio "Update Versions" sebelum kickoff Phase 0**, karena minor version bisa naik lagi dalam beberapa minggu.

---

## BAGIAN 3 — SYARAT WAJIB AGAR LOLOS PLAY STORE & APP STORE (Khusus App Sekolah)

Karena app ini menyimpan **data anak (nilai, kehadiran, NISN, foto) dan data finansial (SPP)**, ada lapisan kepatuhan ekstra di luar fitur. Saya cek kebijakan terbaru — ini yang **wajib** masuk roadmap, idealnya jadi task baru di **Phase 9 (Security Hardening)**:

### 3.1 🔴 Hapus Akun (Account Deletion) — WAJIB, baru tidak ada di blueprint kamu

Google Play **mewajibkan**: kalau app punya akun, harus ada (1) jalur hapus akun **di dalam app**, DAN (2) link web eksternal untuk request hapus akun **tanpa perlu install app**. Menghapus = harus benar-benar hapus data, bukan sekadar nonaktifkan/freeze. Apple punya syarat serupa (App Review Guideline 5.1.1(v)).

**Yang harus ditambahkan:**
- Screen baru di Profil: **"Hapus Akun"** (lihat contoh kode `kode-contoh/04-AccountDeletionFeature.kt`)
- Endpoint baru di Laravel: `POST /api/parent/account/deletion-request`
- Halaman web publik (boleh halaman statis sekolah): `https://sdm3.sch.id/hapus-akun` — wajib didaftarkan di Play Console → App Content → Data deletion
- **Nuansa penting untuk app sekolah:** karena akun orang tua biasanya **dibuat oleh admin sekolah** (bukan self-signup), kamu tetap wajib menyediakan jalur request hapus — tapi proses approval-nya boleh manual lewat admin (bukan instant delete), karena data siswa terhubung ke catatan akademik resmi sekolah yang punya kewajiban retensi tersendiri. **Jelaskan masa retensi ini di Privacy Policy.**

### 3.2 🔴 Privacy Policy + Data Safety Form (Play) & Privacy Nutrition Label (Apple)

- Privacy Policy **wajib** publik (URL aktif), mencakup: data apa yang dikumpulkan (nama, NISN, nilai, kehadiran, foto, data pembayaran), untuk apa, disimpan di mana (sebut eksplisit: Laravel server + Cloudflare R2 + Midtrans), berapa lama, siapa yang bisa akses, dan hak orang tua (lihat/koreksi/hapus).
- Isi **Data Safety form** di Play Console dengan jujur: app ini **collect** data pribadi & finansial, **share** sebagian ke Midtrans (untuk proses bayar) dan ke Firebase/Sentry/Posthog (untuk notifikasi & analitik) — semua ini harus dicentang, jangan disembunyikan.
- Di Apple: isi **App Privacy ("Nutrition Label")** dengan detail serupa.

### 3.3 🔴 Target API Level Android — WAJIB API 35 (Android 15) minimum

Per kebijakan Play Console terbaru: **app baru & update wajib target Android 15 (API 35)** sejak 31 Agustus 2025. Setiap Agustus, Google menaikkan syarat ini lagi — jadi pastikan saat rilis nanti, `targetSdk` di `build.gradle.kts` sudah di versi terbaru yang berlaku saat itu, bukan API level lama. Tambahkan ini sebagai acceptance criteria eksplisit di **P11-T2 (Signing Configuration)**.

### 3.4 🟡 Izin Notifikasi Runtime (Android 13+) & Permission Prompt iOS

Blueprint sudah sebut FCM, tapi belum sebut **runtime permission**:
- Android 13+ : harus minta izin `POST_NOTIFICATIONS` secara eksplisit (lihat contoh kode `06-NotificationPermission.kt`)
- iOS: harus minta izin via `UNUserNotificationCenter` — App Store akan reject kalau app push notifikasi tanpa permission prompt yang jelas

### 3.5 🟡 iOS Privacy Manifest (`PrivacyInfo.xcprivacy`)

Sejak pertengahan 2024, Apple **mewajibkan** app yang pakai SDK pihak ketiga tertentu (Firebase, Sentry, dll — semua yang dipakai blueprint kamu termasuk) untuk menyertakan **Privacy Manifest**, baik manifest dari SDK itu sendiri maupun manifest gabungan di level app. Tanpa ini, build iOS bisa **ditolak otomatis** saat submit ke App Store Connect. Tambahkan task ini ke **P11 (CI/CD)** untuk target iOS.

### 3.6 🟡 Deklarasi Ekspor Enkripsi (Apple)

Karena app hanya pakai HTTPS standar (tidak ada enkripsi kustom), tambahkan di `Info.plist`:
```xml
<key>ITSAppUsesNonExemptEncryption</key>
<false/>
```
supaya tidak perlu isi formulir export compliance manual setiap submit.

### 3.7 🟡 WebView Midtrans Snap — batasi domain (allowlist)

Karena Snap dibuka via WebView, **pastikan WebView hanya boleh navigasi ke domain Midtrans resmi** (`app.midtrans.com`, `api.midtrans.com`, dst). Kalau WebView bisa membuka domain sembarang, Apple reviewer kadang menganggap app "cuma wrapper browser" dan menolaknya. Implementasi: override `shouldOverrideUrlLoading` (Android WebViewClient) / `decidePolicyFor navigationAction` (iOS WKNavigationDelegate) dan blokir domain di luar allowlist.

### 3.8 🟡 Bukan "Designed for Families" — tapi tetap perlu kehati-hatian data anak

App ini **penggunanya orang tua dewasa**, bukan anak langsung, jadi **tidak masuk kategori "Designed for Families"** Google Play (yang punya aturan jauh lebih ketat). Tapi karena app ini **memproses** data pribadi anak (bukan dikumpulkan langsung dari anak), tetap:
- Jangan pasang iklan pihak ketiga di app ini (selain memang Anthropic/Google nggak izinkan SDK ads sembarangan, ini juga riskan kalau ada data anak nyangkut).
- Jangan kumpulkan data lebih dari yang perlu (data minimization) — terutama foto anak, hanya simpan kalau memang dipakai.

### 3.9 🟢 Khusus Indonesia: UU PDP & Kominfo PSE

Di luar syarat toko aplikasi, ada 2 hal regulasi Indonesia yang relevan dan **sering terlewat tim sekolah**:
- **UU No. 27 Tahun 2022 (PDP)** — karena memproses data pribadi anak & data finansial, sekolah sebagai **Pengendali Data Pribadi** sebaiknya punya dasar hukum pemrosesan yang jelas (kontrak layanan pendidikan dengan orang tua), dan menyiapkan prosedur kalau ada permintaan akses/hapus/koreksi data dari orang tua.
- **PSE Kominfo (PP No. 71/2019)** — sistem elektronik yang diakses publik Indonesia dan memproses transaksi (termasuk pembayaran SPP) umumnya wajib didaftarkan sebagai **PSE Lingkup Privat** ke Kominfo. Ini soal legal-administratif yayasan/sekolah, bukan soal kode, tapi taruh sebagai item di checklist go-live supaya tidak lupa.

---

## RINGKASAN: Task baru yang sebaiknya ditambahkan ke Progress Tracker (Section 20)

| Task ID baru | Nama | Phase | Alasan |
|---|---|---|---|
| P9-T6 | Implementasi Account Deletion (in-app + web link) | 9 | Wajib Play Store & App Store |
| P9-T7 | Audit Data Safety Form (Play) & App Privacy Label (Apple) | 9 | Wajib sebelum submit |
| P9-T8 | iOS Privacy Manifest (`PrivacyInfo.xcprivacy`) untuk semua SDK pihak ketiga | 9 | Wajib App Store |
| P9-T9 | WebView domain allowlist untuk Midtrans Snap | 9 | Cegah App Store review reject |
| P11-T5 | Verifikasi `targetSdk` Android sesuai syarat Play Console terkini | 11 | Wajib Play Store |
| P11-T6 | Cek & registrasi PSE Kominfo + review legal UU PDP | 11 | Regulasi Indonesia |

---

## Contoh Kode Perbaikan

Saya buatkan 6 file contoh kode di folder `kode-contoh/`, semua mengikuti aturan ketat yang sudah kamu tulis sendiri di Section 21 (tanpa `!!`, semua lewat `Result`/sealed class, KDoc lengkap, tidak ada hardcoded secret):

1. **`00-libs.versions.toml`** — version catalog yang sudah diperbarui (lihat Bagian 2)
2. **`01-Navigation.kt`** — navigasi type-safe multiplatform yang BENAR-BENAR jalan di Android+iOS (pakai `navigation-compose:2.9.2`, bukan Navigation3 alpha)
3. **`02-NetworkClient.kt`** — Ktor + Laravel Sanctum (cookie SPA + Bearer fallback), error mapping 401/403/419/422/429/500, tanpa pernah log token
4. **`03-BaseViewModel.kt`** — base architecture: `UiState`, `ApiResult` wrapper, `BaseViewModel` dengan `viewModelScope`
5. **`04-SecureTokenManager.kt`** — wrapper KVault (AES-256 Android Keystore / iOS Keychain) untuk token & data sensitif
6. **`05-AccountDeletionFeature.kt`** — fitur hapus akun lengkap (UI + ViewModel + contoh endpoint Laravel) untuk syarat Play Store/App Store
7. **`06-NotificationPermission.kt`** — runtime permission notifikasi Android 13+ dengan `expect/actual` untuk iOS

Semua file ini contoh/skeleton yang sudah benar secara arsitektur — sesuaikan nama package (`com.sdm3.parent.*`) dan sambungkan ke Koin module proyek kamu yang sebenarnya.
