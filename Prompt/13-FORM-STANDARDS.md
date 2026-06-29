# 13 — Form Standards

## Layout

1. Single column on mobile; two-column grouping allowed on tablet/desktop only for clearly paired
   fields (e.g. City + Postal Code), never for unrelated fields.
2. Group fields under a `titleMedium` section header when a form has more than ~5 fields (e.g. PPDB
   registration: "Student Data," "Parent Data," "Document Upload").
3. Label above field, always visible (never placeholder-as-label). Placeholder text shows a format
   example only (`"08xxxxxxxxxx"`), never the field's purpose.
4. Required fields marked with a subtle asterisk + `onSurfaceMuted` "Required" hint pattern handled
   once in the shared `EduOctoTextField` component, not per-screen.

## Validation

1. **Inline, on blur — not on every keystroke.** Validate when focus leaves a field, and re-validate
   on submit attempt. Validating on every keystroke before the user finishes typing reads as hostile.
2. **Errors appear directly under the field**, in `danger` color, `bodySmall`, with a specific
   message ("Nomor HP harus 10–13 digit," not "Invalid input").
3. **Submit button stays enabled** even with invalid fields present (so the user can tap it and be
   shown exactly what's wrong) — but tapping with errors scrolls to and focuses the first invalid
   field rather than submitting.
4. **Async/server-side validation** (e.g. "NISN already registered") surfaces the same way as local
   validation — same visual treatment, no special-cased styling.

## Submission UX

1. Submit button shows a loading spinner *inside the button* (button stays same size, label replaced
   by spinner) — never a full-screen blocking spinner for a form submit unless the action is
   destructive/irreversible and a full takeover is intentional.
2. On success: navigate forward or show a success state (`EduOctoSuccessBanner`) — never just close
   silently. Pair with `success()` haptic per `10-HAPTICS.md`.
3. On failure: keep all entered data intact, show the error via `19-ERROR-STATES.md` pattern inline
   near the submit button, never via a generic toast that loses context.
4. Multi-step forms (PPDB application) show a stepper/progress indicator at the top and persist
   partial progress (draft autosave) so a parent can resume later — see `ERP/PPDB.md`.

## Field Types Reference

| Field type | Component | Notes |
|---|---|---|
| Short text | `EduOctoTextField` | |
| Long text | `EduOctoTextField(multiline = true)` | Auto-growing up to 6 lines, then scrolls |
| Currency (IDR) | `EduOctoCurrencyField` | Auto-formats with `Rp` + thousands separators as typed |
| Date | `EduOctoDateField` | Opens platform-appropriate date picker; never a free-text date field |
| Select (few options) | `EduOctoSegmentedControl` or radio group | ≤ 4 options |
| Select (many options) | `EduOctoDropdown` / searchable sheet | > 4 options |
| File/photo upload | `EduOctoUploadField` | Shows thumbnail/filename + progress + remove action |
| Phone number | `EduOctoPhoneField` | Indonesian format validation (+62 / 0 prefix handling) |

## Accessibility

Every field has a programmatically associated label (`semantics { contentDescription = ... }` where
visual label isn't sufficient), and error messages are announced to screen readers immediately when
they appear (`liveRegion` semantics). See `26-ACCESSIBILITY.md`.

## Anti-Patterns

- Disabling the submit button until the form is "valid" (hides *why* it's invalid from the user).
- Clearing a field's value on a validation error.
- Using a generic native alert/dialog for validation errors instead of inline field errors.
