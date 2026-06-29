# ERP — Payment Module

## Purpose

Records and reconciles actual payments against `Invoice`s defined in `ERP/Finance.md`. This module
is the transaction ledger; Finance module is the billing policy/reporting layer above it.

## Domain Model

```kotlin
data class Payment(
    val id: String,
    val invoiceId: String,
    val studentId: String,
    val amount: Long,
    val method: PaymentMethod,
    val paidAt: Instant,
    val reference: String?,            // bank/VA reference number, receipt number
    val recordedBy: String,            // Finance Staff userId, or "system" for gateway webhook
    val status: PaymentStatus,
)
enum class PaymentMethod { Cash, BankTransfer, VirtualAccount, QRIS, EWallet }
enum class PaymentStatus { Pending, Confirmed, Failed, Refunded }

data class Receipt(
    val paymentId: String,
    val receiptNumber: String,         // sequential, school-defined format
    val issuedAt: Instant,
)
```

## Business Rules

1. **A payment always references exactly one invoice.** A single physical transaction covering
   multiple invoices (e.g. a parent paying 3 months of SPP at once) is split into multiple `Payment`
   records at entry time, each referencing its own invoice — never one `Payment` with a list of
   invoice IDs, to keep reconciliation and reporting per-invoice unambiguous.
2. **Overpayment handling**: if `amount` exceeds the invoice's remaining balance, the excess is
   recorded as a credit balance on the student's account (`StudentCreditBalance`), automatically
   applied to the next issued invoice — never silently discarded or left unaccounted.
3. **Digital payment methods (VA/QRIS/E-Wallet) are reconciled via gateway webhook**, creating the
   `Payment` with `recordedBy = "system"` and `status = Confirmed` only after gateway confirmation —
   `Pending` status is shown to the parent in the interim, never prematurely shown as `Paid`.
4. **Cash payments require a Finance Staff-issued receipt** (`Receipt`, sequential number, no gaps —
   gaps in receipt sequence are a reportable anomaly per school financial policy) at the moment of
   recording.
5. **Refunds** create a new `Payment` with negative-equivalent accounting (`status = Refunded`
   linked to the original `Payment.id`) — never edit/delete the original payment record (preserves
   audit trail integrity per `27-SECURITY.md`).
6. **Invoice status auto-updates** the moment a `Confirmed` payment is recorded against it
   (`Issued/Overdue → PartiallyPaid → Paid` as `amountPaid` accumulates) — this is a side effect
   computed by `RecordPaymentUseCase`, not a manually-set field.

## Screens

- Record Payment (Finance Staff) — quick-entry form against a selected invoice, see
  `13-FORM-STANDARDS.md` and `Blueprints/PaymentBlueprint.md`.
- Payment History (Parent/Student) — list of own/child's payments with receipt download.
- Payment Reconciliation (Finance Staff) — gateway-confirmed vs. manually-recorded view, flags
  for any `Pending` digital payment older than a configurable threshold (possible gateway issue).

## Permission Matrix

| Action | Principal/VP | Finance Staff | Admin | Parent | Student |
|---|---|---|---|---|---|
| Record cash/manual payment | ❌ | ✅ | ❌ | ❌ | ❌ |
| Initiate digital payment (VA/QRIS) | ➖ | ➖ | ➖ | ✅ (own child) | ✅ (self, if applicable) |
| Issue refund | ✅ (co-approve) | ✅ (co-approve) | ❌ | ❌ | ❌ |
| View payment history/reports | ✅ | ✅ | ❌ | ✅ (own child only) | ✅ (self only) |
| Download receipt | ➖ | ✅ | ❌ | ✅ (own child) | ✅ (self) |

## Audit Requirements

Every `Payment` creation, refund, and any manual correction is audited (actor, timestamp, amount,
method, linked invoice) — financial transaction integrity is non-negotiable per `27-SECURITY.md`.
