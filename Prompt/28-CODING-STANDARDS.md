# 28 — Coding Standards

## Kotlin Style

- Follow the official Kotlin coding conventions as the baseline; this document only lists EduOcto
  additions/deviations.
- 4-space indentation, max line length 120.
- `ktlint`/`detekt` (or equivalent) run in CI; a feature is not complete if it introduces new lint
  violations.

## Naming

| Element | Convention | Example |
|---|---|---|
| Classes/Interfaces | `PascalCase`, noun phrases | `StudentRepository`, `AppError` |
| Functions | `camelCase`, verb phrases | `calculateOutstandingSpp()` |
| Composables | `PascalCase` (Compose convention) | `StudentCard`, `EduOctoButton` |
| Properties/vals | `camelCase` | `outstandingBalance` |
| Constants | `UPPER_SNAKE_CASE` in a `companion object` or top-level `const val` | `MAX_UPLOAD_SIZE_MB` |
| Packages | `lowercase`, feature-first (see `23-CLEAN-ARCHITECTURE.md`) | `com.eduocto.feature.attendance.domain` |
| Sealed interfaces for state/events | suffix `UiState`/`Intent`/`Effect`/`Error` | `StudentListUiState` |
| Boolean properties | `isX`/`hasX`/`canX` | `isLoading`, `hasOverdueInvoice` |

## File Organization

- One top-level public class/interface per file, file name matches the type name.
- Extension functions grouped in a clearly named `*Ext.kt` file scoped to the type they extend
  (`StudentMapperExt.kt`), not a catch-all `Utils.kt`.
- Composable files for a single screen are organized as: `StudentListScreen.kt` (wiring),
  `StudentListContent.kt` (pure UI), `StudentListUiState.kt` (state/intent/effect), co-located in the
  same `presentation` package.

## Comments & Documentation

- KDoc on every public API in `domain` and `data` layers explaining *purpose* and any non-obvious
  business rule — not restating the signature.
- Inline comments explain **why**, never **what** ("// SPP is prorated for mid-month enrollment per
  school finance policy," not "// loop through invoices").
- No commented-out code committed — delete it; version control preserves history.

## Null Safety & Defensive Coding

- Avoid `!!` outside of tests; if a non-null is truly guaranteed, express it in the type system
  (don't model as nullable in the first place) rather than asserting at runtime.
- Public API functions validate their preconditions explicitly (`require`/`check`) with a clear
  message rather than failing later with an obscure `NullPointerException`/`IndexOutOfBounds`.

## Formatting Numbers, Currency, Dates (cross-cutting — used everywhere)

```kotlin
fun formatCurrencyIdr(amount: Long): String = // 1500000 -> "Rp1.500.000"
fun formatDateLong(date: LocalDate): String = // -> "27 Juni 2026"
fun formatDateShort(date: LocalDate): String = // -> "27/06/2026"
```

Defined once in `core/common/Formatting.kt`, used everywhere — never ad hoc `"Rp$amount"` string
concatenation anywhere in feature code.

## Git & Review

- Conventional commit messages: `feat(attendance): add bulk mark-present action`,
  `fix(payment): correct SPP proration for mid-month enrollment`.
- A PR/change is scoped to one feature/fix; unrelated formatting-only changes are a separate commit.
- Every PR description states: what changed, why, which docs in this kit were consulted/updated, and
  which checklist(s) were run.

## Anti-Patterns

- Abbreviated/unclear names (`stu`, `pmt`, `mgr`) — full words, always.
- Giant functions doing fetch + transform + format + render-adjacent logic in one block — split by
  responsibility per `23-CLEAN-ARCHITECTURE.md`.
- Magic numbers/strings without a named constant (`if (status == "1")` — use an enum/sealed type).
