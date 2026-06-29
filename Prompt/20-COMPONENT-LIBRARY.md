# 20 — Component Library (Canonical Inventory)

This is the exhaustive list of shared components that must exist in
`shared/src/commonMain/kotlin/com/eduocto/designsystem/components/`. Before creating a new component,
check this list — extend an existing entry before adding a new one. Every component must implement
all "Required States" listed.

## Primitives

| Component | Required States | Notes |
|---|---|---|
| `EduOctoButton` | default, pressed, disabled, loading | Variants: Primary (navy fill), Secondary (outline), Tertiary (text-only), Destructive (danger fill) |
| `EduOctoIconButton` | default, pressed, disabled, selected | Min 48dp touch target always |
| `EduOctoTextField` | default, focused, error, disabled, with leading/trailing icon | See `13-FORM-STANDARDS.md` |
| `EduOctoCurrencyField` | same as TextField | Auto-formats IDR |
| `EduOctoDateField` | default, error, disabled | Opens platform date picker |
| `EduOctoPhoneField` | default, error | Indonesian phone validation |
| `EduOctoCheckbox` / `EduOctoSwitch` | checked, unchecked, disabled, indeterminate (checkbox only) | |
| `EduOctoChip` | default, selected, disabled | Filter chip + input chip variants |
| `EduOctoBadge` | default | Numeric/dot badge for notification counts |
| `EduOctoAvatar` | image, initials-fallback, loading | Always falls back to initials on image load failure |
| `EduOctoSegmentedControl` | default, disabled | ≤4 segments |
| `EduOctoDropdown` | default, open, error, disabled | |

## Layout

| Component | Required States | Notes |
|---|---|---|
| `EduOctoCard` | default, pressed (if clickable), disabled | Flat elevation L1 |
| `GlassSurface` | default | See `08-GLASSMORPHISM.md` |
| `EduOctoSection` | with/without header action | Wraps a titled content block |
| `EduOctoListItem` | default, pressed, disabled | See `07-COMPONENT-SYSTEM.md` |
| `EduOctoScreenScaffold` | — | TopBar + content + optional FAB/bottom bar slot, handles insets |
| `EduOctoDivider` | — | Hairline, `outline` token |

## Data Display

| Component | Required States | Notes |
|---|---|---|
| `StatTile` | default, loading (skeleton) | Label + value + optional trend indicator (▲/▼ + %) |
| `StatusPill` | one per semantic status | See `04-COLOR-SYSTEM.md` mapping |
| `EduOctoDataTable` | loading, empty, error, populated | See `15-TABLE-STANDARDS.md` |
| `ProgressRing` | determinate, indeterminate | Used for attendance %, payment collection % |
| `Sparkline` | populated, empty | Lightweight trend line, no axis labels |
| `EduOctoSkeleton` | shimmer | See `18-LOADING-STATES.md` |

## Feedback

| Component | Required States | Notes |
|---|---|---|
| `EduOctoSnackbar` | info, success, warning, error | Auto-dismiss 4s, swipeable |
| `EduOctoDialog` | — | See `16-DIALOG-STANDARDS.md` |
| `EduOctoBottomSheet` | — | See `16-DIALOG-STANDARDS.md` |
| `EduOctoEmptyState` | neutral, positive, informative | See `17-EMPTY-STATES.md` |
| `EduOctoErrorState` | with/without secondary action | See `19-ERROR-STATES.md` |

## Navigation

| Component | Required States | Notes |
|---|---|---|
| `EduOctoTopBar` | default, with search, with back | Title + optional actions slot |
| `EduOctoBottomNavBar` | per-tab selected/unselected, with badge | See `11-NAVIGATION.md` |
| `EduOctoNavRail` | per-tab selected/unselected | Medium/Expanded width classes |
| `EduOctoTabRow` | scrollable, fixed | |
| `EduOctoBreadcrumb` | — | Desktop/expanded only |

## Domain-Specific (compose from primitives above — never bypass the design system)

| Component | Composed From | Used In |
|---|---|---|
| `StudentRow` | `EduOctoListItem`, `EduOctoAvatar`, `StatusPill` | `ERP/Student.md` |
| `AttendanceRow` | `EduOctoListItem`, `StatusPill`, swipe actions | `ERP/Attendance.md` |
| `PaymentRow` | `EduOctoListItem`, `StatusPill`, currency display | `ERP/Payment.md`, `ERP/Finance.md` |
| `ReportCardGradeRow` | `EduOctoDataTable` row | `ERP/ReportCard.md` |
| `AnnouncementCard` | `EduOctoCard`, `EduOctoBadge` | `ERP/Announcement.md` |
| `PPDBStatusTracker` | `ProgressRing`, stepper primitives | `ERP/PPDB.md` |

## Rule for Adding a New Component

A new component may be added only when:
1. No existing component in this table can be composed/extended to satisfy the need, **and**
2. It is added to this table in the same change that introduces it, with its Required States
   defined, **and**
3. It follows the anatomy rules in `07-COMPONENT-SYSTEM.md`.
