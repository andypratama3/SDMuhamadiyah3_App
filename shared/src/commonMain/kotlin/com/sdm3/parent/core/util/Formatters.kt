package com.sdm3.parent.core.util

/**
 * Format angka menjadi Rupiah dengan pemisah ribuan titik, mis. 350000 -> "Rp350.000".
 * Dipakai bersama agar konsisten di seluruh layar (SPP, kwitansi, dashboard).
 */
fun formatRupiah(amount: Number): String {
    val value = amount.toLong()
    val negative = value < 0
    val digits = kotlin.math.abs(value).toString()
    val sb = StringBuilder()
    for (i in digits.indices) {
        if (i > 0 && (digits.length - i) % 3 == 0) sb.append('.')
        sb.append(digits[i])
    }
    return if (negative) "-Rp$sb" else "Rp$sb"
}

/**
 * Inisial nama untuk avatar (maks. 2 huruf), aman untuk nama kosong / berspasi.
 * Contoh: "Ahmad Fauzi" -> "AF", "Ahmad " -> "A", "" -> "?".
 */
fun nameInitials(name: String?): String {
    val parts = name?.trim()?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

private val bulanSingkat = listOf(
    "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
)

/**
 * Ubah tanggal ISO ("yyyy-MM-dd", "yyyy-MM-ddTHH:mm:ss", atau "yyyy-MM-dd HH:mm:ss")
 * menjadi "13 Jun 2026". Bila format tak dikenali, kembalikan apa adanya ("-" jika kosong).
 */
fun formatTanggal(raw: String?): String {
    val s = raw?.trim().orEmpty()
    if (s.isEmpty()) return "-"
    val datePart = s.substringBefore('T').substringBefore(' ')
    val parts = datePart.split('-')
    if (parts.size < 3) return s
    val year = parts[0].toIntOrNull() ?: return s
    val month = parts[1].toIntOrNull() ?: return s
    val day = parts[2].take(2).toIntOrNull() ?: return s
    if (month !in 1..12) return s
    return "$day ${bulanSingkat[month - 1]} $year"
}

/**
 * Seperti [formatTanggal] tapi menambahkan jam:menit bila tersedia,
 * mis. "2026-06-13 18:52:23" -> "13 Jun 2026, 18:52".
 */
fun formatTanggalWaktu(raw: String?): String {
    val s = raw?.trim().orEmpty()
    if (s.isEmpty()) return "-"
    val tanggal = formatTanggal(s)
    if (tanggal == s) return tanggal // format tak dikenali, jangan tempel waktu
    val timePart = when {
        s.contains('T') -> s.substringAfter('T')
        s.contains(' ') -> s.substringAfter(' ')
        else -> ""
    }
    val hm = timePart.take(5)
    val validHm = hm.length == 5 && hm[2] == ':' &&
        hm.substring(0, 2).toIntOrNull() != null && hm.substring(3, 5).toIntOrNull() != null
    return if (validHm) "$tanggal, $hm" else tanggal
}

/**
 * Beri awalan "Kelas " hanya bila belum ada, agar tak muncul "Kelas Kelas 1 Baghdad".
 * Backend kadang mengirim "Kelas 1 Baghdad" (sudah berawalan) atau "1 Baghdad".
 */
fun formatClassName(className: String?): String {
    val c = className?.trim().orEmpty()
    if (c.isEmpty()) return "-"
    return if (c.startsWith("Kelas", ignoreCase = true)) c else "Kelas $c"
}

/**
 * Ubah kode metode pembayaran Midtrans menjadi label ramah pengguna,
 * mis. "bank_transfer" -> "Transfer Bank", "gopay" -> "GoPay".
 */
fun formatPaymentMethod(type: String?): String {
    val t = type?.trim()?.lowercase().orEmpty()
    if (t.isEmpty()) return "-"
    return when (t) {
        "bank_transfer" -> "Transfer Bank"
        "echannel" -> "Mandiri Bill Payment"
        "gopay" -> "GoPay"
        "qris" -> "QRIS"
        "shopeepay" -> "ShopeePay"
        "credit_card" -> "Kartu Kredit"
        "cstore" -> "Gerai Retail"
        "bca_va" -> "Virtual Account BCA"
        "bni_va" -> "Virtual Account BNI"
        "bri_va" -> "Virtual Account BRI"
        "cimb_va" -> "Virtual Account CIMB"
        "permata_va" -> "Virtual Account Permata"
        "other_va" -> "Virtual Account"
        "akulaku" -> "Akulaku"
        "kredivo" -> "Kredivo"
        else -> t.split('_', ' ').filter { it.isNotBlank() }.joinToString(" ") { w ->
            w.replaceFirstChar { ch -> ch.uppercaseChar() }
        }
    }
}
