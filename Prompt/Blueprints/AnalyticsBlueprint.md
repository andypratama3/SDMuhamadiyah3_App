# Analytics Blueprint

Blueprint for the role-scoped Analytics Dashboard, implementing `ERP/Analytics.md`.

## Composable Structure

```
AnalyticsDashboardScreen
└── AnalyticsDashboardContent(state: AnalyticsUiState)
    ├── EduOctoTopBar(title = "Analitik", periodFilter)
    ├── MetricGrid                  (StatTile per visible metric, role-scoped per AnalyticsDashboardConfig)
    └── MetricDetailSections        (one chart block per key metric)
          ├── ChartHeader(label, comparisonBasisLabel — explicit, e.g. "vs. bulan sebelumnya")
          └── LineChart/Sparkline(series)
```

## Metric Detail / Drill-down

```
MetricDetailScreen
└── MetricDetailContent(state: MetricDetailUiState)
    ├── EduOctoTopBar(back, metricLabel, exportAction if permitted)
    ├── FullTimeSeriesChart
    ├── ComparisonToggle           (vs. previous period / vs. same period last year)
    └── DrillDownTable             (EduOctoDataTable, by class/grade — only if role permits drill-down)
```

## State Shape

```kotlin
data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val visibleMetrics: ImmutableList<AnalyticsMetricUiModel> = persistentListOf(),
    val error: AppError? = null,
)
data class AnalyticsMetricUiModel(val id: String, val label: String, val currentValue: String, val trend: Trend, val unit: MetricUnit)

data class MetricDetailUiState(
    val isLoading: Boolean = false,
    val metric: AnalyticsMetric? = null,
    val comparisonBasis: ComparisonBasis = ComparisonBasis.PreviousPeriod,
    val drillDownRows: ImmutableList<DrillDownRow> = persistentListOf(),
    val canDrillDown: Boolean = false,
    val error: AppError? = null,
)
enum class ComparisonBasis { PreviousPeriod, SamePeriodLastYear }
```

## Business Rule Hooks (per `ERP/Analytics.md`)

- `visibleMetrics` is resolved server-side/UseCase-side from `AnalyticsDashboardConfig` for the
  current role — the Composable never decides which metrics to show based on role itself.
- `canDrillDown` gates whether `DrillDownTable`/student-level navigation is rendered at all, per the
  Permission Matrix (e.g. Finance Staff sees financial drill-down only, not academic).

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Metric Grid | Skeleton tiles | N/A (always shows at least zero-state values) | Per-tile error icon |
| Chart sections | Skeleton chart block | "Belum ada data untuk periode ini" | Inline retry |
| Drill-down table | Skeleton rows | EmptyState neutral | EduOctoErrorState replacing table |

## Permission Notes

Enforced per `ERP/Analytics.md` matrix — most critically, financial metrics are never shown to
Teacher/Homeroom roles, and academic-record drill-down is never shown to Finance Staff.
