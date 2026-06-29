# Form Example

A worked example of the Record Payment form (`Blueprints/PaymentBlueprint.md`), demonstrating
`13-FORM-STANDARDS.md` validation rules and currency field handling end-to-end.

## UI State

```kotlin
@Immutable
data class RecordPaymentUiState(
    val invoice: InvoiceSummaryUiModel? = null,
    val amountText: String = "",
    val method: PaymentMethod = PaymentMethod.Cash,
    val reference: String = "",
    val isSubmitting: Boolean = false,
    val fieldErrors: ImmutableMap<String, String> = persistentMapOf(),
    val error: AppError? = null,
)
data class InvoiceSummaryUiModel(val studentName: String, val periodLabel: String, val remainingBalance: Long)
```

## ViewModel — on-blur validation pattern

```kotlin
class RecordPaymentViewModel(
    private val invoiceId: String,
    private val recordPayment: RecordPaymentUseCase,
    private val invoiceRepository: InvoiceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordPaymentUiState())
    val uiState: StateFlow<RecordPaymentUiState> = _uiState.asStateFlow()
    private val _effect = Channel<RecordPaymentEffect>(Channel.BUFFERED)
    val effect: Flow<RecordPaymentEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val invoice = invoiceRepository.getInvoice(invoiceId)
            _uiState.update {
                it.copy(
                    invoice = invoice?.toSummaryUiModel(),
                    amountText = invoice?.let { inv -> (inv.amount - inv.amountPaid).toString() } ?: "",
                )
            }
        }
    }

    fun onAmountChanged(value: String) = _uiState.update { it.copy(amountText = value, fieldErrors = it.fieldErrors - "amount") }
    fun onMethodChanged(method: PaymentMethod) = _uiState.update { it.copy(method = method, fieldErrors = it.fieldErrors - "reference") }
    fun onReferenceChanged(value: String) = _uiState.update { it.copy(reference = value, fieldErrors = it.fieldErrors - "reference") }

    fun onAmountBlurred() = validateField("amount") {
        val amount = _uiState.value.amountText.toLongOrNull()
        val balance = _uiState.value.invoice?.remainingBalance ?: 0
        when {
            amount == null -> "Jumlah tidak valid"
            amount <= 0 -> "Jumlah harus lebih dari 0"
            amount > balance -> null // overpayment allowed per ERP/Payment.md rule 2 (becomes credit)
            else -> null
        }
    }

    fun onReferenceBlurred() = validateField("reference") {
        if (_uiState.value.method != PaymentMethod.Cash && _uiState.value.reference.isBlank())
            "Nomor referensi wajib diisi untuk metode ini" else null
    }

    private fun validateField(key: String, check: () -> String?) {
        val message = check()
        _uiState.update { state ->
            state.copy(fieldErrors = if (message != null) (state.fieldErrors + (key to message)).toImmutableMap()
                                       else (state.fieldErrors - key))
        }
    }

    fun onSubmit() {
        onAmountBlurred(); onReferenceBlurred()
        if (_uiState.value.fieldErrors.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val state = _uiState.value
            when (val result = recordPayment(invoiceId, state.amountText.toLong(), state.method, state.reference)) {
                is AppResult.Success -> _effect.send(RecordPaymentEffect.Saved)
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }
}
sealed interface RecordPaymentEffect { data object Saved : RecordPaymentEffect }
```

## Composable highlights

```kotlin
EduOctoCurrencyField(
    value = state.amountText,
    onValueChange = viewModel::onAmountChanged,
    onFocusLost = viewModel::onAmountBlurred,
    label = "Jumlah Pembayaran",
    errorText = state.fieldErrors["amount"],
    modifier = Modifier.fillMaxWidth(),
)

EduOctoSegmentedControl(
    options = listOf("Tunai" to PaymentMethod.Cash, "Transfer" to PaymentMethod.BankTransfer, "Lainnya" to PaymentMethod.EWallet),
    selected = state.method,
    onSelected = viewModel::onMethodChanged,
)

if (state.method != PaymentMethod.Cash) {
    EduOctoTextField(
        value = state.reference,
        onValueChange = viewModel::onReferenceChanged,
        onFocusLost = viewModel::onReferenceBlurred,
        label = "Nomor Referensi",
        errorText = state.fieldErrors["reference"],
        modifier = Modifier.fillMaxWidth(),
    )
}

EduOctoButton(
    text = "Simpan Pembayaran",
    onClick = viewModel::onSubmit,
    isLoading = state.isSubmitting,
    modifier = Modifier.fillMaxWidth(),
)
```

This demonstrates: on-blur validation (not on every keystroke), data preserved on submit failure
(`isSubmitting` resets but fields are untouched), conditional field display (reference field only for
non-Cash), and currency-specific input handling per `13-FORM-STANDARDS.md`.
