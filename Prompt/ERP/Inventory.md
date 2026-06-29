# ERP — Inventory Module

## Purpose

Tracks school physical assets and consumable supplies (furniture, lab equipment, stationery,
electronics) for administrative oversight, distinct from the Library's book-lending domain.

## Domain Model

```kotlin
data class InventoryItem(
    val id: String,
    val name: String,
    val category: InventoryCategory,
    val quantity: Int,
    val unit: String,                  // "unit", "box", "set"
    val condition: ItemCondition,
    val location: String,              // e.g. "Lab Komputer", "Gudang"
    val isConsumable: Boolean,
)
enum class InventoryCategory { Furniture, Electronics, LabEquipment, Stationery, Sports, Other }
enum class ItemCondition { Good, NeedsRepair, Damaged, Disposed }

data class InventoryTransaction(
    val id: String,
    val itemId: String,
    val type: TransactionType,
    val quantityChange: Int,           // positive for in, negative for out
    val reason: String,
    val performedBy: String,
    val performedAt: Instant,
)
enum class TransactionType { StockIn, StockOut, Adjustment, Disposal }
```

## Business Rules

1. **`quantity` is derived from the sum of all `InventoryTransaction.quantityChange`** for an item —
   never edited directly; corrections go through an `Adjustment` transaction with a stated reason
   (mirrors the audit-first pattern used for Finance/Attendance).
2. **Consumable items below a configurable low-stock threshold** surface in Admin Staff's
   actionable items (e.g. "Stok kertas A4 menipis").
3. **Disposal requires Admin + Principal/VP approval** for items above a configurable value
   threshold, mirroring the Finance write-off co-approval pattern.

## Screens

- Inventory List (Admin Staff) — filterable by category/condition/location.
- Item Detail — full transaction history for traceability.
- Stock In/Out form — see `13-FORM-STANDARDS.md`.

## Permission Matrix

| Action | Principal/VP | Admin Staff | Teacher | Finance |
|---|---|---|---|---|
| View inventory | ✅ | ✅ | ❌ | ❌ |
| Record stock in/out/adjustment | ❌ | ✅ | ❌ | ❌ |
| Approve high-value disposal | ✅ | ✅ (co-approve) | ❌ | ❌ |

## Audit Requirements

Every `InventoryTransaction` is logged with actor, timestamp, and reason — standard audit retention.
