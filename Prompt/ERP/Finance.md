# ERP — Finance Module

## Purpose

The financial control center: SPP (monthly tuition) billing policy, invoice generation, collection
oversight, payroll, and financial reporting. `ERP/Payment.md` covers the actual transaction
recording UX; this module covers billing rules, invoicing, payroll, and reporting that sit above it.

## Domain Model

```kotlin
data class FeeStructure(
    val id: String,
    val name: String,                  // "SPP Reguler", "Uang Pangkal"
    val gradeLevel: Int?,              // null = applies to all levels
    val amount: Long,                  // IDR
    val frequency: FeeFrequency,
    val effectiveFrom: LocalDate,
)
enum class FeeFrequency { Monthly, OneTime, Annual, PerTerm }

data class Invoice(
    val id: String,
    val studentId: String,
    val feeStructureId: String,
    val periodLabel: String,           // "Juli 2026"
    val amount: Long,
    val dueDate: LocalDate,
    val status: InvoiceStatus,
    val amountPaid: Long,
)
enum class InvoiceStatus { Draft, Issued, PartiallyPaid, Paid, Overdue, Voided, WrittenOff }

data class PayrollRun(
    val id: String,
    val periodLabel: String,
    val staffEntries: List<PayrollEntry>,
    val status: PayrollStatus,
    val approvedBy: String?,
)
data class PayrollEntry(
    val staffId: String,
    val baseSalary: Long,
    val allowances: Long,
    val deductions: Long,
    val netPay: Long,
)
enum class PayrollStatus { Draft, PendingApproval, Approved, Disbursed }
```

## Business Rules

1. **Invoice generation is automated monthly** for `Monthly` fee structures, generated a configurable
   number of days before the period starts (default: 5 days), per active `FeeStructure` applicable to
   each active student's grade level — generation is idempotent (re-running for an already-generated
   period does nothing).
2. **Mid-period enrollment is prorated.** A student enrolling mid-month is billed
   `amount * (remaining_days / total_days_in_period)`, rounded to the nearest Rp1.000 — this exact
   rule lives in one `ProrateInvoiceUseCase`, never duplicated.
3. **Invoice status transitions:** `Draft → Issued → (PartiallyPaid → Paid) | Overdue → Paid`.
   `Overdue` is a derived status (computed from `dueDate < today AND status != Paid`), not a stored
   state that must be separately maintained — avoids drift between stored and actual status.
4. **Voiding/Write-off requires Finance Staff + Principal/VP co-approval** and a stated reason,
   always audited — these are the highest-risk financial actions in the system (see
   `27-SECURITY.md` typed-confirmation requirement).
5. **Payroll approval is a two-step workflow**: Finance Staff prepares `Draft`, Principal/VP approves
   (`PendingApproval → Approved`) before `Disbursed` — Finance Staff alone cannot move payroll
   straight to `Disbursed`, enforced server-side, not just hidden client-side.
6. **Reports are read-only aggregations** over Invoice/Payment/Payroll data — never a separately
   maintained "reporting" data set that could drift from source records.

## Screens

- Fee Structure management (Finance Staff/Principal) — define/update SPP and other fee policies.
- Invoice list/detail (Finance Staff) — filterable by status, class, period.
- Financial Reports (Finance Staff/Principal) — collection rate, outstanding by class/grade,
  month-over-month trend (see `Blueprints/FinanceBlueprint.md`).
- Payroll Run management (Finance Staff prepares, Principal/VP approves).

## Permission Matrix

| Action | Principal/VP | Finance Staff | Admin | Teacher | Parent | Student |
|---|---|---|---|---|---|---|
| Define fee structures | ✅ (approve) | ✅ (propose) | ❌ | ❌ | ❌ | ❌ |
| Generate/issue invoices | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Void/write-off invoice | ✅ (co-approve) | ✅ (co-approve) | ❌ | ❌ | ❌ | ❌ |
| View financial reports | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Prepare payroll | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Approve payroll | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| View own/child's invoice | ➖ | ➖ | ➖ | ➖ | ✅ | ✅ |

## Audit Requirements

Every fee structure change, invoice void/write-off, and payroll status transition is fully audited
(actor, timestamp, before/after, stated reason where applicable) per `27-SECURITY.md`.
