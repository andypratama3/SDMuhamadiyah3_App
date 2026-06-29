# 02 — Design Philosophy

## Theme: Academic Intelligence

EduOcto's design language is called **Academic Intelligence** — the intersection of institutional
trust (a school manages families' money and children's records) and modern software craft (it should
feel as considered as a fintech dashboard).

## Visual Keywords (the filter every design decision passes through)

Elegant · Professional · Calm · Premium · Academic · Modern · Luxury · Readable · Trustworthy ·
Sophisticated · Minimal · Enterprise.

If a screen feels "generic admin panel," it has failed this filter. If it feels "loud consumer app,"
it has also failed — EduOcto is calm, not flashy.

## Reference Quality, Not Reference Copying

Apple HIG, Material Design 3, Linear, Stripe Dashboard, Notion, Arc, Vercel, Raycast, Figma, JetBrains
IDEs are studied for **product quality bar and craft**, never copied as templates. Concretely:

- From **Stripe Dashboard**: dense financial data presented without feeling cluttered; numeric
  alignment; calm use of color to mean something (status, not decoration).
- From **Linear**: keyboard-first speed, minimal chrome, confident typography, subtle motion.
- From **Notion**: comfortable information density, clear grouping, generous whitespace.
- From **Apple HIG / Material 3**: platform-appropriate touch targets, native-feeling navigation.

EduOcto's own visual identity (Deep Navy + Academic Gold, see `04-COLOR-SYSTEM.md`) must remain
distinct from all of these — it should never be mistaken for a clone of any single reference.

## Every Screen Answers Five Questions

A screen is not finished — regardless of how "complete" the UI looks — until it visibly answers:

1. **Where am I?** — clear screen title, breadcrumb, or section context.
2. **What can I do?** — primary action(s) are obvious within 2 seconds of looking.
3. **What changed?** — state changes (new data, updated status) are perceptible, not silent.
4. **What is important?** — visual hierarchy surfaces the one or two things that matter most first.
5. **What should I do next?** — there is always a clear next step, even on a dead-end/empty screen.

## Cognitive Load Reduction

- One primary action per screen. Secondary actions are visually subordinate.
- Progressive disclosure: advanced options are hidden until requested, never displayed by default.
- Group related data; never present an unstructured wall of fields or numbers.
- Default to showing *less* data with a clear path to *more*, not all data at once.

## Tone of Voice (copy/microcopy)

- Direct and respectful — this is software for a school, used by staff, teachers, and parents who
  are not necessarily technical.
- Bahasa Indonesia copy is formal-but-warm ("Anda", not "kamu", except in clearly casual contexts
  like a student-facing motivational message).
- Error messages explain what happened and what to do next — never just "Error" or a raw exception.

## What "Premium" Means Here (and what it does not)

Premium means: restraint, precision, consistency, and craft in the details (spacing, motion,
typography rhythm). It does **not** mean: heavy decoration, gratuitous animation, or visual noise.
Glassmorphism (`08-GLASSMORPHISM.md`) is used to create depth and hierarchy — not as a gimmick
slapped onto every surface.

## Anti-Patterns (explicitly rejected)

- Generic Bootstrap/Material-default look with no custom tokens.
- Dashboards that are just a grid of identical cards with no hierarchy.
- Color used decoratively rather than semantically (e.g. random accent colors with no meaning).
- Modal-itis — using a dialog for things that should be an inline state or a dedicated screen.
- Infinite spinners with no skeleton or progress indication (`18-LOADING-STATES.md`).
