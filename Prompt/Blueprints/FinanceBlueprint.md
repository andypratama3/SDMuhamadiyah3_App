# Finance Blueprint

Blueprint for the Financial Reports screen (Finance Staff/Principal), implementing
`ERP/Finance.md` reporting requirements and `15-TABLE-STANDARDS.md`.

## Composable Structure

```
FinanceReportsScreen
└── FinanceReportsContent(state: FinanceReportsUiState)
    ├── EduOctoTopBar(title = "Laporan Keuangan", actions = [export, dateRangeFilter])
    ├── SummaryStatsRow
    │     ├── StatTile("Terkumpul Bulan Ini", amount, trend)
    │     ├── StatTile("Tertunggak", amount, tone = Warning/Danger)
    │     └── StatTile("Tingkat Penagihan", percent)
    ├── CollectionTrendChart       (Sparkline/line chart, monthly trend)
    ├── OutstandingByGradeTable    (EduOctoDataTable — grade level, outstanding amount, % of total)
    └── RecentTransactionsSection  (EduOctoListItem rows, link to full Payment history)
```

## State Shape

```kotlin
data class FinanceReportsUiState(
    val isLoading: Boolean = false,
    val dateRange: DateRange = DateRange.currentMonth(),
    val collectedThisPeriod: Long = 0,
    val outstandingTotal: Long = 0,
    val collectionRatePercent: Double = 0.0,
    val trend: ImmutableList<MetricPoint> = persistentListOf(),
    val outstandingByGrade: ImmutableList<GradeOutstandingRow> = persistentListOf(),
    val recentTransactions: ImmutableList<PaymentUiModel> = persistentListOf(),
    val error: AppError? = null,
)
data class GradeOutstandingRow(val gradeLevel: Int, val outstandingAmount: Long, val percentOfTotal: Double)
```

## Business Rule Hooks (per `ERP/Finance.md`)

- `outstandingTotal` is computed from `Invoice.status in {Issued, Overdue, PartiallyPaid}`'s
  remaining balance — never a separately stored running total.
- `collectionRatePercent` = collected / (collected + outstanding) for the selected period.
- Table rows are clickable → drill into `Blueprints/PaymentBlueprint.md`'s filtered list for that
  grade level.

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Summary Stats | Skeleton tiles | N/A (always shows 0/Rp0 if genuinely empty) | Inline retry |
| Trend Chart | Skeleton chart block | "Belum ada data untuk periode ini" | Inline retry |
| Outstanding Table | Skeleton rows | EmptyState ("Tidak ada tunggakan ✓", positive tone) | Inline retry banner replacing table body |
| Recent Transactions | Skeleton rows | EmptyState neutral | Inline retry |

## Permission Notes

Visible only to Finance Staff and Principal/VP per `ERP/Finance.md` permission matrix — Admin Staff
and Teachers must receive a permission-denied state if this route is reached directly via deep link.
