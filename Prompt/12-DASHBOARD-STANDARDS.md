# 12 — Dashboard Standards

## Purpose

Every role in EduOcto lands on a dashboard. The dashboard is the single most important screen in the
app — it sets the tone for "Academic Intelligence" and must answer all five questions from
`02-DESIGN-PHILOSOPHY.md` within the first viewport, with no scrolling required for the most critical
information.

## Canonical Dashboard Structure

```
┌─────────────────────────────────────────┐
│ Header: greeting + role + date          │  ("Where am I?")
├─────────────────────────────────────────┤
│ Hero stat / primary KPI (glass card)    │  ("What is important?")
├─────────────────────────────────────────┤
│ Secondary KPI row (2–4 StatTiles)       │
├─────────────────────────────────────────┤
│ Actionable items section                │  ("What can I do? / What's next?")
│ (pending approvals, overdue payments,   │
│  today's classes, unread announcements) │
├─────────────────────────────────────────┤
│ Recent activity / changes feed          │  ("What changed?")
└─────────────────────────────────────────┘
```

## Rules by Section

1. **Header** — always shows the user's name, role badge, and current date (Bahasa Indonesia long
   format). Avoid a generic "Welcome back" with no context.
2. **Hero stat** — exactly one. The single number that matters most for that role *today*:
   - Principal: school-wide attendance % today, or total SPP collection this month.
   - Finance Staff: total outstanding SPP this month.
   - Teacher: today's class attendance summary.
   - Parent: child's outstanding balance, or "no balance due" success state.
   Rendered as a `GlassSurface` hero card per `08-GLASSMORPHISM.md`, using `displayLarge`/`displayMedium`.
3. **Secondary KPI row** — 2 to 4 `StatTile` components max. Never more than 4 — beyond that,
   route to an Analytics/Reports screen instead of cramming the dashboard.
4. **Actionable items** — a list of items requiring *this user's* action, each with a clear CTA
   (e.g. "3 leave requests pending approval" → tap → Approvals screen filtered). Empty state here
   uses `17-EMPTY-STATES.md` with a positive tone ("All caught up").
5. **Recent activity feed** — last 5–10 relevant events, each with a timestamp (`metadata` style)
   and a clear subject ("SPP payment received from Budi Santoso — 2 hours ago").

## Per-Role Hero Content (see `ERP/Dashboard.md` for full spec)

| Role | Hero Stat | Top Actionable Item |
|---|---|---|
| Principal | School-wide attendance % | Pending approvals count |
| Vice Principal | Same as Principal, scoped to assigned grade levels | Pending approvals count |
| Teacher | Today's class attendance summary | Ungraded assignments / unfilled attendance |
| Homeroom Teacher | Homeroom class attendance % | Students needing follow-up (absences, grades) |
| Admin Staff | New PPDB applications this week | Documents pending verification |
| Finance Staff | Total SPP outstanding this month | Overdue invoices count |
| Student | Today's schedule | Upcoming assignment/exam |
| Parent | Child's outstanding balance | Unread message from teacher |
| Guest | PPDB intake status/dates | Continue my application |

## Loading & Empty Behavior

- Dashboard never blocks on a single slow query. Each section (hero, KPIs, actionable, feed) loads
  and renders independently with its own skeleton (`18-LOADING-STATES.md`) — a slow "recent activity"
  query must never delay the hero stat from appearing.
- First-ever login (no data yet, e.g. brand-new student account) shows a tailored empty state per
  section, never a blank screen.

## Anti-Patterns

- A dashboard that is just a grid of equally-weighted cards with no hero/hierarchy.
- Showing the same dashboard layout to every role with content swapped — the *structure* may be
  shared (this document), but hero selection and actionable items must be genuinely role-specific.
- Auto-refreshing the entire dashboard on a timer in a way that resets scroll position or causes
  layout jank — use targeted, section-level refresh.
