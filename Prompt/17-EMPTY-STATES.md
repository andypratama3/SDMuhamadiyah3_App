# 17 — Empty States

## Principle

An empty state is not "nothing to show" — it is an opportunity to answer "what should I do next?"
(`02-DESIGN-PHILOSOPHY.md`). Every list, table, dashboard section, and search result must define an
explicit empty state; rendering literally nothing is never acceptable.

## Anatomy

```
┌───────────────────────────┐
│        [Illustration/Icon]│   — simple line icon, brand-tinted, not a generic "empty box" stock asset
│        Title (titleMedium)│   — what's empty, in plain language
│   Description (bodySmall, │   — why it's empty / reassurance
│      onSurfaceMuted)      │
│      [Primary Action]     │   — the one thing the user can do about it (when one exists)
└───────────────────────────┘
```

## Tone by Context

| Context | Tone | Example Title |
|---|---|---|
| Genuinely empty, action available | Inviting | "Belum ada siswa terdaftar" + CTA "Tambah Siswa" |
| Empty because filtered | Neutral, actionable | "Tidak ada hasil untuk filter ini" + CTA "Reset Filter" |
| Empty and *good news* (e.g. no overdue payments) | Positive/reassuring | "Semua pembayaran lunas ✓" (no CTA needed) |
| Empty due to permissions (nothing assigned yet) | Neutral, informative | "Belum ada kelas yang ditugaskan kepada Anda" |
| Search with no query yet | Prompt | "Cari siswa berdasarkan nama atau NISN" |

## Rules

1. Never show a "good news" empty state (e.g. zero overdue payments) with the same visual weight as
   a "needs action" empty state — use `success` color accents for positive-empty, neutral
   `onSurfaceMuted` for action-needed.
2. Always provide a CTA when one exists; never show a bare message when there's an obvious next
   action available to that role.
3. Illustrations are simple, line-based, brand-colored (Navy/Gold on transparent) — never a generic
   third-party stock illustration; keep file sizes small (SVG/vector, not bitmap).
4. Empty states inside a table preserve the header row (and active filter chips) so the user
   understands *why* it's empty without losing their place.

## Component

```kotlin
@Composable
fun EduOctoEmptyState(
    title: String,
    description: String? = null,
    icon: ImageVector,
    tone: EmptyStateTone = EmptyStateTone.Neutral,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
)

enum class EmptyStateTone { Neutral, Positive, Informative }
```

## Anti-Patterns

- A blank white screen with no message when a query returns zero results.
- Reusing one generic "No data" empty state across every screen regardless of context.
- Empty states that block the user with no way back/forward (always reachable via normal nav).
