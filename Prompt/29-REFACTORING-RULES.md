# 29 — Refactoring Rules

## When to Refactor

Refactor when, and only when, one of these is true:
1. The change you're making requires duplicating logic that already exists elsewhere (extract and
   share instead of copy-pasting — DRY).
2. A file/function has grown past the point where its responsibility is singular and obvious
   (violates SOLID's Single Responsibility) — e.g. a ViewModel that fetches data, formats currency,
   and contains navigation logic all in one 400-line class.
3. A pattern in this kit has changed (e.g. a new shared component supersedes three ad hoc
   implementations) — existing call sites are migrated, not left divergent.
4. Performance profiling (`25-PERFORMANCE.md`) identifies a concrete hotspot.

Do **not** refactor speculatively "because it might be needed later" (YAGNI) — every refactor must
be justified by a current, concrete reason from the list above.

## How to Refactor Safely

1. **Never refactor and add a feature in the same change.** Land the refactor first (behavior
   identical, verified), then build the new feature on top of the cleaner structure.
2. **Tests before refactor.** If the code being touched lacks unit test coverage for its current
   behavior, add characterization tests first so the refactor can be verified to be behavior-
   preserving.
3. **Refactor in the smallest safe increments.** Prefer a sequence of small, independently
   verifiable changes over one large rewrite — especially for `domain`/`data` layers touching
   Finance or Attendance logic.
4. **Update this kit in the same change** if the refactor changes an established pattern (e.g.
   moving from one `UiState` shape to another should update `24-STATE-MANAGEMENT.md` examples if the
   canonical pattern itself changed, not just the one screen).

## Common Refactor Targets in This Codebase

| Symptom | Refactor |
|---|---|
| Same formatting/validation logic duplicated across 2+ features | Extract to `core/common` |
| A ViewModel with more than ~5 distinct responsibilities | Split into UseCases (`23-CLEAN-ARCHITECTURE.md`) |
| A Composable file exceeding ~300 lines | Extract sub-Composables, each independently previewable |
| Repeated raw Material 3 widget usage instead of an `EduOcto*` component | Migrate to the shared component, update call sites |
| A feature directly importing another feature's internal classes | Introduce a shared `domain` model or navigate by ID instead |

## What Refactoring Must Never Change Silently

- Public API contracts consumed by other modules (repository interfaces, navigation destinations) —
  changing these is a deliberate, called-out architectural change, not an incidental refactor side
  effect.
- Business rule outcomes (e.g. SPP proration math) — a refactor that accidentally changes a financial
  calculation's result is a bug, not a refactor, and must be treated with the same rigor as any other
  Finance-affecting change (review, tests, audit trail awareness per `27-SECURITY.md`).

## Definition of Done for a Refactor

- All existing tests pass unchanged in assertions (behavior-preserving).
- New tests added if coverage gaps were found.
- No new lint/style violations (`28-CODING-STANDARDS.md`).
- This kit's relevant document updated if the refactor changes a documented pattern.
