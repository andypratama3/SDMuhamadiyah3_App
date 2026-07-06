package com.sdm3.parent.domain.model

enum class AttendanceStatus(val apiValue: String, val label: String) {
    HADIR("hadir", "Hadir"),
    IZIN("izin", "Izin"),
    SAKIT("sakit", "Sakit"),
    ALPA("alpa", "Alpa"),
    PULANG("pulang", "Pulang"),
    ;

    val parentTodayLabel: String
        get() = when (this) {
            HADIR -> "Terverifikasi Hadir"
            SAKIT -> "Sedang Sakit"
            IZIN -> "Izin Tidak Hadir"
            ALPA -> "Tanpa Keterangan"
            PULANG -> "Pulang (Check-out)"
        }

    companion object {
        val teacherSelectable: List<AttendanceStatus> = listOf(HADIR, IZIN, SAKIT, ALPA, PULANG)

        fun fromApi(value: String?): AttendanceStatus? {
            if (value.isNullOrBlank()) return null
            return entries.find { it.apiValue.equals(value, ignoreCase = true) }
                ?: when (value.lowercase()) {
                    "present" -> HADIR
                    "excused", "permission" -> IZIN
                    "sick" -> SAKIT
                    "absent" -> ALPA
                    else -> null
                }
        }
    }
}
