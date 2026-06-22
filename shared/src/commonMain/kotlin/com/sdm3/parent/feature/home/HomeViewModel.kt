package com.sdm3.parent.feature.home

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.*
import kotlinx.coroutines.delay

data class HomeUiState(
    val studentId: String = "",
    val studentName: String = "",
    val className: String = "",
    val students: List<StudentDto> = emptyList(),
    val attendanceSummary: AttendanceSummaryDto? = null,
    val recentGrades: List<GradeDto> = emptyList(),
    val activeFees: List<StudentFeeDto> = emptyList(),
    val announcements: List<ArticleDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class HomeViewModel : BaseViewModel<HomeUiState>(HomeUiState()) {

    fun loadDashboard(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
            delay(1000) // Simulate network delay
            
            val dummyStudents = listOf(
                StudentDto(id = "student_1", name = "Ahmad Fathoni", className = "4-A (Ibnu Sina)", nisn = "0012345678", gender = "Laki-laki", birthPlace = "Samarinda", birthDate = "2015-01-15"),
                StudentDto(id = "student_2", name = "Aisyah Humaira", className = "4-A (Ibnu Sina)", nisn = "0098765432", gender = "Perempuan", birthPlace = "Jakarta", birthDate = "2014-05-12")
            )

            val dummyGrades = listOf(
                GradeDto(id = "1", subjectId = "m1", subjectName = "Matematika", score = 92.0, predicate = "A", semester = "ganjil"),
                GradeDto(id = "2", subjectId = "m2", subjectName = "Bahasa Indonesia", score = 88.0, predicate = "B+", semester = "ganjil"),
                GradeDto(id = "3", subjectId = "m3", subjectName = "IPA", score = 95.0, predicate = "A", semester = "ganjil")
            )
            
            val dummyFees = listOf(
                StudentFeeDto(id = "f1", paymentTitleId = "p1", paymentTitleName = "SPP Juli 2026", amount = 350000.0, dueDate = "15 Juli 2026", status = "belum_bayar")
            )
            
            val dummyAnnouncements = listOf(
                ArticleDto(id = "a1", title = "Libur Hari Raya Idul Adha 1447 H", publishedAt = "18 Juni 2026"),
                ArticleDto(id = "a2", title = "Rapor Sumatif 1 Telah Terbit", publishedAt = "15 Juni 2026"),
                ArticleDto(id = "a3", title = "Jadwal Pembayaran SPP Semester Baru", publishedAt = "10 Juni 2026")
            )

            val student = dummyStudents.find { it.id == studentId } ?: dummyStudents.first()

            updateState {
                it.copy(
                    isLoading = false,
                    studentId = student.id,
                    studentName = student.name,
                    className = student.className.orEmpty(),
                    students = dummyStudents,
                    attendanceSummary = AttendanceSummaryDto(hadir = 18, sakit = 1, izin = 0, alpa = 0),
                    recentGrades = dummyGrades,
                    activeFees = dummyFees,
                    announcements = dummyAnnouncements,
                    isEmpty = false
                )
            }
        }
    }

    fun refresh(studentId: String) {
        loadDashboard(studentId)
    }
}
