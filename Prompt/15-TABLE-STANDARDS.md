# 15 — Table / Data Grid Standards

## When to Use a Table vs. a List

Use `EduOctoDataTable` (fintech-style grid) instead of `EduOctoListItem` lists when:
- Multiple comparable numeric/status columns exist per row (e.g. Payroll: Name, Base Salary,
  Deductions, Net Pay, Status).
- Users need to scan/compare values vertically down a column.
- The data is primarily consumed on tablet/desktop (Finance Staff, Principal back-office views).

On mobile/compact width, a table with >3 columns collapses into a card-per-row layout (each column
becomes a labeled line within the card) rather than horizontal-scrolling a dense grid — horizontal
scroll tables are a last resort, used only when collapsing would destroy necessary comparison (rare).

## Anatomy

```
┌──────────────────────────────────────────────┐
│ Header row (sticky, sortable columns)        │
├──────────────────────────────────────────────┤
│ Row (zebra-free; hairline divider only)      │
│ Row (hover/focus state on desktop)           │
│ ...                                          │
├──────────────────────────────────────────────┤
│ Footer: pagination / totals row (optional)   │
└──────────────────────────────────────────────┘
```

## Rules

1. **No zebra striping.** EduOcto tables separate rows with a single hairline (`outline` token), not
   alternating background colors — keeps the "calm/premium" target rather than "spreadsheet" feel.
2. **Numeric columns are right-aligned with tabular figures**; text columns left-aligned; status
   columns center the `StatusPill`.
3. **Sticky header** on scroll for any table taller than one viewport.
4. **Sortable columns** show a subtle chevron indicator only on hover/focus (desktop) or a persistent
   small icon (touch) — clicking/tapping a header toggles asc/desc.
5. **Row click** opens detail (e.g. clicking a payroll row opens that staff member's payroll detail)
   — the entire row is the tap target, not just a single cell.
6. **Totals/summary row**, when present, is visually distinct (slightly elevated background, bold
   numerals) and pinned to the bottom of the visible table, not just the end of the data.
7. **Column widths are content-aware but capped** — a "Name" column never grows to consume 80% of
   the table at the expense of numeric columns becoming cramped.

## Status Representation in Tables

Status always renders as a `StatusPill` component (background = semantic color at ~12% opacity,
text = full-strength semantic color, `labelSmall` weight) — never as plain colored text with no pill
container, and never as an emoji.

## Empty / Loading / Error

- Loading: skeleton rows (same column structure, shimmer blocks) — see `18-LOADING-STATES.md`.
- Empty: a full-width empty state replaces the table body, header row may remain visible if filters
  are active (so the user can see what they're filtering by) — see `17-EMPTY-STATES.md`.
- Error: inline retry banner replaces table body — see `19-ERROR-STATES.md`.

## Anti-Patterns

- Tables with more than ~7 visible columns on desktop — group secondary data into an expandable row
  or a detail view instead.
- Using a table for fewer than ~4 rows of fewer than 3 columns — that's just a list/card.
- Mixing currency formats or omitting the `Rp` prefix inconsistently within the same column.
