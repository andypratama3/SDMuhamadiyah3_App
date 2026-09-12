# AGENTS.md — Konteks Audit & Aturan UI (SDM3 Parent)

Konteks ringkas untuk agent yang mengerjakan UI Compose Multiplatform aplikasi SDM3 Parent.
Tracker detail per-layar ada di `TRACKER.md`.

## Batasan pengerjaan
- **Kode saja — JANGAN pernah menjalankan/membangun apa pun** (tidak ada gradle/build test).
- Verifikasi = inspeksi statis (re-grep) + teliti ulang file yang diedit.
- Fokus bug UI: warna yang bertabrakan/duplikat pada satu elemen + alpha-hack yang merusak kontras.
- Jangan mengubah file di luar konteks warna/UI tanpa izin.

## Aturan warna (kontrak desain)
- Satu-satunya sumber hex beku = `core/designsystem/theme/Color.kt` (`ProductSchoolColors`, light+dark).
- Jangan pakai `colorScheme.primary.copy(alpha=...)` untuk teks/border/divider. Ganti dengan token:
  - `ProductSchoolTheme.colors.onSurfaceMuted` (#525B66) — teks sekunder/label non-aksen.
  - `ProductSchoolTheme.colors.onSurfaceFaint` (#8B93A0) — teks tersier/ukuran kecil.
- Warna status pakai helper `core/designsystem/theme/ThemeExt.kt`:
  `statusSuccessColor()`, `statusWarningColor()`, `statusDangerColor()`, `statusInfoColor()`.
- Hero di atas navy: teks/ikon = `heroContentColor()` (= onPrimary).
- Untuk fill chip/tag jangan `secondary.copy(alpha=0.1f..0.15f)` dengan teks `secondary`
  (kontras ~1.8:1). Pakai `secondaryContainer`/`onSecondaryContainer` atau
  `primaryContainer`/`onPrimaryContainer`.
- Divider (`HorizontalDivider`) → `colorScheme.outline`. Jangan `primary.copy(alpha=0.05f)`.
- Hindari `surfaceTint` untuk glow dekoratif pada fitur → bocor ungu; pakai `heroContent`/`primaryContainer`.
- Tab/chip selected: border harus kontras terhadap fill (mis. primary fill + secondary border).

## Skor / predikat nilki (SERAGAM)
`core/util/ScorePredicate.kt` — satu sumber kebenaran untuk warna + label skor:
- `predicateForScore(Number)` → Pair(label, color); `scoreColor(Number)` → Color.
- Batas: ≥90 SANGAT BAIK(success), ≥80 BAIK(info), ≥70 CUKUP(warning), else PERLU BIMBINGAN(danger).
- Jangan menulis `when` skala ulang di layar lain — pakai helper ini.

## Status audit per-klaster
- Bersih (audited, tanpa bug): theme/design-system components, auth (kecuali AccountDeletion sdh fixed), SPP/proses pembayaran, guru (`GuruAbsensiScreen`, `AbsensiSayaScreen`, `TeacherHomeScreen`).
- Fixed: detail lihat `TRACKER.md`.
- Belum diaudit: layar guru lain jika ada, verifikasi QR positif, wizard baru apa pun.

## MD
- Jangan buat ulang `Prompt/` atau doc lama. `README.md` saja yang dipertahankan.