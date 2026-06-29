# 14 — List Standards

## Default List Item Anatomy

Use `EduOctoListItem` (see `07-COMPONENT-SYSTEM.md`) as the base for every list across the app —
student rosters, payment history, announcements, notifications. Domain-specific rows (e.g.
`PaymentRow`, `StudentRow`) wrap it rather than reimplementing layout.

## Lazy Lists & Performance

1. Always use `LazyColumn`/`LazyVerticalGrid` for any list that may exceed ~15 items — never a
   `Column` with `forEach` for data-driven lists. See `25-PERFORMANCE.md`.
2. Always provide stable `key = { item.id }` in `items()` — never rely on index as key for mutable
   lists.
3. Heavy row content (avatars/images) uses `Modifier.size` fixed dimensions to avoid layout jumps,
   and image loading shows a placeholder (shimmer, per `18-LOADING-STATES.md`) — never a layout
   collapse-then-expand when an image loads.

## Pagination

1. Server-backed lists (student roster, payment history, audit logs) paginate — default page size
   20, loaded via cursor/offset depending on backend contract — never fetch-all-then-display for any
   list that could exceed a few hundred rows.
2. Use a `LoadState`-style sealed model (`Loading`, `LoadingMore`, `Error`, `EndReached`) surfaced as
   a footer item in the `LazyColumn`, not a separate overlay.
3. Pull-to-refresh is available on every primary list screen; refresh resets pagination from page 1.

## Filtering & Sorting

1. Lists with > 20 typical items expose a filter/search affordance in the top bar — never bury
   filtering inside a settings menu.
2. Default sort order is always the most operationally useful one for the role (e.g. Payments sorted
   by due date ascending for Finance Staff, not by creation date).
3. Active filters are shown as removable chips below the search/filter bar, never hidden state the
   user has to remember they applied.

## Selection & Bulk Actions

1. Long-press (mobile) or a dedicated "Select" mode toggle (tablet/desktop) enters multi-select.
2. A contextual action bar replaces the top bar while in selection mode, showing count selected and
   available bulk actions (e.g. "Mark 12 as Present").
3. Bulk destructive actions always confirm via `16-DIALOG-STANDARDS.md` before executing.

## Swipe Actions (mobile)

- Swipe-right: primary positive action (e.g. mark attendance present, mark paid) — `success` tint.
- Swipe-left: secondary/destructive action (e.g. flag absent, archive) — `warning`/`danger` tint.
- Swipe actions always have an equivalent menu-based action for accessibility/desktop parity — never
  swipe-only functionality.

## Anti-Patterns

- Infinite scroll with no end-of-list indicator (`EndReached` state must show something — a subtle
  "You've reached the end" or simply stop showing a loading footer, never an endless spinner).
- Mixing item types in a single list without a clear `EduOctoListItem` variant per type (sections
  should use distinct header rows, not ambiguous visual grouping).
