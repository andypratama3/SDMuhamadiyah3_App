# ERP — Analytics Module

## Purpose

Provides aggregate, trend, and comparative views over data from other modules for
Principal/VP/Finance Staff strategic oversight — distinct from per-module reports (e.g.
`ERP/Finance.md` financial reports), Analytics specifically covers cross-module and trend-over-time
views.

## Domain Model

```kotlin
data class AnalyticsMetric(
    val id: String,
    val label: String,
    val series: List<MetricPoint>,     // time series
    val unit: MetricUnit,
)
data class MetricPoint(val period: String, val value: Double)
enum class MetricUnit { Percent, Currency, Count }

data class AnalyticsDashboardConfig(
    val role: UserRole,
    val visibleMetrics: List<String>,  // AnalyticsMetric ids
)
```

## Standard Metrics

| Metric | Source Module | Granularity |
|---|---|---|
| School-wide attendance rate trend | Attendance | Monthly, by grade level |
| SPP collection rate trend | Finance/Payment | Monthly |
| Outstanding balance by grade level | Finance | Snapshot, current |
| PPDB funnel (started → submitted → accepted → enrolled) | PPDB | Per academic year intake |
| Average grade trend by subject | ReportCard | Per term |
| Chronic absence count trend | Attendance | Monthly |

## Business Rules

1. **Analytics is read-only and derived** — it never writes back to source modules; it aggregates
   via scheduled/on-demand computation jobs reading the same SQLDelight-backed local cache (or a
   dedicated analytics endpoint) as other modules.
2. **Sensitive drill-down is permission-gated**: a Principal can drill from a school-wide trend down
   to a specific class/student, but the *same* drill-down capability is not available to, e.g.,
   Finance Staff viewing academic metrics — Finance Staff's analytics scope is limited to financial
   metrics per the Permission Matrix below.
3. **Trend comparisons always show the comparison basis explicitly** (e.g. "vs. bulan sebelumnya," "vs.
   periode yang sama tahun lalu") — never an unlabeled arrow/percentage with ambiguous meaning.

## Screens

- Analytics Dashboard (role-scoped metric set per `AnalyticsDashboardConfig`) — chart-forward,
  using `Sparkline`/chart components from `20-COMPONENT-LIBRARY.md`.
- Metric Detail / Drill-down — full time series, exportable, filterable by class/grade where
  permitted.

## Permission Matrix

| Action | Principal/VP | Finance Staff | Admin | Teacher/Homeroom | Other roles |
|---|---|---|---|---|---|
| View school-wide academic analytics | ✅ | ❌ | ✅ (operational, non-financial) | ❌ | ❌ |
| View school-wide financial analytics | ✅ | ✅ | ❌ | ❌ | ❌ |
| View own-class analytics | ➖ | ➖ | ➖ | ✅ | ❌ |
| Drill down to individual student | ✅ | ✅ (financial only) | ➖ | ✅ (own class) | ❌ |

## Audit Requirements

View access to Analytics is not individually audited (read-only, non-destructive), but exports
(CSV/PDF download of sensitive aggregates) are logged per `27-SECURITY.md` data-handling baseline.
