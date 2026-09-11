# TRACKER.md — Audit & Fix Warna/UI Per-Layar (SDM3 Parent)

Audit statis warna/UI Compose Multiplatform. **Kode saja — tidak pernah build/run**; verifikasi = re-grep + baca ulang file.
Kontrak desain & aturan agent: lihat `AGENTS.md`.

Legend: ✅ bersih · 🔧 fixed · ⏳ pending · ❌ bug belum fix

---

## 1. Work Log (riwayat pengerjaan)

### Sesi 1 — Audit menyeluruh + fix klaster payment/home/kehadiran
- Load skill `ui-skills`/`improve-ui` (metodologi diadaptasi; edit diizinkan user secara eksplisit).
- Audit statis: `theme/*` + design-system components → bersih. Auth → bersih (1 minor: AccountDeletion).
- 4 agen audit paralel: pembayaran, rapor/nilai, info/profil/notifikasi, home/kehadiran. Semua temuan H/M/L dicatat (lihat §2).
- Fix: `PaymentUiComponents.kt`, `DetailBuktiBayarScreen.kt`, `HomeScreen.kt`, `KehadiranSiswaScreen.kt` (lihat §3).

### Sesi 2 — SkorPredicate + fix nilai/rapor/profil/notifikasi/LOW + cleanup + verifikasi
- `core/util/ScorePredicate.kt` dibuat: satu sumber kebenaran predikat/skor (lihat §3.0).
- Fix: `NilaiRaporScreen`, `DetailNilaiMapelScreen`, `HalamanRaporScreen`, `PengaturanNotifikasiScreen`, `NotifikasiScreen`, `PengumumanSekolahScreen`.
- Fix cheap/LOW: `PilihMetodeBayarScreen`, `PembayaranBerhasilScreen`, `DetailPengumumanScreen`, `PreviewRaporPdfScreen`, `VerifikasiQrRaporScreen`, `DetailInfoAnakScreen`, `KegiatanProgramScreen`, `ProfilAkunScreen`, + `AccountDeletionScreen`, + legenda "Alpa".
- Cleanup MD (`Prompt/`, `docs/`, 8 root docs) → README.md saja.
- Verifikasi akhir statis (tabel §4).

---

## 2. Temuan Audit (LENGKAP, sebelum fix)

### 2.1 Klaster Pembayaran
| ID | Sev | Lokasi | Defect |
|---|---|---|---|
| H1 | HIGH | `DetailBuktiBayarScreen.kt` | Status bayar hijau **hardcoded** ("Lunas & Terverifikasi") — antek kontrak token. |
| M1 | MED | `PaymentUiComponents.kt` `statusColor` | Cabang `when` "BELUM DIBAYAR" (non-overdue) salah → `error`, kasus TERLAMBAT. Dua state gugur warna sama. |
| M2 | MED | `PaymentUiComponents.kt` `TabRow` | **Compile break**: fungsionalitas `PrimaryTabRow(...)` tidak ada. |
| M3 | MED | `PaymentUiComponents.kt` tab | selected gold + border gold ≈2:1 di unselect; teks subtitle/mount/bilang `onSurfaceVariant@0.6f`, alpha-hack. |
| M4 | MED | `DetailBuktiBayarScreen.kt` | "Status Audit" hijau beku; `DashedDivider` default `outlineVariant`; label "PENERIMAAN"/"RINCIAN"/"TOTAL BAYAR" alpha-hack. |

### 2.2 Klaster Rapor / Nilai
| ID | Sev | Lokasi | Defect |
|---|---|---|---|
| H-1 | HIGH | `HalamanRaporScreen.kt` hero card | **Double-fill satu elemen**: `Card(containerColor=primary)` + repaint `Box.background(Brush.horizontalGradient(primary, inversePrimary@0.8))`. |
| H-2 | HIGH | `HalamanRaporScreen.kt` ~L195 | Chip "TAHUN AJARAN": fill `secondary@0.15` + teks `secondary` ≈1.8:1 (gold-on-gold). |
| H-3 | HIGH | `HalamanRaporScreen.kt` ~L288 | Pill status non-TERBIT: fill `heroContent@0.2` + teks `heroContent` → **white-on-white / dark-on-dark** di mode gelap. |
| N-1 | MED | `NilaiRaporScreen.kt` chip semester | Border selected = `primary`, fill = `primary` → border tak terlihat. |
| N-2 | MED | `NilaiRaporScreen.kt` | Skala skor **inkonsisten** antar tab: hero `90/80/70`; SubjectCard `90/75`; Formatif `secondary`; Projek `statusSuccess`. |
| N-3 | MED | `NilaiRaporScreen.kt` | Warna skor tak ikut predikat (satu skor bisa beda warna tergantung tab). |
| D-1 | MED | `DetailNilaiMapelScreen.kt` glow | `surfaceTint` utk glow dekoratif → **bocor ungu**. |
| D-2 | MED | `DetailNilaiMapelScreen.kt` predikat | "BAIK" pakai `colorScheme.secondary` (emas) vs "BAIK" info di layar nilai → tak konsisten. |
| D-3 | MED | `DetailNilaiMapelScreen.kt` TP | Skala TP `90/75/error` kontra skala predikat `90/80/70`; catatan TP threshold 75 vs 80. |

### 2.3 Klaster Info / Profil / Notifikasi
| ID | Sev | Lokasi | Defect |
|---|---|---|---|
| I-1 | HIGH | `PengaturanNotifikasiScreen.kt` ~L275 | **Off-switch nyaris tak terlihat**: track `primary@0.05`, thumb `primary@0.1`; disabled `primary@0.02`/`secondary@0.5`. |
| I-2 | MED | `PengumumanSekolahScreen.kt` ~L238 | Chip kategori: fill `secondary@0.1` + teks `secondary` ≈1.8:1. |
| I-3 | MED | `NotifikasiScreen.kt` ~L237 | Header section `primary@0.3` ≈1.93:1; timestamp `primary@0.3`; body `onSurfaceVariant@0.7`. |
| I-4 | LOW | `DetailPengumumanScreen.kt` ~L242 | Label ukuran lampiran `primary@0.4`. |

### 2.4 Klaster Home / Kehadiran
| ID | Sev | Lokasi | Defect |
|---|---|---|---|
| Hm-1 | HIGH | `HomeScreen.kt` badge "SEGERA" (ShortcutCard + ServiceItem) | Teks `primary` di atas fill gold → ≈1:1 di dark (tak terbaca). |
| Hm-2 | MED | `HomeScreen.kt` label "MEMANTAU", className, chip info | alpha-hack `onSurfaceVariant@0.5..0.7`; chip info fill `secondary@0.15`. |
| Hm-3 | HIGH | `KehadiranSiswaScreen.kt` sel hari-ini | Border `primary` = fill `primary` (tak terlihat); titik IZIN `primary` di sel navy → tak terlihat. |
| Hm-4 | MED | `KehadiranSiswaScreen.kt` glow | `surfaceTint` → bocor ungu; label legend/waktu/note alpha-hack. |
| Hm-5 | LOW | `KehadiranSiswaScreen.kt` legenda | "Alpa" pakai `colorScheme.error` (merah Material) ≠ `statusDanger` (merah brand). |

### 2.5 Auth
| ID | Sev | Lokasi | Defect |
|---|---|---|---|
| Au-1 | LOW | `AccountDeletionScreen.kt` | Tombol hapus: `containerColor=error` tapi `contentColor=onPrimary` (kegelapan di dark, putih di light). Seharusnya `onError`. |

---

## 3. Fix Register (LENGKAP per file: before → after)

### 3.0 File BARU
- **`core/util/ScorePredicate.kt`** — source of truth skor:
  - `@Composable predicateForScore(Number): Pair<String,Color>` — ≥90 "SANGAT BAIK"→success; ≥80 "BAIK"→info; ≥70 "CUKUP"→warning; else "PERLU BIMBINGAN"→danger.
  - `@Composable scoreColor(Number): Color` = `.second` dari predicateForScore.
  - Dipakai oleh NilaiRapor + DetailNilaiMapel agar skala seragam; tak ada lagi `when` skala duplikat di layar.

### 3.1 Klaster Pembayaran
**`PaymentUiComponents.kt`**
- `PrimaryTabRow(...)` → `TabRow(...)` (compile break).
- `statusColor`: cabang "BELUM DIBAYAR" (non-overdue) `error` → `statusWarning` (error hanya utk "TERLAMBAT" + overdue).
- `TabRow` colors: `selectedContentColor = primary` (bukan secondary/gold), `unselectedContentColor = onSurfaceMuted` (bukan `onSurfaceVariant@0.6f`).
- Teks subtitle, month-name, item-count, progress: `colorScheme.primary@0.4f`/`onSurfaceVariant@0.5f` → `onSurfaceMuted`/`onSurfaceFaint`.

**`DetailBuktiBayarScreen.kt`**
- Teks status "Lunas & Terverifikasi" hijau hardcoded → `statusHeadline.uppercase()` diberi `color = statusColor` (ikut status nyata sukses/warning/danger).
- `ReceiptRow` + param baru `valueColor: Color = MaterialTheme.colorScheme.primary`.
- Row "Status Audit" → `statusColor`.
- `DashedDivider` default `color = outlineVariant` → `outline`.
- Label "PENERIMAAN", "RINCIAN TRANSAKSI", "TOTAL BAYAR" `primary@0.4f/0.5f` → `onSurfaceMuted`.

**`PilihMetodeBayarScreen.kt`**
- "RINGKASAN TAGIHAN" `primary@0.4f` → `onSurfaceMuted`.
- "Jatuh tempo" `onSurfaceVariant@0.7f` → `onSurfaceMuted`.
- `HorizontalDivider primary@0.05f` → `outline`.
- "TOTAL BAYAR" `primary@0.5f` → `onSurfaceMuted`.
- Deskripsi metode `onSurfaceVariant@0.5f` → `onSurfaceFaint`.

**`PembayaranBerhasilScreen.kt`**
- 4× `HorizontalDivider primary@0.05f` → `outline` (replaceAll).
- Teks body "Dana telah terotentikasi…" `onSurfaceVariant@0.7f` → `onSurfaceMuted`.

### 3.2 Home / Kehadiran
**`HomeScreen.kt`**
- Badge "SEGERA" (ShortcutCard + ServiceItem): `color = primary` → `onSecondary` (kontras di fill gold, light+dark).
- Label "MEMANTAU·N ANAK", baris className, chip info (`secondary@0.15`→`secondaryContainer`), teks comingSoon → `onSurfaceMuted`/`onSurfaceFaint`/`onSecondaryContainer`.

**`KehadiranSiswaScreen.kt`**
- Border sel hari-ini `colorScheme.primary` → `heroContent.copy(alpha=0.35f)` (border kontras vs fill primary).
- Titik IZIN: `primary` → `if (onPrimary) heroContent else primary` (terlihat di sel navy).
- Glow `surfaceTint@0.3` → `primaryContainer`.
- Label legend / waktu / SummaryCard `color.accent...@0.x` → `onSurfaceMuted`/`onSurfaceFaint`.
- Legenda "Alpa": `colorScheme.error` → `statusDangerColor()` (brand danger).

### 3.3 Nilai
**`NilaiRaporScreen.kt`**
- Chip filter semester: border selected `primary` (tak terlihat) → `colorScheme.secondary` (gold, kontras vs fill primary).
- Hero: `when` predikat lokal (90/80/70) → `predicateForScore(avgScore)`; hapus `val statusInfo` & `statusDanger` yg mati.
- SubjectCard: `when` skala 90/75 → `scoreColor(subject.score)`; hapus 3 variabel status yg tak terpakai.
- Formatif: teks skor `secondary` → `scoreColor(item.score)`.
- Projek: teks skor `statusSuccess` → `scoreColor(item.nilai)`; hapus `statusSuccess` lokal yg mati.
- Deskripsi 2× `onSurfaceVariant@0.6f` → `onSurfaceMuted`.
- "Predikat" `onSurfaceVariant@0.4f` → `onSurfaceMuted`; "Predikat …" (projek) `@0.5f` → `onSurfaceFaint`.
- StatMiniCard label `onSurfaceVariant@0.4f` → `onSurfaceFaint`.

**`DetailNilaiMapelScreen.kt`**
- Glow `surfaceTint@0.3` → `heroContent@0.3` (putih, bukan bocor ungu).
- Predikat hero: `when` (gold utk BAIK) → `predicateForScore(finalScore)` (BAIK jadi info biru, konsisten).
- Skala TP: `when` 90/75/`colorScheme.error` → `scoreColor(tpScore)` (batas 90/80/70).
- Catatan TP: threshold `90/75` → `90/80/70` (baris catatan TP selaras dgn skala baru).
- Divider `primary@0.05f` → `outline`; teks catatan `onSurfaceVariant@0.7f` → `onSurfaceMuted`.
- Hapus `statusWarning` & `statusDanger` lokal yang mati.

### 3.4 Rapor
**`HalamanRaporScreen.kt`**
- Hero double-fill: hapus `Box.background(Brush.horizontalGradient(primary, inversePrimary@0.8))` → **satu fill** `Card containerColor=primary` (Box tetap utk padding).
- Chip "TAHUN AJARAN": fill `secondary@0.15`+teks `secondary` → `secondaryContainer`/`onSecondaryContainer`.
- Pill status non-TERBIT ("DIPROSES"/"SIAP UNDUH"/—): fill `heroContent@0.2`+teks `heroContent` → `primaryContainer`/`onPrimaryContainer` (kontras di light & dark).
- Import `Brush`, `Color` terbuang → dihapus (re-grep 0 pemakaian).

**`PreviewRaporPdfScreen.kt`**
- Label ukuran file `primary@0.4f` → `onSurfaceFaint`.
- Ikon sync `primary@0.3f` → `onSurfaceMuted`.
- Teks body "Sedang menyinkronkan…/"Dokumen siap…" `primary@0.7f` → `onSurfaceMuted`.

**`VerifikasiQrRaporScreen.kt`**
- Hint "Scan QR Code Resmi…" `primary@0.4f` → `onSurfaceMuted`.
- Pesan hasil `primary@0.7f` → `onSurfaceMuted`.
- `HorizontalDivider primary@0.05f` → `outline`.
- `VerifInfoRow` label `primary@0.4f` → `onSurfaceMuted`.

### 3.5 Profil
**`PengaturanNotifikasiScreen.kt`**
- Switch OFF (tak terlihat): `uncheckedThumb = primary@0.1`, `uncheckedTrack = primary@0.05` → `thumb = outline`, `track = surfaceContainerHighest`.
- Disabled: `secondary@0.5`/`primary@0.02` → `onSurface@0.38`/`onSurface@0.12`.
- 4× `HorizontalDivider primary@0.05f` → `outline`.

**`ProfilAkunScreen.kt`**
- `HorizontalDivider primary@0.06f` (L633) → `outline`.

### 3.6 Notifikasi
**`NotifikasiScreen.kt`**
- Header section `primary@0.3` (1.93:1) → `onSurfaceMuted`.
- Timestamp `primary@0.3` → `onSurfaceFaint`.
- Body `onSurfaceVariant@0.7f` → `onSurfaceMuted`.
- Tint unread `primary@0.03f` **dipertahankan** (dekoratif, bukan kontras).

**`PengumumanSekolahScreen.kt`**
- Chip kategori: fill `secondary@0.1`+teks `secondary` → `secondaryContainer`/`onSecondaryContainer`.

**`DetailPengumumanScreen.kt`**
- Label ukuran lampiran `primary@0.4f` → `onSurfaceFaint`.

### 3.7 Info Anak (cheap batch)
- `DetailInfoAnakScreen.kt`: 3× divider `primary@0.05f` → `outline`.
- `KegiatanProgramScreen.kt`: 1× divider `primary@0.05f` → `outline`.

### 3.8 Auth
- `AccountDeletionScreen.kt`: tombol hapus `contentColor = onPrimary` → `onError`.

---

## 4. Verifikasi Akhir (statis, sesi 2)
| Pemeriksaan | Hasil |
|---|---|
| `surfaceTint` di seluruh `feature/*` | **0** (bocor ungu hilang) |
| `HorizontalDivider(color=primary.copy(0.05f/0.06f))` | **0** → semua `outline` |
| Predikat skor `when` duplikat di nilai/rapor (`SANGAT BAIK`/`PERLU BIMBINGAN`) | **0** → semua via `ScorePredicate.kt` |
| `to statusDanger` / deviasi helper status di nilai/rapor | **0** |
| Variabel helper mati (statusDanger/statusWarning/pStatusInfo) di file fix | **0** (dibersihkan) |
| Import mati (`Brush`, `Color`) | **0** |
| Baca ulang manual semua file fix (sesi 2) | OK — tidak ada referensi menggantung, param mogok, atau duplikasi warna tersisa |

---

## 5. Cleanup MD
Dihapus: `Prompt/` (75 file: standards, ERP, Blueprints, Checklists, Examples, Prompt/), `docs/OWASP_CHECKLIST.md`, `Production.md`, `PROJECT.md`, `PRD_REDESIGN_SDM3_PARENT.md`, `PRODUCTION_BACKEND_SYNC_FIREBASE.md`, `RemainingWork.md`, `RINGKASAN_PEKERJAAN.md`, `PENDING_TASKS.md`.
Dipertahankan: `README.md`. **Jangan dibuat ulang** (kontrak di `AGENTS.md`).

---

## 6. Deferred / Dibiar sama sekali
- ⏳ Layar guru (`feature/guru/ui/{GuruAbsensiScreen, AbsensiSayaScreen, TeacherHomeScreen}.kt`) — ada perubahan dari sesi lama (refactor), **belum diaudit ulang** untuk warna.
- ⏳ Verifikasi QR skenario positif (yg negatif sudah fixed).
- ⏳ Template/wizard baru apa pun.
- Konsiderasi disengaja: ±59 sisa `color.copy(alpha=0.05..0.4f)` adalah **fill dekoratif** (bg ikon-badge 5%, sheen hero, glow gold) — bukan bug kontras, dibiarkan.

---

## 7. Konteks Repo (di luar scope audit warna ini)
Working tree berisi pula refactor desain-system & konsolidasi komponen dari sesi-sesi sebelumnya (60 file, −5343 baris vs HEAD 940fc7f): pemusnahan komponen duplikat (`IconText`, `NetworkErrorDialog`, `Sdm3ActionComponents`, `Sdm3ListItem`, `Sdm3ProgressIndicator`, `Sdm3Snackbar`, `Sdm3StatTile`) → digantikan `ScreenScaffold.kt`, `ScreenUiState.kt`, `Sdm3CommonPatterns.kt`, plus polish di auth/guru/infoanak/pembayaran dll.
Catatan: belum ada commit untuk semua perubahan di atas; `AGENTS.md`, `TRACKER.md`, `ScorePredicate.kt` masih untracked.