package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import kotlinx.coroutines.delay

data class HalamanRaporUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val studentId: String = "",
    val rapors: List<RaporInstanceDto> = emptyList()
) : ScreenState

class HalamanRaporViewModel : BaseViewModel<HalamanRaporUiState>(HalamanRaporUiState()) {

    fun loadRapors(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
            delay(1000)
            val dummyRapors = listOf(
                RaporInstanceDto(id = "1", studentId = studentId, semester = "Ganjil (1)", academicYear = "2023 / 2024", status = "available", pdfUrl = "https://example.com/rapor.pdf"),
                RaporInstanceDto(id = "2", studentId = studentId, semester = "Genap (2)", academicYear = "2022 / 2023", status = "available", pdfUrl = "https://example.com/rapor.pdf")
            )
            updateState {
                it.copy(
                    rapors = dummyRapors,
                    isLoading = false,
                    isEmpty = dummyRapors.isEmpty()
                )
            }
        }
    }

    fun refresh(studentId: String) {
        loadRapors(studentId)
    }
}
