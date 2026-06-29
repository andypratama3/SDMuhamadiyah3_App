# ERP — Library Module

## Purpose

Tracks the school library's book catalog and lending/return cycle for students and staff.

## Domain Model

```kotlin
data class Book(
    val id: String,
    val isbn: String?,
    val title: String,
    val author: String,
    val category: String,
    val totalCopies: Int,
    val availableCopies: Int,
)

data class LoanRecord(
    val id: String,
    val bookId: String,
    val borrowerId: String,            // studentId or teacherId
    val borrowerType: BorrowerType,
    val borrowedAt: LocalDate,
    val dueDate: LocalDate,
    val returnedAt: LocalDate?,
    val status: LoanStatus,
)
enum class BorrowerType { Student, Teacher }
enum class LoanStatus { Active, Returned, Overdue, Lost }
```

## Business Rules

1. **`availableCopies` is derived**, decremented on loan creation and incremented on return —
   never independently edited; a discrepancy between `totalCopies` and the sum of active loans +
   available copies is a data integrity issue to surface, not silently correct.
2. **A borrower cannot exceed the configured max concurrent loans** (default: 3 for students, 5 for
   teachers — configurable in `ERP/Settings.md`).
3. **Overdue status** is derived (`dueDate < today AND returnedAt == null`), surfaced in Admin Staff's
   actionable items and optionally a Parent notification (`ERP/Notification.md`) for student loans.
4. **Lost book handling**: marking a loan `Lost` requires Admin confirmation and may trigger a
   replacement-cost note (informational; actual billing, if the school charges for lost books, is
   handled as a manually-issued one-time `FeeStructure`/`Invoice` in `ERP/Finance.md`, not automated
   here, since policy varies by school).

## Screens

- Catalog browse/search (all roles, read access).
- Loan management (Admin/Librarian role within Admin Staff) — checkout/return flow, overdue list.
- My Loans (Student/Teacher) — active and historical loans, due-date reminders.

## Permission Matrix

| Action | Admin Staff | Teacher | Student | Parent |
|---|---|---|---|---|
| Manage catalog (add/edit books) | ✅ | ❌ | ❌ | ❌ |
| Checkout/return a loan | ✅ | ❌ | ❌ | ❌ |
| Browse catalog | ✅ | ✅ | ✅ | ✅ |
| View own loans | ➖ | ✅ | ✅ | ✅ (child's loans, read-only) |

## Audit Requirements

Loan checkout/return/lost transitions are logged with actor and timestamp — moderate sensitivity,
standard audit retention applies (no special financial/PII rigor beyond `27-SECURITY.md` baseline).
