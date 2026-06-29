# 16 — Dialog Standards

## Taxonomy

| Type | Use For | Dismissible by tap-outside? |
|---|---|---|
| `EduOctoDialog` (centered) | Confirmations, short single-decision prompts | Yes, unless destructive-confirm |
| `EduOctoBottomSheet` | Forms, option pickers, multi-field quick actions | Yes |
| `EduOctoFullScreenDialog` | Complex flows (multi-step PPDB step, image viewer) | No — explicit close button only |
| `EduOctoSnackbar` | Transient, non-blocking confirmations ("Saved") | N/A — auto-dismiss |

## Confirmation Dialog Rules

1. Title states the action in plain language ("Hapus data siswa ini?"), not a generic "Are you
   sure?"
2. Body explains the consequence, especially if irreversible ("Data kehadiran 6 bulan terakhir akan
   ikut terhapus dan tidak dapat dikembalikan.").
3. Destructive confirm button uses `danger` color and a specific verb label ("Hapus"), never a
   generic "OK"/"Yes." Cancel/secondary action is the visually subordinate button, always present,
   always the easier tap target to reach accidentally-safe placement (per platform convention —
   right-side primary on most contexts, but never make Cancel and Delete adjacent without a gap).
4. Destructive actions affecting financial records (deleting a payment, voiding an invoice) require
   **typed confirmation** (re-enter a short code or the record's reference number) in addition to the
   dialog, per `27-SECURITY.md` audit requirements.

## Bottom Sheet Rules

1. Used for quick actions launched from a list/detail context without losing the underlying screen's
   place (e.g. "Record Payment" from a student's invoice row).
2. Drag handle at top, rounded `radiusLarge` top corners, `GlassSurface` background per
   `08-GLASSMORPHISM.md`.
3. Contains its own scroll if content exceeds ~70% of screen height; never exceeds 90% of screen
   height (always leave context of the underlying screen visible at the edges).
4. Primary action button is sticky at the bottom of the sheet, not requiring scroll to reach.

## Motion

All dialogs/sheets use the standard entry per `09-ANIMATION.md`: scale 0.95→1.0 + fade for centered
dialogs (`durationDefault`), slide-up + fade for bottom sheets (`durationSlow`), both with
`EduOctoEasing`.

## Stacking

Only one dialog/sheet may be visible at a time. A flow that seems to require dialog-on-dialog (e.g.
confirm inside a form sheet) should instead replace the sheet's content in place (step transition)
rather than stacking a second surface on top.

## Anti-Patterns

- Using a dialog to display a long-form read-only document (use a full screen or dedicated route).
- A confirmation dialog with both buttons styled identically (no clear primary/destructive emphasis).
- Auto-dismissing a confirmation dialog after a timeout — confirmations always require an explicit
  user decision.
