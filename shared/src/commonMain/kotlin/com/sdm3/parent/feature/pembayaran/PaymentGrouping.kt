package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class PaymentMonthGroup<T>(
    val month: Int,
    val monthName: String,
    val items: List<T>,
)

data class PaymentYearGroup<T>(
    val year: Int,
    val months: List<PaymentMonthGroup<T>>,
) {
    val itemCount: Int get() = months.sumOf { it.items.size }
}

object IndonesianMonths {
    val full = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember",
    )

    fun name(month: Int): String = full.getOrElse(month - 1) { "Lainnya" }
}

private const val FALLBACK_YEAR = 0
private const val FALLBACK_MONTH = 0

val PAID_FEE_STATUSES = setOf("lunas", "paid", "success", "settlement")
val PAID_PAYMENT_STATUSES = setOf("success", "settlement", "lunas", "paid", "capture", "completed")
val FAILED_PAYMENT_STATUSES = setOf("failed", "failure", "expire", "expired", "deny", "cancel", "cancelled", "refunded")

data class PaymentProgressInfo(
    val total: Int = 0,
    val paid: Int = 0,
    val percent: Int = 0,
) {
    val ratio: Float get() = if (total > 0) paid.toFloat() / total else 0f
}

fun currentYear(): Int =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

/** Parse ISO date ("yyyy-MM-dd", "yyyy-MM-ddTHH:mm:ss", "yyyy-MM-dd HH:mm:ss") → (year, month, day). */
fun parseIsoDateParts(raw: String?): Triple<Int, Int, Int>? {
    val s = raw?.trim().orEmpty()
    if (s.isEmpty()) return null
    val datePart = s.substringBefore('T').substringBefore(' ')
    val parts = datePart.split('-')
    if (parts.size < 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].take(2).toIntOrNull() ?: return null
    if (month !in 1..12) return null
    return Triple(year, month, day)
}

fun StudentFeeDto.isPaid(): Boolean = status.lowercase() in PAID_FEE_STATUSES

fun findActiveFee(fees: List<StudentFeeDto>): StudentFeeDto? =
    fees.firstOrNull { !it.isPaid() }

fun computePaymentProgress(fees: List<StudentFeeDto>): PaymentProgressInfo {
    val total = fees.size
    val paid = fees.count { it.isPaid() }
    val percent = if (total > 0) ((paid.toFloat() / total) * 100).toInt() else 0
    return PaymentProgressInfo(total = total, paid = paid, percent = percent)
}

fun paymentDateParts(payment: PaymentDto): Triple<Int, Int, Int>? =
    parseIsoDateParts(payment.paidAt) ?: parseIsoDateParts(payment.createdAt)

fun groupFeesByYearMonth(fees: List<StudentFeeDto>): List<PaymentYearGroup<StudentFeeDto>> =
    groupByYearMonth(
        items = fees,
        dateSelector = { parseIsoDateParts(it.dueDate) },
    )

fun groupPaymentsByYearMonth(payments: List<PaymentDto>): List<PaymentYearGroup<PaymentDto>> =
    groupByYearMonth(
        items = payments,
        dateSelector = { paymentDateParts(it) },
    )

private fun <T> groupByYearMonth(
    items: List<T>,
    dateSelector: (T) -> Triple<Int, Int, Int>?,
): List<PaymentYearGroup<T>> {
    if (items.isEmpty()) return emptyList()

    val nested = linkedMapOf<Int, LinkedHashMap<Int, MutableList<T>>>()
    for (item in items) {
        val parts = dateSelector(item)
        val year = parts?.first ?: FALLBACK_YEAR
        val month = parts?.second ?: FALLBACK_MONTH
        val yearBucket = nested.getOrPut(year) { linkedMapOf() }
        yearBucket.getOrPut(month) { mutableListOf() }.add(item)
    }

    return nested.entries
        .sortedByDescending { (year, _) -> year }
        .map { (year, monthMap) ->
            PaymentYearGroup(
                year = year,
                months = monthMap.entries
                    .sortedByDescending { (month, _) -> month }
                    .map { (month, monthItems) ->
                        PaymentMonthGroup(
                            month = month,
                            monthName = monthLabel(month),
                            items = monthItems,
                        )
                    },
            )
        }
}

private fun monthLabel(month: Int): String = when (month) {
    in 1..12 -> IndonesianMonths.name(month)
    else -> "Lainnya"
}

fun PaymentDto.paymentStatusLabel(): String = when {
    status.lowercase() in PAID_PAYMENT_STATUSES -> "LUNAS"
    status.lowercase() in FAILED_PAYMENT_STATUSES -> "GAGAL"
    else -> "MENUNGGU"
}

fun StudentFeeDto.feeStatusLabel(): String = when {
    isPaid() -> "LUNAS"
    status.lowercase() in setOf("overdue", "terlambat", "late") -> "TERLAMBAT"
    else -> "BELUM DIBAYAR"
}
