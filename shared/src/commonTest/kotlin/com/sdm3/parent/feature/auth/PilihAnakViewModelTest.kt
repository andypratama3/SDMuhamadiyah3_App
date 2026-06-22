package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.test.TestDispatcher
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.data.repository.StudentRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeStudentRepository : StudentRepository(api = null) {
    var studentsResult: ApiResult<List<StudentDto>> = ApiResult.Success(
        listOf(
            StudentDto(
                id = "1",
                name = "Ahmad Fathan",
                nisn = "0012345678",
                gender = "L",
                birthPlace = "Samarinda",
                birthDate = "2015-01-15",
                className = "Kelas 4-A"
            )
        )
    )

    override suspend fun getStudents(): ApiResult<List<StudentDto>> = studentsResult
}

class PilihAnakViewModelTest : TestDispatcher() {

    @Test
    fun initialLoadingFetchesStudentsAndUpdatesState() {
        val repo = FakeStudentRepository()
        val viewModel = PilihAnakViewModel(repo)

        // Run current to execute init block loadStudents() coroutine
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
        assertEquals(1, state.students.size)
        assertEquals("Ahmad Fathan", state.students[0].name)
        assertNull(state.errorMessage)
    }

    @Test
    fun loadStudentsErrorUpdatesErrorMessage() {
        val repo = FakeStudentRepository()
        repo.studentsResult = ApiResult.Error(ApiError.Unknown("Error fetching data"))
        val viewModel = PilihAnakViewModel(repo)

        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Terjadi kesalahan. Silakan coba lagi.", state.errorMessage)
    }

    @Test
    fun selectStudentUpdatesSelectedStudentId() {
        val repo = FakeStudentRepository()
        val viewModel = PilihAnakViewModel(repo)

        testDispatcher.scheduler.runCurrent()

        viewModel.selectStudent("1")
        assertEquals("1", viewModel.uiState.value.selectedStudentId)
    }
}
