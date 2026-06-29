# List Example

A worked example of the paginated Student List (`Blueprints/StudentBlueprint.md`), demonstrating
`14-LIST-STANDARDS.md` pagination, filtering, and lazy list performance rules together.

## UI State

```kotlin
@Immutable
data class StudentListUiState(
    val students: ImmutableList<StudentRowUiModel> = persistentListOf(),
    val filters: StudentFilters = StudentFilters(),
    val searchQuery: String = "",
    val loadState: LoadState = LoadState.Idle,
    val isRefreshing: Boolean = false,
)
sealed interface LoadState {
    data object Idle : LoadState
    data object LoadingInitial : LoadState
    data object LoadingMore : LoadState
    data class Error(val error: AppError) : LoadState
    data object EndReached : LoadState
}
```

## ViewModel — pagination + filter

```kotlin
class StudentListViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()
    private var currentPage = 0
    private val pageSize = 20

    init { loadPage(reset = true) }

    fun onFilterChanged(filters: StudentFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadPage(reset = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadPage(reset = true) // debounced upstream via snapshotFlow + delay in the Composable layer
    }

    fun onLoadMore() {
        if (_uiState.value.loadState is LoadState.LoadingMore || _uiState.value.loadState is LoadState.EndReached) return
        loadPage(reset = false)
    }

    fun onRefresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadPage(reset = true)
    }

    private fun loadPage(reset: Boolean) {
        if (reset) currentPage = 0
        _uiState.update { it.copy(loadState = if (reset) LoadState.LoadingInitial else LoadState.LoadingMore) }
        viewModelScope.launch {
            val state = _uiState.value
            when (val result = repository.getStudentsPage(state.filters, state.searchQuery, currentPage, pageSize)) {
                is AppResult.Success -> {
                    currentPage++
                    _uiState.update {
                        it.copy(
                            students = if (reset) result.data.toImmutableList() else (it.students + result.data).toImmutableList(),
                            loadState = if (result.data.size < pageSize) LoadState.EndReached else LoadState.Idle,
                            isRefreshing = false,
                        )
                    }
                }
                is AppResult.Failure -> _uiState.update { it.copy(loadState = LoadState.Error(result.error), isRefreshing = false) }
            }
        }
    }
}
```

## Composable — LazyColumn with pagination trigger and footer states

```kotlin
@Composable
fun StudentListContent(state: StudentListUiState, onIntent: (StudentListIntent) -> Unit, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { it.visibleItemsInfo.lastOrNull()?.index to it.totalItemsCount }
            .distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (lastVisible != null && lastVisible >= total - 4) onIntent(StudentListIntent.LoadMore)
            }
    }

    Column(modifier.fillMaxSize()) {
        FilterChipsRow(filters = state.filters, onFilterChanged = { onIntent(StudentListIntent.FilterChanged(it)) })

        if (state.loadState is LoadState.LoadingInitial) {
            repeat(6) { EduOctoSkeleton(Modifier.fillMaxWidth().height(72.dp).padding(vertical = 4.dp)) }
            return@Column
        }
        if (state.students.isEmpty() && state.loadState !is LoadState.LoadingInitial) {
            EduOctoEmptyState(title = "Tidak ada siswa ditemukan", icon = Icons.Outlined.PersonSearch,
                actionLabel = "Reset Filter", onAction = { onIntent(StudentListIntent.ResetFilters) })
            return@Column
        }

        LazyColumn(state = listState, contentPadding = PaddingValues(vertical = EduOctoTheme.spacing.sm)) {
            items(state.students, key = { it.id }) { student ->
                StudentRow(student = student, onClick = { onIntent(StudentListIntent.StudentClicked(student.id)) })
            }
            item {
                when (state.loadState) {
                    is LoadState.LoadingMore -> Box(Modifier.fillMaxWidth().padding(EduOctoTheme.spacing.md), Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                    is LoadState.Error -> EduOctoErrorState(
                        title = "Gagal memuat lebih banyak", description = state.loadState.error.toUserMessage(),
                        onRetry = { onIntent(StudentListIntent.LoadMore) },
                    )
                    else -> Unit // EndReached / Idle: render nothing, no infinite spinner
                }
            }
        }
    }
}
```

This demonstrates: stable `key`, cursor-style pagination via `snapshotFlow` scroll detection (per
`25-PERFORMANCE.md`), distinct loading-more vs. loading-initial states, and the "no infinite spinner
at end of list" rule from `14-LIST-STANDARDS.md`.
