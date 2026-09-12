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

### Sesi 3 — Deep re-audit "cek lagi" (paralel 3 agen: alpha-hack, kontras/duplikasi, compile-sanity)
- Compile breaker ditemukan & difix: `PaymentUiComponents.kt` `else -> statusWarning` di scope `PaymentHeroCard` (var lokal tidak ada) → tambah `statusDanger` & `statusWarning` lokal; `TERLAMBAT` `colorScheme.error` → `statusDangerColor()`. `ProsesPembayaranScreen` butuh `statusDanger` lokal (grep bukti) → dibenahi.
- Double-fill `PaymentHeroCard`: gradient `[primary, inversePrimary@0.5]` di atas `Card primary` → `[Color.Transparent, inversePrimary@0.35]` (killer-bug warna).
- Pill emas-on-emas: HalamanRapor pill TERBIT (`onSecondary`), DetailPengumuman "INFORMASI", predikat NilaiRapor/DetailNilaiMapel → solid `predicateColor` + `onPrimary`.
- `colorScheme.error` vs `statusDangerColor()` diseragamkan: KehadiranSiswa (SummaryCard ALPA, log ALPA, DayContent dot ALPA, Minggu), PaymentUi (TERLAMBAT), ProsesPembayaran (FAILED), DetailBuktiBayar (FAILED). Sisa `colorScheme.error` = pesan error boks (sah).
- Tab gold-teks: Notifikasi + PengumumanSekolah `selectedContentColor` → `primary`.
- Design-system: AppSection/ScreenScaffold/Sdm3TextField/Sdm3CommonPatterns/nav-rail/bottom-nav → token (`onSurfaceMuted`/`onSurfaceFaint`).
- Guru: GuruAbsensi FilterChip solid-fill (`selectedContainerColor=color` + `selectedLabelColor=onPrimary`); teacher-home/Absensi kept.
- PilihAnak selected name/gold → `primary`; ceklist ikon → `onSecondary`; RadioButton metode bayar → `primary`.
- Sisa 168 `copy(alpha)` di-sweep: semua terklasifikasi **dekoratif/hero-on-navy/badge** (detail §4), bukan teks melanggar kontras.
- Var/import mati dibersihkan: `heroContent` (NilaiRapor), reindent pill HalamanRapor.

### Sesi 5 — Audit layout/overlap (4 agen paralel) + fix animasi snap & overflow
- **Animasi intro "snap" (bukan eased) LoginScreen**: semua `graphicsLayer`/`blur` digerakkan boolean `startAnimation` yang flip instan → logo/title/kartu/tombol *pop* mendadak, `animateContentSize` tidak berfungsi. Diffix: 4 `animateFloatAsState` bernilai `revealLogo/Title/Form/Action` (tween 700, delay kaskade 0/100/200/300ms), scale `0.85→1`, blur `16→0`, `translationY` eased. `animateContentSize` dihapus.
- SplashScreen: Canvas mesh background `alpha(if startAnimation)` pop → dipakai `logoAlphaAnim` (fade menyatu dengan logo).
- **PilihAnak (HIGH)**: list-item terakhir tertutup island "Lanjutkan Ke Dashboard" (~122dp) — `contentPadding bottom` 56→**140.dp**.
- **KehadiranSiswa (MED)**: state Error/Empty tidak pakai `padding` scaffold (konten tergeser ke atas) → dibungkus `Box(fillMaxSize().padding(padding))`.
- **DetailPengumuman (MED)**: shimmer loading tanpa padding scaffold → dibungkus; row meta tanggal/penulis tanpa weight/maxLines → `weight(1f)` + `Ellipsis` + import `TextOverflow`.
- **HalamanRapor (MED)**: hero header baris semester `titleLarge` tanpa weight → grup kiri `weight(1f)` + `Ellipsis`; baris arsip `semesterLabel • TA` → `maxLines=1` + `Ellipsis`.
- **GuruAbsensi (MED)**: 5 `FilterChip` dalam `Row` non-wrap → terpotong di layar sempit → `FlowRow` (wrap) + `OptIn(ExperimentalLayoutApi)`.
- **ProfilAkun (MED)**: clip 20dp vs Surface radius 24dp + `shadowElevation` terkunci oleh clip induk (bayangan mati, sudut menumpuk) → seragam 24dp, drop `shadowElevation`; dua tombol destruktif tinggi 52/56 → diseragamkan **56dp**.
- **PilihMetodeBayar (LOW)**: "TOTAL BAYAR" nilai Rupiah tanpa batas → `weight(1f, fill=false)` + `Ellipsis` + `TextAlign.End`.
- **DetailInfoAnak (LOW)**: judul quick-nav `maxLines=1` tanpa ellipsis → `TextOverflow.Ellipsis`.
- Temuan yang dibiarkan (tercatat §6): glow OTP di dalam scroll-column (Visual ringan, tidak menimpa konten), Onboarding pager pendek di HP kecil, double bottom-inset KegiatanProgram, status Error/Empty self-center di layar guru.
- Verifikasi statis: seimbang-brace seluruh 10 file diubah; import baru diverifikasi (`TextOverflow`, `TextAlign`, `FlowRow`, `ExperimentalLayoutApi`, `animateFloatAsState` via wildcard).

### Sesi 4 — Audit @Preview (40 preview, 3 agen paralel) + polish preview-safe
- **Semua 40 @Preview**: referensi simbol masih ada (tidak ada komponen lama `IconText`/`NetworkErrorDialog`/`Sdm3ActionComponents`/`Sdm3ListItem`/`Sdm3ProgressIndicator`/`Sdm3Snackbar`/`Sdm3StatTile`), arity/tipe argumen benar, nama preview unik, semua dibungkus `SDM3Theme`.
- **Preview-crash risk LoginScreen** (passing `koinViewModel()` langsung di preview, layar tanpa guard inspection) → `viewModel` dibuat nullable: default `if (LocalInspectionMode.current) null else koinViewModel()`; `uiState`, `LaunchedEffect(effect)`, dan semua `onIntent` diberi null-guard. NavHost tetap pass VM eksplisit (aman), preview pass apa-apa.
- **Preview isi-kosong → ditampilkan konten**: `NilaiRaporScreen` preview di-seed data (2 GradeDto, formatif, projek, `isEmpty=false`, semesters) sehingga hero + pill predikat + tab ter-render; `DetailBuktiBayarScreen` preview di-seed `PaymentDto(status="settlement")` sehingga kwitansi ter-render; `DetailInfoAnakScreen` preview di-seed `StudentDto` (bukan lagi skeleton loading).
- **Alpha-hack teks (SectionHeader) dihapus** (tombol deck teks pada permukaan terang): ProfilAkun `Modifier.alpha(0.5f)` ×2, PengaturanNotifikasi `Modifier.alpha(0.5f)` ×2, VerifikasiQr `Modifier.alpha(0.5f)`, DetailPengumuman row tanggal/penulis `Modifier.alpha(0.6f)`. Tinggal shimmer-skeleton `.alpha(0.5f)` (bukan teks) — dibiarkan.
- Label disabled switch `primary@0.3` → `onSurfaceFaint` (PengaturanNotifikasi L268).
- **Dead imports dibersihkan (±30 baris di 18 file)**: design-system (Sdm3Button×3, Sdm3Card×2, Sdm3EmptyState×1, Sdm3ErrorState×1, Sdm3GlassCard×5, Sdm3TextField×1), auth (Login×5, Onboarding×2, PilihAnak×4, PilihAnakBottomSheet×1, AccountDeletion×1, VerifikasiOtp×5), home (HomeScreen×5), nilai (DetailNilaiMapel×3, NilaiRapor×1), rapor (HalamanRapor×2, VerifikasiQr×1), pembayaran (PilihMetodeBayar×1, DetailBuktiBayar×0), notifikasi (DetailPengumuman×1), profil (ProfilAkun×1), infoanak (DetailInfoAnak×2). Semua diverifikasi 0 pemakaian sebelum dihapus.
- Preview fn `Sdm3Button` & `Sdm3TextField` → `private` (konsisten).
- `StatusChip` dianalisis: pakai `color@0.1` fill + teks solid warna sama — TAPI semua pemakaian aktual memakai status color/primary (kontras ≥4.5:1), tidak pernah `secondary`; kategori "diterima", dicatat §6.
- Verifikasi statis penuh (tabel §4-Sesi 4).

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

### 3.9 Sesi 3 — deep re-audit (per file: before → after)
**`PaymentUiComponents.kt`**
- (compile) `else -> statusWarning` di `PaymentHeroCard` tanpa var → tambah `val statusDanger`/`val statusWarning` lokal.
- `TERLAMBAT` `colorScheme.error` → `statusDangerColor()` (hero + FeeItemCard + PaymentHistoryCard).
- Double-fill gradient hero `[primary, inversePrimary@0.5]` → `[Color.Transparent, inversePrimary@0.35]` (Card tetap `primary`).
- Chevron year-header `primary@0.5` → `onSurfaceMuted`; chevron card `primary@0.15` → `onSurfaceMuted`.

**`ProsesPembayaranScreen.kt`**
- FAILED `colorScheme.error` → `statusDangerColor()` (lokal `statusDanger` ditambahkan).
- 218/275/350 teks alpha-hack → `onSurfaceMuted`.

**`DetailBuktiBayarScreen.kt`**
- FAILED `colorScheme.error` → `statusDangerColor()`; rincian badge fill tetap `statusColor@0.1` (diterima).

**`KehadiranSiswaScreen.kt`**
- ALPA `colorScheme.error` → `statusDangerColor()` (SummaryCard, log, DayContent dot, angka Minggu).
- PULANG `colorScheme.secondary` → `statusInfoColor()` (log + dot) — bukan lagi gold-on-gold.
- Label card-absen `primary@0.3` → `onSurfaceMuted`.

**`HalamanRaporScreen.kt`**
- Pill TERBIT: teks `onSecondary` (kontras di fill gold, light+dark); fill tetap `secondary`.
- "Status: …" `onSurfaceVariant@0.6` → `onSurfaceMuted`; reindent pill.

**`DetailPengumumanScreen.kt`**
- Pill "INFORMASI": teks `primary` → `onSecondary` (fill gold).

**`NilaiRaporScreen.kt` + `DetailNilaiMapelScreen.kt`**
- Pill predikat: `heroContent@0.15/0.1` fill + teks `predicateColor` → solid `predicateColor` + teks `onPrimary` (hapus BorderStroke; `heroContent` var mati di NilaiRapor dihapus).

**`NotifikasiScreen.kt` / `PengumumanSekolahScreen.kt`**
- Tab: `selectedContentColor secondary` → `primary`; unselected → `onSurfaceMuted` (keduanya).

**`PilihAnakScreen.kt`**
- Nama/inisial terpilih `secondary` (gold) → `primary`; ikon ceklis `secondary` → `onSecondary`.
**`PilihAnakBottomSheet.kt`**
- Ikon ceklis tint → `onSecondary`; baris 59/107 alpha-hack → `onSurfaceMuted`.

**`AccountDeletionScreen.kt`**
- StatusChip "Proses 7 Hari": teks `secondary` → `statusWarningColor`; "Final & Absolut" → `statusDangerColor`.

**`GuruAbsensiScreen.kt`**
- FilterChip: `selectedContainerColor = secondary@0.2` + teks `secondary` → solid `color` + `selectedLabelColor = onPrimary`; 2× label `primary@0.3` → `onSurfaceMuted`.

**`TeacherHomeScreen.kt` / `AbsensiSayaScreen.kt` / `KegiatanProgramScreen.kt` / `PengaturanNotifikasiScreen.kt` / `ProfilAkunScreen.kt`**
- Batch mekanis: `primary@0.3..0.5`/`onSurfaceVariant@0.7` → `onSurfaceMuted`; `@0.1..0.2` → `onSurfaceFaint`; chevrons → `onSurfaceMuted` (ProfilAkun 530/537/643/607/615/623, dsb).

**Design-system components (semua → token)**
- `AppSection.kt`: subtitle `secondary@0.6` → `onSurfaceMuted`; aksi "Lihat Semua" `secondary@0.9` → `primary`.
- `ScreenScaffold.kt`: subtitle `secondary@0.6/0.7` → `onSurfaceMuted`.
- `Sdm3TextField.kt`: label → `onSurfaceMuted`, placeholder → `onSurfaceFaint`, leading icon → `onSurfaceMuted`.
- `Sdm3CommonPatterns.kt`: default labelColor `secondary@0.6` → `onSurfaceMuted`; divider `primary@0.05` → `outline`; bg main/dialog glow `primary@0.03` (dekoratif) dipertahankan.
- `Sdm3AdaptiveNav.kt` + `SDM3BottomNavBar.kt`: unselect `primary@0.4` → `onSurfaceMuted`.

**`OnboardingScreen.kt` / `LoginScreen.kt` / `VerifikasiOtpScreen.kt`**
- "LEWATI", subtitle, "Sudah punya akun?", lupa-kunci, versi, deskripsi OTP, timer → `onSurfaceMuted`/`onSurfaceFaint`.
- "KIRIM ULANG KODE" auto-callout `secondary` → `primary` (Onboarding + VerifikasiOtp).

**`PilihMetodeBayarScreen.kt`**
- RadioButton `selectedColor` `secondary` → `primary`.

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

### Sesi 3 (ulang penuh, semua scope)
| Pemeriksaan | Hasil |
|---|---|
| Tek `primary@0.3..0.9`/`onSurfaceVariant@0.5..0.7`/`onSurface@0.x` pada permukaan terang (alpha-hack) | **0** → semua token (`onSurfaceMuted`/`onSurfaceFaint`/`outline`) |
| `colorScheme.error` utk status (ALPA/TERLAMBAT/FAILED/Minggu/PULANG) | **0** → semua `statusDangerColor()`/`statusInfoColor()`; sisa `error` = boks pesan error improvi (sah) |
| Pill/capsul fill `secondary@0.1..0.2`/`heroContent@0.1..0.2` + teks sama | **0** → `secondaryContainer`/`onSecondaryContainer` atau solid `predicateColor`/`onPrimary` |
| Tab/Radio `selectedContentColor`/`selectedColor` berwarna gold | **0** → `primary` |
| Double-fill satu elemen (Card + Box background opaque) | **0** — `PaymentHeroCard` gradient `[Transparent, inversePrimary@0.35]` |
| Compile-sanity agen (referensi var status di scope) | OK — `statusWarning`/`statusDanger` lokal ditambahkan di PaymentHeroCard & ProsesPembayaran |
| `heroContent` var mati di NilaiRapor | **0** (dihapus) |
| Import `ProductSchoolTheme` di semua file yang memakainya | OK (wildcard `theme.*` atau eksplisit) |
| Sweep sisa `copy(alpha)` (ruang awal 168) | Semua **dekoratif** — bg ikon-badge 5%, sheen hero, glow navy, badge `statusColor@0.12` (diterima §6), drag-handle, switch disabled. Tidak ada teks melanggar kontras. |

### Sesi 4 (preview & polish)
| Pemeriksaan | Hasil |
|---|---|
| 40 `@Preview` — simbol mati (7 komponen lama dihapus, DTO/state berubah) | **0** → semua referensi valid |
| 40 `@Preview` — arity/tipe argumen vs signature layar | OK — semua cocok |
| 40 `@Preview` — dibungkus `SDM3Theme` & nama unik | OK |
| Preview crash-risk `koinViewModel()` (LoginScreen eksplisit) | **Fixed** — nullable VM + guard inspection; NavHost pass eksplisit |
| Preview tampil konten (bukan kosong/loading saja) — NilaiRapor, DetailBuktiBayar, DetailInfoAnak | **Fixed** — seed data & `isEmpty=false` |
| Alpha-hack teks `SectionHeader`/row detail (`Modifier.alpha(0.5/0.6)`) permukaan terang | **0** → dihapus semua; sisa `.alpha` = shimmer skeleton |
| Dead import (dihapus, tiap-file divalidasi 0 pemakaian) | **±30** baris/18 file, verifikasi lintas-simbol PASS |
| Preview fn non-private (Sdm3Button, Sdm3TextField) | → `private` |
| `StatusChip` pola fill `color@0.1`+teks solid-sama | Diterima — pemakaian aktual = statusColor/primary (≥4.5:1); tak pernah gold |

---

## 5. Cleanup MD
Dihapus: `Prompt/` (75 file: standards, ERP, Blueprints, Checklists, Examples, Prompt/), `docs/OWASP_CHECKLIST.md`, `Production.md`, `PROJECT.md`, `PRD_REDESIGN_SDM3_PARENT.md`, `PRODUCTION_BACKEND_SYNC_FIREBASE.md`, `RemainingWork.md`, `RINGKASAN_PEKERJAAN.md`, `PENDING_TASKS.md`.
Dipertahankan: `README.md`. **Jangan dibuat ulang** (kontrak di `AGENTS.md`).

---

## 6. Deferred / Dibiar sama sekali
- ⏳ Layar guru: **sebagian besar sudah diaudit sesi 3** (`GuruAbsensiScreen`, `AbsensiSayaScreen`, `TeacherHomeScreen` difix); sisa layar guru yang belum dicek mendalam = dashboard/kelola-absensi lainnya jika ada.
- ⏳ Verifikasi QR skenario positif (yg negatif sudah fixed).
- ⏳ Template/wizard baru apa pun.
- Diterima sesi 3:
  - Badge jumlah tagihan/`statusColor.copy(alpha=0.1..0.12f)` fill + teks status penuh (tidak ada token on-status-container di `Color.kt`; kontras ok karena teks solid).
  - `LocalizedGlow`/tab-indicator/garis progress gold `secondary` (`Material3` overflow-glow; bukan kontras teks).
  - Label/ikon `heroContent.copy(alpha)` di atas navy (kontras ok).
  - Switch off/disabled (`PengaturanNotifikasi` disable color `onSurface@0.38/0.12`), drag-handle `primary@0.1`, cursor OTP `primary@0.4` (dekoratif non-teks).
  - Ikon empty-state `primary@0.2` (icon, bukan teks).
- Diterima sesi 4:
  - `StatusChip` (fill `color@0.1` + teks solid): seluruh pemakaian aktual memakai `statusSuccess/Warning/Danger` atau `primary` (kontras ≥4.5:1, tak pernah `secondary`/gold). Hanya preview-nya yang menampilkan `primary`.
  - Shimmer skeleton `Modifier.alpha(0.5f)` (placeholder non-teks) di ProfilAkun/PengaturanNotifikasi.
- Konsiderasi disengaja: sisa `color.copy(alpha=0.05..0.4f)` adalah **fill dekoratif** — bukan bug kontras, dibiarkan.

---

## 7. Konteks Repo (di luar scope audit warna ini)
Working tree berisi pula refactor desain-system & konsolidasi komponen dari sesi-sesi sebelumnya (60 file, −5343 baris vs HEAD 940fc7f): pemusnahan komponen duplikat (`IconText`, `NetworkErrorDialog`, `Sdm3ActionComponents`, `Sdm3ListItem`, `Sdm3ProgressIndicator`, `Sdm3Snackbar`, `Sdm3StatTile`) → digantikan `ScreenScaffold.kt`, `ScreenUiState.kt`, `Sdm3CommonPatterns.kt`, plus polish di auth/guru/infoanak/pembayaran dll.
Catatan: belum ada commit untuk semua perubahan di atas; `AGENTS.md`, `TRACKER.md`, `ScorePredicate.kt` masih untracked.