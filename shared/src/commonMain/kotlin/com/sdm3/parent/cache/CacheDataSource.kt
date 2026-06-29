package com.sdm3.parent.cache

import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.remote.dto.GradeWeightDto
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.data.remote.dto.StudentDto
class CacheDataSource(private val database: SDM3Database) {

    private val queries get() = database.sDM3DatabaseQueries
    private val now get() = epochMillisNow()

    // -- Students --
    fun getStudents(): List<StudentDto> =
        queries.getAllStudents().executeAsList().map { it.toDto() }

    fun getStudentById(id: String): StudentDto? =
        queries.getStudentById(id).executeAsOneOrNull()?.toDto()

    fun cacheStudents(students: List<StudentDto>) {
        queries.transaction {
            queries.deleteAllStudents()
            students.forEach { queries.insertStudent(it.toEntity(now)) }
        }
    }

    fun cacheStudent(student: StudentDto) {
        queries.insertStudent(student.toEntity(now))
    }

    // -- Attendance --
    fun getAttendances(studentId: String, month: Int, year: Int): List<AttendanceDto> =
        queries.getAttendancesByStudentAndMonth(
            student_id = studentId,
            month = month.toLong(),
            year = year.toLong(),
        ).executeAsList().map { it.toDto() }

    fun cacheAttendances(studentId: String, month: Int, year: Int, attendances: List<AttendanceDto>) {
        queries.transaction {
            queries.deleteAttendancesByStudent(studentId)
            attendances.forEach { queries.insertAttendance(it.toEntity(studentId, month, year, now)) }
        }
    }

    // -- Attendance Summary --
    fun getAttendanceSummary(studentId: String): AttendanceSummaryDto? =
        queries.getAttendanceSummary(studentId).executeAsOneOrNull()?.toDto()

    fun cacheAttendanceSummary(studentId: String, summary: AttendanceSummaryDto) {
        queries.insertAttendanceSummary(summary.toEntity(studentId, now))
    }

    // -- Grades --
    fun getGrades(studentId: String): List<GradeDto> =
        queries.getGradesByStudent(studentId).executeAsList().map { it.toDto() }

    fun getGradesBySemester(studentId: String, semester: String): List<GradeDto> =
        queries.getGradesByStudentAndSemester(studentId, semester).executeAsList().map { it.toDto() }

    fun cacheGrades(studentId: String, grades: List<GradeDto>) {
        queries.transaction {
            queries.deleteGradesByStudent(studentId)
            grades.forEach { queries.insertGrade(it.toEntity(studentId, now)) }
        }
    }

    // -- Grade Components --
    fun getGradeComponents(subjectId: String, studentId: String): List<GradeComponentDto> =
        queries.getGradeComponentsBySubject(subjectId, studentId).executeAsList().map { it.toDto() }

    fun cacheGradeComponents(studentId: String, components: List<GradeComponentDto>) {
        queries.transaction {
            queries.deleteGradeComponentsByStudent(studentId)
            components.forEach { queries.insertGradeComponent(it.toEntity(studentId)) }
        }
    }

    // -- Grade Weights --
    fun getGradeWeights(subjectId: String): List<GradeWeightDto> =
        queries.getGradeWeightsBySubject(subjectId).executeAsList().map { it.toDto() }

    fun cacheGradeWeights(weights: List<GradeWeightDto>) {
        queries.transaction {
            weights.forEach { queries.insertGradeWeight(it.toEntity()) }
        }
    }

    // -- Fees --
    fun getFees(studentId: String): List<StudentFeeDto> =
        queries.getFeesByStudent(studentId).executeAsList().map { it.toDto() }

    fun cacheFees(studentId: String, fees: List<StudentFeeDto>) {
        queries.transaction {
            queries.deleteFeesByStudent(studentId)
            fees.forEach { queries.insertFee(it.toEntity(studentId, now)) }
        }
    }

    // -- Payments --
    fun getPayments(studentId: String): List<PaymentDto> =
        queries.getPaymentsByStudent(studentId).executeAsList().map { it.toDto() }

    fun cachePayments(studentId: String, payments: List<PaymentDto>) {
        queries.transaction {
            queries.deletePaymentsByStudent(studentId)
            payments.forEach { queries.insertPayment(it.toEntity(studentId, now)) }
        }
    }

    // -- Notifications --
    fun getNotifications(): List<NotificationDto> =
        queries.getAllNotifications().executeAsList().map { it.toDto() }

    fun cacheNotifications(notifications: List<NotificationDto>) {
        queries.transaction {
            queries.deleteAllNotifications()
            notifications.forEach { queries.insertNotification(it.toEntity(now)) }
        }
    }

    // -- Articles --
    fun getArticles(): List<ArticleDto> =
        queries.getAllArticles().executeAsList().map { it.toDto() }

    fun cacheArticles(articles: List<ArticleDto>) {
        queries.transaction {
            queries.deleteAllArticles()
            articles.forEach { queries.insertArticle(it.toEntity(now)) }
        }
    }

    // -- Extracurriculars --
    fun getExtracurriculars(): List<ExtracurricularDto> =
        queries.getAllExtracurriculars().executeAsList().map { it.toDto() }

    fun cacheExtracurriculars(extracurriculars: List<ExtracurricularDto>) {
        queries.transaction {
            queries.deleteAllExtracurriculars()
            extracurriculars.forEach { queries.insertExtracurricular(it.toEntity(now)) }
        }
    }

    // -- Rapors --
    fun getRaporInstances(studentId: String): List<RaporInstanceDto> =
        queries.getRaporInstancesByStudent(studentId).executeAsList().map { it.toDto() }

    fun cacheRaporInstances(studentId: String, instances: List<RaporInstanceDto>) {
        queries.transaction {
            queries.deleteRaporByStudent(studentId)
            instances.forEach { queries.insertRaporInstance(it.toEntity(studentId, now)) }
        }
    }

    // -- Profile --
    fun getProfile(id: String): ProfileDto? =
        queries.getProfile(id).executeAsOneOrNull()?.toDto()

    fun cacheProfile(profile: ProfileDto) {
        queries.insertProfile(profile.toEntity(now))
    }
}

// -- DTO to Entity mappings --

private fun StudentDto.toEntity(cachedAt: Long) = StudentEntity(
    id = id, name = name, nisn = nisn, nis = nis, gender = gender,
    birth_place = birthPlace, birth_date = birthDate, photo = photo,
    class_name = className, spp = spp?.toLong(), dpp = dpp?.toLong(),
    cached_at = cachedAt,
)

private fun StudentEntity.toDto() = StudentDto(
    id = id, name = name, nisn = nisn, nis = nis, gender = gender,
    birthPlace = birth_place, birthDate = birth_date, photo = photo,
    className = class_name, spp = spp?.toInt(), dpp = dpp?.toInt(),
)

private fun AttendanceDto.toEntity(studentId: String, month: Int, year: Int, cachedAt: Long) = AttendanceEntity(
    id = id, student_id = studentId, date = date, status = status,
    notes = notes, month = month.toLong(), year = year.toLong(),
    cached_at = cachedAt,
)

private fun AttendanceEntity.toDto() = AttendanceDto(
    id = id, date = date, status = status, notes = notes,
)

private fun AttendanceSummaryDto.toEntity(studentId: String, cachedAt: Long) = AttendanceSummaryEntity(
    student_id = studentId, hadir = hadir.toLong(), sakit = sakit.toLong(),
    izin = izin.toLong(), alpa = alpa.toLong(), cached_at = cachedAt,
)

private fun AttendanceSummaryEntity.toDto() = AttendanceSummaryDto(
    hadir = hadir.toInt(), sakit = sakit.toInt(),
    izin = izin.toInt(), alpa = alpa.toInt(),
)

private fun GradeDto.toEntity(studentId: String, cachedAt: Long) = GradeEntity(
    id = id, subject_id = subjectId, subject_name = subjectName,
    score = score, predicate = predicate, narrative = narrative,
    semester = semester, student_id = studentId, cached_at = cachedAt,
)

private fun GradeEntity.toDto() = GradeDto(
    id = id, subjectId = subject_id, subjectName = subject_name,
    score = score, predicate = predicate, narrative = narrative,
    semester = semester,
)

private fun GradeComponentDto.toEntity(studentId: String) = GradeComponentEntity(
    id = id, subject_id = subjectId, subject_name = subjectName,
    component_type = componentType, component_subtype = componentSubtype,
    score = score, tp_name = tpName, tp_number = tpNumber?.toLong(),
    student_id = studentId,
)

private fun GradeComponentEntity.toDto() = GradeComponentDto(
    id = id, subjectId = subject_id, subjectName = subject_name,
    componentType = component_type, componentSubtype = component_subtype,
    score = score, tpName = tp_name, tpNumber = tp_number?.toInt(),
)

private fun GradeWeightDto.toEntity() = GradeWeightEntity(
    id = id, subject_id = subjectId, component_label = componentLabel,
    percentage = percentage, kkm = kkm,
)

private fun GradeWeightEntity.toDto() = GradeWeightDto(
    id = id, subjectId = subject_id, componentLabel = component_label,
    percentage = percentage, kkm = kkm,
)

private fun StudentFeeDto.toEntity(studentId: String, cachedAt: Long) = StudentFeeEntity(
    id = id, payment_title_id = paymentTitleId,
    payment_title_name = paymentTitleName, amount = amount,
    due_date = dueDate, status = status, student_id = studentId,
    cached_at = cachedAt,
)

private fun StudentFeeEntity.toDto() = StudentFeeDto(
    id = id, paymentTitleId = payment_title_id,
    paymentTitleName = payment_title_name, amount = amount,
    dueDate = due_date, status = status,
)

private fun PaymentDto.toEntity(studentId: String, cachedAt: Long) = PaymentEntity(
    id = id, order_id = orderId, gross_amount = grossAmount,
    payment_type = paymentType, status = status, va_number = vaNumber,
    paid_at = paidAt, created_at = createdAt, student_id = studentId,
    cached_at = cachedAt,
)

private fun PaymentEntity.toDto() = PaymentDto(
    id = id, orderId = order_id, grossAmount = gross_amount,
    paymentType = payment_type, status = status, vaNumber = va_number,
    paidAt = paid_at, createdAt = created_at,
)

private fun NotificationDto.toEntity(cachedAt: Long) = NotificationEntity(
    id = id, type = type, title = title, message = message,
    data_json = data?.toString(),
    read_at = readAt, created_at = createdAt, cached_at = cachedAt,
)

private fun NotificationEntity.toDto() = NotificationDto(
    id = id, type = type, title = title, message = message,
    data = parseDataJson(data_json), readAt = read_at, createdAt = created_at,
)

private fun parseDataJson(json: String?): Map<String, String>? {
    if (json == null) return null
    val map = mutableMapOf<String, String>()
    val trimmed = json.removePrefix("{").removeSuffix("}").trim()
    if (trimmed.isEmpty()) return emptyMap()
    trimmed.split(",").forEach { pair ->
        val parts = pair.split("=", limit = 2)
        if (parts.size == 2) map[parts[0].trim()] = parts[1].trim()
    }
    return map
}

private fun ArticleDto.toEntity(cachedAt: Long) = ArticleEntity(
    id = id, title = title, content = content, image_url = image,
    category = category, published_at = publishedAt, cached_at = cachedAt,
)

private fun ArticleEntity.toDto() = ArticleDto(
    id = id, title = title, content = content, image = image_url,
    category = category, publishedAt = published_at,
)

private fun ExtracurricularDto.toEntity(cachedAt: Long) = ExtracurricularEntity(
    id = id, name = name, description = description,
    schedule = schedule, teacher_name = teacherName, cached_at = cachedAt,
)

private fun ExtracurricularEntity.toDto() = ExtracurricularDto(
    id = id, name = name, description = description,
    schedule = schedule, teacherName = teacher_name,
)

private fun RaporInstanceDto.toEntity(studentId: String, cachedAt: Long) = RaporInstanceEntity(
    id = id, student_id = studentId, semester = semester,
    academic_year = academicYear, status = status, pdf_url = pdfUrl,
    cached_at = cachedAt,
)

private fun RaporInstanceEntity.toDto() = RaporInstanceDto(
    id = id, studentId = student_id, semester = semester,
    academicYear = academic_year, status = status, pdfUrl = pdf_url,
)

private fun ProfileDto.toEntity(cachedAt: Long) = ProfileEntity(
    id = id, name = name, email = email, phone = phone,
    avatar_url = avatar, cached_at = cachedAt,
)

private fun ProfileEntity.toDto() = ProfileDto(
    id = id, name = name, email = email, phone = phone, avatar = avatar_url,
)
