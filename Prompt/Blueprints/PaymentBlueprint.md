# Payment Blueprint

Blueprint for Record Payment (Finance Staff) and Payment History (Parent/Student), implementing
`ERP/Payment.md` and `13-FORM-STANDARDS.md`/`14-LIST-STANDARDS.md`.

## Record Payment (Finance Staff)

```
RecordPaymentScreen   (launched as EduOctoBottomSheet from an Invoice row, per 16-DIALOG-STANDARDS.md)
└── RecordPaymentContent(state: RecordPaymentUiState)
    ├── InvoiceSummaryHeader  (student name, invoice period, remaining balance)
    ├── EduOctoCurrencyField  (amount, defaults to remaining balance)
    ├── EduOctoSegmentedControl (PaymentMethod: Cash / Transfer / Other)
    ├── EduOctoTextField      (reference number — required for non-Cash)
    └── EduOctoButton(Primary, "Simpan Pembayaran", loading state while submitting)
```

## Payment History (Parent/Student)

```
PaymentHistoryScreen
└── PaymentHistoryContent(state: PaymentHistoryUiState)
    ├── EduOctoTopBar(title = "Riwayat Pembayaran")
    ├── OutstandingSummaryCard (GlassSurface, total outstanding or "Lunas ✓" positive state)
    └── LazyColumn of PaymentRow
          (date, invoice period, amount, StatusPill, tap → receipt detail/download)
```

## State Shape

```kotlin
data class RecordPaymentUiState(
    val invoice: InvoiceSummaryUiModel? = null,
    val amount: String = "",
    val method: PaymentMethod = PaymentMethod.Cash,
    val reference: String = "",
    val isSubmitting: Boolean = false,
    val fieldErrors: ImmutableMap<String, String> = persistentMapOf(),
    val error: AppError? = null,
)

data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val outstandingTotal: Long = 0,
    val payments: ImmutableList<PaymentRowUiModel> = persistentListOf(),
    val error: AppError? = null,
)
```

## Business Rule Hooks (per `ERP/Payment.md`)

- Submitting Record Payment calls `RecordPaymentUseCase`, which updates the linked `Invoice.status`
  and handles overpayment-as-credit logic — the screen itself contains no balance math.
- `reference` field is required (validated) when `method != Cash`, optional otherwise — validation
  rule lives in the ViewModel, not duplicated in the UI layer beyond displaying the error.

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Record Payment form | N/A (form renders immediately once invoice summary loads) | — | Inline error near submit button, data preserved |
| Payment History | 5 skeleton rows | Positive EmptyState ("Belum ada riwayat pembayaran") | EduOctoErrorState with retry |

## Permission Notes

Record Payment is Finance Staff-only. Payment History is scoped to the requesting Parent's own
children or the Student's own record — never another family's data, per `ERP/Payment.md` matrix.
