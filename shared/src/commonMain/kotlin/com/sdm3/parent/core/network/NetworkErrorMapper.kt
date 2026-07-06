package com.sdm3.parent.core.network

private val IP_PATTERN = Regex("""\d{1,3}(\.\d{1,3}){3}""")
private val URL_PATTERN = Regex("""https?://\S+""", RegexOption.IGNORE_CASE)

fun sanitizeUserFacingMessage(raw: String?): String {
    val message = raw?.trim().orEmpty()
    if (message.isEmpty()) return "Terjadi kesalahan. Silakan coba lagi."

    if (IP_PATTERN.containsMatchIn(message) || URL_PATTERN.containsMatchIn(message)) {
        return "Tidak ada koneksi internet. Periksa jaringan Anda."
    }

    val lower = message.lowercase()
    return when {
        lower.contains("unable to resolve host") ||
            lower.contains("network is unreachable") ||
            lower.contains("failed to connect") ||
            lower.contains("connection refused") ||
            lower.contains("no route to host") ||
            lower.contains("hostname") && lower.contains("not known") ->
            "Tidak ada koneksi internet. Periksa jaringan Anda."
        lower.contains("timeout") || lower.contains("timed out") ->
            "Permintaan terlalu lama. Silakan coba lagi."
        lower.contains("ssl") || lower.contains("certificate") ->
            "Koneksi aman gagal. Silakan coba lagi."
        else -> "Terjadi kesalahan. Silakan coba lagi."
    }
}

fun Throwable.toApiError(fallback: String = "Terjadi kesalahan. Silakan coba lagi."): ApiError {
    val message = message.orEmpty()
    val lower = message.lowercase()
    return when {
        lower.contains("timeout") || lower.contains("timed out") -> ApiError.Timeout
        lower.contains("unable to resolve host") ||
            lower.contains("network is unreachable") ||
            lower.contains("failed to connect") ||
            lower.contains("connection refused") ||
            IP_PATTERN.containsMatchIn(message) ||
            URL_PATTERN.containsMatchIn(message) -> ApiError.NoInternet
        else -> ApiError.Unknown(sanitizeUserFacingMessage(message).ifBlank { fallback })
    }
}
