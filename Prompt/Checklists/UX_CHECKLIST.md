# UX Checklist

Run this for any new/changed user flow (not just a single screen).

## Flow Clarity
- [ ] Every step in a multi-step flow shows progress (stepper/indicator), per `13-FORM-STANDARDS.md`
- [ ] User can always go back without losing entered data
- [ ] No dead ends — every screen has a clear path forward or back

## Forms
- [ ] Validation is on-blur + on-submit, not on every keystroke (`13-FORM-STANDARDS.md`)
- [ ] Error messages are specific and actionable, not generic
- [ ] Submit button stays enabled; first invalid field is focused on failed submit attempt
- [ ] Entered data is preserved on submission failure

## Feedback
- [ ] Every user-triggered action has visible feedback within 100ms (loading indicator, haptic, or
      immediate visual change)
- [ ] Success is confirmed explicitly (banner/toast/navigation), never silent
- [ ] Destructive actions require explicit confirmation (`16-DIALOG-STANDARDS.md`)
- [ ] Financial/irreversible destructive actions require typed confirmation (`27-SECURITY.md`)

## Role Awareness
- [ ] Flow behaves correctly for every role permitted to access it (per the relevant
      `ERP/*.md` permission matrix) — not just the role primarily designed for
- [ ] Unauthorized access attempts show a clear permission-denied state, not a crash or blank screen

## Copy
- [ ] All user-facing text is localizable (no hardcoded strings) and in the correct default locale
      (`id` per `01-PROJECT-CONTEXT.md`)
- [ ] Tone matches `02-DESIGN-PHILOSOPHY.md` (formal-but-warm, direct, no jargon)

## Edge Cases
- [ ] Behavior verified for: first-time use (no data yet), offline, slow network, and
      maximum-realistic data volume (e.g. a class of 40 students, a year of attendance history)
