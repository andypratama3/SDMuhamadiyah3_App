# 26 — Accessibility (WCAG AA)

## Baseline Commitment

EduOcto targets **WCAG 2.1 AA** equivalence across Android, iOS, and Desktop. This is not optional
for any screen reachable in production — including back-office/desktop-only screens (Finance/Admin
staff with visual impairments are real users this system must support).

## Contrast

- Body text vs. background: minimum 4.5:1 — verify every text/surface pairing introduced in
  `04-COLOR-SYSTEM.md` and any new component combination, not just the defaults.
- Large text (≥18sp regular or ≥14sp bold) and icons conveying meaning: minimum 3:1.
- Never convey status by color alone — every `StatusPill` (per `04-COLOR-SYSTEM.md`) includes a text
  label, not just a colored dot.

## Semantics

- Every interactive element has a meaningful accessible label. Icon-only buttons set
  `Modifier.semantics { contentDescription = "..." }` with an action-oriented description ("Tandai
  hadir," not "Icon button").
- Decorative-only images/icons set `contentDescription = null` explicitly (so screen readers skip
  them) rather than leaving it ambiguous.
- Headings use proper semantic roles (`heading()` semantics modifier) so screen reader users can
  navigate by heading, matching the visual hierarchy in `05-TYPOGRAPHY.md`.
- Live-updating content (form errors, snackbars, "what changed" indicators) uses
  `liveRegion = LiveRegionMode.Polite` (or `Assertive` for errors) so changes are announced without
  the user needing to re-discover focus.

## Touch Targets & Input

- Minimum 48x48dp touch target for every interactive element, regardless of visual size — verified
  per component in `20-COMPONENT-LIBRARY.md`.
- Full keyboard navigation support on Desktop: tab order follows visual/logical reading order; every
  action reachable by mouse is reachable by keyboard (Enter/Space to activate, Esc to dismiss
  dialogs/sheets).
- Focus indicators are always visible on Desktop (focus ring using `info`/`primary` token outline)
  — never suppressed for aesthetic reasons.

## Dynamic Type / Text Scaling

- Respect system font scaling up to at least 130% without text truncation/clipping or overlapping
  elements — test every screen at the largest supported scale as part of `Checklists/UI_CHECKLIST.md`.
- Layouts use `weight`/intrinsic sizing rather than fixed heights for text-containing containers so
  they can grow with scaled text.

## Motion & Reduced Motion

- Respect OS-level "reduce motion" setting per `09-ANIMATION.md` — replace slide/scale/stagger
  entrances with simple fades; never remove the state-change signal entirely.

## Screen Reader Flows (TalkBack / VoiceOver)

- Every screen has a logical reading order verified manually with TalkBack (Android) and VoiceOver
  (iOS) before being marked complete for any feature touching Finance, PPDB, or Attendance (the
  highest-stakes flows).
- Custom gestures (swipe-to-action in `14-LIST-STANDARDS.md`) always have a non-gesture equivalent
  exposed via accessibility actions (`semantics { customActions = listOf(...) }`).

## Forms

- Every field's error state is announced immediately (`liveRegion`) per `13-FORM-STANDARDS.md`.
- Required fields are exposed via `stateDescription`/equivalent semantics, not just a visual
  asterisk.

## Verification Procedure (per feature, before merge)

1. Run an automated contrast/semantics audit (Android Accessibility Scanner equivalent, or
   Compose's accessibility testing APIs) — zero new critical issues.
2. Manually navigate the new/changed screen with TalkBack or VoiceOver end-to-end.
3. Manually navigate with keyboard-only on Desktop.
4. Test at 130% system font scale.
5. Confirm against `Checklists/UI_CHECKLIST.md`.

## Anti-Patterns

- Disabling system font scaling "to protect the design."
- Icon-only navigation items with no accessible label.
- Color-only status indication anywhere in the app.
