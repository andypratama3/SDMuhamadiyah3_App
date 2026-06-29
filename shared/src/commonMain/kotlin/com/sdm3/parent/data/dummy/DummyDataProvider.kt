package com.sdm3.parent.data.dummy

import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.data.remote.dto.DashboardDto
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.remote.dto.GradeWeightDto
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.data.remote.dto.UserDto

internal object DummyDataProvider {

    val dummyUser = UserDto(
        id = "1",
        name = "Budi Santoso",
        email = "budi@example.com",
        phone = "081234567890",
        avatar = null
    )

    val dummyStudents = listOf(
        StudentDto(
            id = "101",
            name = "Ahmad Rizki Pratama",
            nisn = "0012345678",
            nis = "12345",
            gender = "L",
            birthPlace = "Samarinda",
            birthDate = "2014-05-12",
            photo = null,
            className = "5-A",
            spp = 150000,
            dpp = 50000
        ),
        StudentDto(
            id = "102",
            name = "Siti Aisyah Rahmadani",
            nisn = "0012345679",
            nis = "12346",
            gender = "P",
            birthPlace = "Samarinda",
            birthDate = "2014-08-21",
            photo = null,
            className = "5-B",
            spp = 150000,
            dpp = 50000
        ),
    )

    val dummyAttendanceSummary = AttendanceSummaryDto(
        hadir = 42,
        sakit = 2,
        izin = 1,
        alpa = 0
    )

    fun dummyAttendances(month: Int = 2, year: Int = 2026) = listOf(
        AttendanceDto(id = "a1", date = "$year-${month.toStr()}-01", status = "hadir"),
        AttendanceDto(id = "a2", date = "$year-${month.toStr()}-03", status = "hadir"),
        AttendanceDto(id = "a3", date = "$year-${month.toStr()}-04", status = "sakit", notes = "Demam"),
        AttendanceDto(id = "a4", date = "$year-${month.toStr()}-05", status = "hadir"),
        AttendanceDto(id = "a5", date = "$year-${month.toStr()}-06", status = "hadir"),
        AttendanceDto(id = "a6", date = "$year-${month.toStr()}-07", status = "izin", notes = "Acara keluarga"),
        AttendanceDto(id = "a7", date = "$year-${month.toStr()}-08", status = "hadir"),
        AttendanceDto(id = "a8", date = "$year-${month.toStr()}-10", status = "hadir"),
        AttendanceDto(id = "a9", date = "$year-${month.toStr()}-11", status = "hadir"),
        AttendanceDto(id = "a10", date = "$year-${month.toStr()}-12", status = "hadir"),
        AttendanceDto(id = "a11", date = "$year-${month.toStr()}-14", status = "hadir"),
        AttendanceDto(id = "a12", date = "$year-${month.toStr()}-15", status = "sakit", notes = "Flu"),
        AttendanceDto(id = "a13", date = "$year-${month.toStr()}-17", status = "hadir"),
        AttendanceDto(id = "a14", date = "$year-${month.toStr()}-18", status = "hadir"),
        AttendanceDto(id = "a15", date = "$year-${month.toStr()}-19", status = "hadir"),
        AttendanceDto(id = "a16", date = "$year-${month.toStr()}-20", status = "hadir"),
        AttendanceDto(id = "a17", date = "$year-${month.toStr()}-21", status = "hadir"),
        AttendanceDto(id = "a18", date = "$year-${month.toStr()}-22", status = "hadir"),
        AttendanceDto(id = "a19", date = "$year-${month.toStr()}-24", status = "hadir"),
        AttendanceDto(id = "a20", date = "$year-${month.toStr()}-25", status = "hadir"),
    )

    val dummyGrades = listOf(
        GradeDto(id = "g1", subjectId = "s1", subjectName = "Pendidikan Agama Islam", score = 88.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g2", subjectId = "s2", subjectName = "Pendidikan Pancasila", score = 85.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g3", subjectId = "s3", subjectName = "Bahasa Indonesia", score = 82.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g4", subjectId = "s4", subjectName = "Matematika", score = 78.0, predicate = "B", semester = "ganjil"),
        GradeDto(id = "g5", subjectId = "s5", subjectName = "IPAS", score = 85.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g6", subjectId = "s6", subjectName = "PJOK", score = 90.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g7", subjectId = "s7", subjectName = "Seni dan Budaya", score = 87.0, predicate = "A", semester = "ganjil"),
        GradeDto(id = "g8", subjectId = "s8", subjectName = "Bahasa Inggris", score = 80.0, predicate = "A", semester = "ganjil"),
    )

    val dummyGradeComponents = listOf(
        GradeComponentDto(id = "gc1", subjectId = "s1", subjectName = "Pendidikan Agama Islam", componentType = "tugas", componentSubtype = "harian", score = 85.0, tpName = "Menyebutkan rukun iman", tpNumber = 1),
        GradeComponentDto(id = "gc2", subjectId = "s1", subjectName = "Pendidikan Agama Islam", componentType = "tugas", componentSubtype = "harian", score = 90.0, tpName = "Hafalan surat pendek", tpNumber = 2),
        GradeComponentDto(id = "gc3", subjectId = "s1", subjectName = "Pendidikan Agama Islam", componentType = "uts", score = 88.0),
        GradeComponentDto(id = "gc4", subjectId = "s1", subjectName = "Pendidikan Agama Islam", componentType = "uas", score = 86.0),
        GradeComponentDto(id = "gc5", subjectId = "s4", subjectName = "Matematika", componentType = "tugas", componentSubtype = "harian", score = 75.0, tpName = "Operasi hitung bilangan", tpNumber = 1),
        GradeComponentDto(id = "gc6", subjectId = "s4", subjectName = "Matematika", componentType = "tugas", componentSubtype = "harian", score = 80.0, tpName = "Pecahan senilai", tpNumber = 2),
        GradeComponentDto(id = "gc7", subjectId = "s4", subjectName = "Matematika", componentType = "uts", score = 78.0),
        GradeComponentDto(id = "gc8", subjectId = "s4", subjectName = "Matematika", componentType = "uas", score = 76.0),
    )

    val dummyGradeWeights = listOf(
        GradeWeightDto(id = "gw1", subjectId = "s1", componentLabel = "Tugas Harian", percentage = 30.0, kkm = 70.0),
        GradeWeightDto(id = "gw2", subjectId = "s1", componentLabel = "UTS", percentage = 30.0, kkm = 70.0),
        GradeWeightDto(id = "gw3", subjectId = "s1", componentLabel = "UAS", percentage = 40.0, kkm = 70.0),
    )

    val dummyActiveFees = listOf(
        StudentFeeDto(id = "f1", paymentTitleId = "pt1", paymentTitleName = "SPP Bulan Januari", amount = 150000.0, dueDate = "2026-01-10", status = "lunas"),
        StudentFeeDto(id = "f2", paymentTitleId = "pt1", paymentTitleName = "SPP Bulan Februari", amount = 150000.0, dueDate = "2026-02-10", status = "lunas"),
        StudentFeeDto(id = "f3", paymentTitleId = "pt1", paymentTitleName = "SPP Bulan Maret", amount = 150000.0, dueDate = "2026-03-10", status = "belum_lunas"),
        StudentFeeDto(id = "f4", paymentTitleId = "pt2", paymentTitleName = "DPP (Dana Pengembangan Pendidikan)", amount = 50000.0, dueDate = "2026-03-15", status = "belum_lunas"),
        StudentFeeDto(id = "f5", paymentTitleId = "pt3", paymentTitleName = "Biaya Studi Lapangan", amount = 200000.0, dueDate = "2026-04-01", status = "belum_lunas"),
    )

    val dummyPayments = listOf(
        PaymentDto(id = "p1", orderId = "INV/2026/001", grossAmount = 150000.0, paymentType = "bank_transfer", status = "settlement", vaNumber = "1234567890", paidAt = "2026-01-05 08:30:00", createdAt = "2026-01-05 08:25:00"),
        PaymentDto(id = "p2", orderId = "INV/2026/002", grossAmount = 150000.0, paymentType = "bank_transfer", status = "settlement", vaNumber = "1234567891", paidAt = "2026-01-10 09:15:00", createdAt = "2026-01-10 09:10:00"),
        PaymentDto(id = "p3", orderId = "INV/2026/003", grossAmount = 150000.0, paymentType = "qris", status = "settlement", paidAt = "2026-02-05 07:45:00", createdAt = "2026-02-05 07:40:00"),
        PaymentDto(id = "p4", orderId = "INV/2026/004", grossAmount = 150000.0, paymentType = "bank_transfer", status = "settlement", vaNumber = "1234567892", paidAt = "2026-02-10 10:00:00", createdAt = "2026-02-10 09:55:00"),
    )

    val dummySnapToken = SnapTokenResponse(
        snapToken = "dummy-snap-token-for-development",
        redirectUrl = "https://app.sandbox.midtrans.com/snap/v4/redirection/dummy"
    )

    val dummyNotifications = listOf(
        NotificationDto(id = "n1", type = "payment", title = "Pembayaran SPP Diterima", message = "Pembayaran SPP Bulan Februari an. Ahmad Rizki Pratama sebesar Rp150.000 telah diterima.", data = mapOf("payment_id" to "p1"), readAt = null, createdAt = "2026-02-10 10:05:00"),
        NotificationDto(id = "n2", type = "attendance", title = "Ketidakhadiran", message = "Ahmad Rizki Pratama tidak hadir pada 4 Februari 2026 karena sakit.", data = null, readAt = null, createdAt = "2026-02-04 08:00:00"),
        NotificationDto(id = "n3", type = "grade", title = "Nilai Tugas Baru", message = "Nilai tugas Matematika an. Ahmad Rizki Pratama telah dirilis.", data = null, readAt = "2026-02-15 12:00:00", createdAt = "2026-02-14 14:00:00"),
        NotificationDto(id = "n4", type = "article", title = "Pengumuman Libur", message = "Sehubungan dengan libur nasional, kegiatan belajar mengajar diliburkan.", data = null, readAt = "2026-02-10 09:00:00", createdAt = "2026-02-09 16:00:00"),
        NotificationDto(id = "n5", type = "payment", title = "Pengingat Pembayaran", message = "SPP Bulan Maret an. Ahmad Rizki Pratama sebesar Rp150.000 belum dibayar. Batas akhir 10 Maret 2026.", data = mapOf("fee_id" to "f3"), readAt = null, createdAt = "2026-03-01 07:00:00"),
    )

    val dummyArticles = listOf(
        ArticleDto(id = "a1", title = "Kegiatan Pesantren Kilat Ramadan 1447 H", content = "SD Muhammadiyah 3 Samarinda akan mengadakan kegiatan Pesantren Kilat pada bulan Ramadan 1447 H. Kegiatan ini bertujuan untuk meningkatkan keimanan dan ketakwaan siswa selama bulan suci. Acara akan diisi dengan tadarus Al-Qur'an, ceramah agama, dan lomba islami.", image = null, category = "kegiatan", publishedAt = "2026-02-20 08:00:00"),
        ArticleDto(id = "a2", title = "Penerimaan Rapor Semester Ganjil TP 2025/2026", content = "Pembagian rapor semester ganjil Tahun Pelajaran 2025/2026 akan dilaksanakan pada hari Sabtu, 21 Desember 2025. Orang tua/wali murid diharapkan hadir untuk menerima rapor secara langsung.", image = null, category = "akademik", publishedAt = "2025-12-15 10:00:00"),
        ArticleDto(id = "a3", title = "Jadwal Pelaksanaan Asesmen Sumatif Akhir Semester", content = "Asesmen Sumatif Akhir Semester Ganjil TP 2025/2026 akan dilaksanakan mulai tanggal 8-14 Desember 2025. Siswa diharapkan mempersiapkan diri dengan belajar sungguh-sungguh.", image = null, category = "akademik", publishedAt = "2025-11-25 09:00:00"),
        ArticleDto(id = "a4", title = "Kegiatan Class Meeting", content = "Setelah pelaksanaan asesmen, akan diadakan kegiatan Class Meeting berupa lomba-lomba antar kelas. Kegiatan ini bertujuan untuk mempererat tali persaudaraan antar siswa.", image = null, category = "kegiatan", publishedAt = "2025-12-10 08:00:00"),
        ArticleDto(id = "a5", title = "Info Pendaftaran Siswa Baru TP 2026/2027", content = "Penerimaan Peserta Didik Baru (PPDB) SD Muhammadiyah 3 Samarinda Tahun Pelajaran 2026/2027 telah dibuka. Pendaftaran dapat dilakukan secara online melalui website sekolah.", image = null, category = "informasi", publishedAt = "2026-01-02 08:00:00"),
    )

    val dummyRaporInstances = listOf(
        RaporInstanceDto(id = "r1", studentId = "101", semester = "Ganjil", academicYear = "2025/2026", status = "published", pdfUrl = null),
        RaporInstanceDto(id = "r2", studentId = "101", semester = "Genap", academicYear = "2024/2025", status = "published", pdfUrl = null),
        RaporInstanceDto(id = "r3", studentId = "101", semester = "Ganjil", academicYear = "2024/2025", status = "published", pdfUrl = null),
    )

    val dummyProfile = ProfileDto(
        id = "1",
        name = "Budi Santoso",
        email = "budi@example.com",
        phone = "081234567890",
        avatar = null
    )

    val dummyExtracurriculars = listOf(
        ExtracurricularDto(id = "e1", name = "Tapak Suci", description = "Latihan pencak silat Tapak Suci setiap hari Sabtu pukul 08.00-10.00 WITA", schedule = "Sabtu, 08.00-10.00 WITA", teacherName = "Ustadz Amir"),
        ExtracurricularDto(id = "e2", name = "Hizbul Wathan", description = "Kegiatan kepanduan HW setiap hari Rabu pukul 14.00-16.00 WITA", schedule = "Rabu, 14.00-16.00 WITA", teacherName = "Kak Rina"),
        ExtracurricularDto(id = "e3", name = "Tilawah Al-Qur'an", description = "Pembelajaran tilawah dan tahsin Al-Qur'an", schedule = "Senin & Kamis, 06.30-07.30 WITA", teacherName = "Ustadzah Fatimah"),
    )

    fun dummyDashboard(studentId: String): DashboardDto {
        val student = dummyStudents.find { it.id == studentId } ?: dummyStudents.first()
        return DashboardDto(
            student = student,
            attendanceSummary = dummyAttendanceSummary,
            recentGrades = dummyGrades,
            activeFees = dummyActiveFees,
            announcements = dummyArticles.take(3),
        )
    }

    fun dummyStudentById(id: String): StudentDto =
        dummyStudents.find { it.id == id } ?: dummyStudents.first()

    fun dummyAttendancesInMonth(month: Int, year: Int): List<AttendanceDto> =
        dummyAttendances(month, year)

    fun dummyPaymentsForStudent(studentId: String, status: String? = null): List<PaymentDto> =
        dummyPayments

    fun dummyCheckPaymentStatus(chargeId: String): PaymentDto =
        dummyPayments.firstOrNull() ?: PaymentDto(
            id = chargeId, orderId = "INV/2026/001",
            grossAmount = 150000.0, paymentType = "bank_transfer",
            status = "settlement", vaNumber = "1234567890",
            paidAt = "2026-01-05 08:30:00", createdAt = "2026-01-05 08:25:00"
        )

    val dummyRaporVerifyResponse = RaporVerifyResponse(
        valid = true,
        message = "Rapor terverifikasi",
        studentName = "Ahmad Rizki Pratama",
        nisn = "0012345678"
    )

    fun dummyNotificationMarkedAsRead(id: String): Unit = Unit

    private fun Int.toStr() = if (this < 10) "0$this" else "$this"
}
