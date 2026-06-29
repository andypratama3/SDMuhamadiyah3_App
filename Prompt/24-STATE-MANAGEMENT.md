# 24 — State Management (MVI on top of MVVM)

## Model

EduOcto uses **MVVM structurally** (ViewModel exposes state to Compose) with an **MVI-shaped
contract** inside each ViewModel: a single immutable `UiState`, a sealed set of `UiEvent`/`Intent`
inputs, and one-shot `UiEffect`s for navigation/snackbar triggers.

```kotlin
@Immutable
data class StudentListUiState(
    val isLoading: Boolean = false,
    val students: ImmutableList<StudentUiModel> = persistentListOf(),
    val searchQuery: String = "",
    val error: AppError? = null,
    val isRefreshing: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && students.isEmpty() && error == null
}

sealed interface StudentListIntent {
    data class SearchQueryChanged(val query: String) : StudentListIntent
    data object Refresh : StudentListIntent
    data object Retry : StudentListIntent
    data class StudentClicked(val studentId: String) : StudentListIntent
}

sealed interface StudentListEffect {
    data class NavigateToDetail(val studentId: String) : StudentListEffect
    data class ShowSnackbar(val message: String) : StudentListEffect
}

class StudentListViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<StudentListEffect>(Channel.BUFFERED)
    val effect: Flow<StudentListEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: StudentListIntent) {
        when (intent) {
            is StudentListIntent.SearchQueryChanged -> handleSearch(intent.query)
            is StudentListIntent.Refresh -> refresh()
            is StudentListIntent.Retry -> refresh()
            is StudentListIntent.StudentClicked ->
                viewModelScope.launch { _effect.send(StudentListEffect.NavigateToDetail(intent.studentId)) }
        }
    }
    // ...
}
```

## Rules

1. **`UiState` is a single immutable data class per screen.** No scattered separate `mutableStateOf`
   fields exposed individually from a ViewModel — one state object, one `StateFlow`.
2. **Effects are one-shot, never re-delivered on rotation/recomposition.** Use a `Channel`
   (`receiveAsFlow()`), not a `StateFlow`, for navigation/snackbar triggers — a `StateFlow` would
   re-fire the last effect on every new collector (e.g. after process restart/config change).
3. **No two-way binding shortcuts.** Compose UI never mutates ViewModel state directly
   (`viewModel.state.value = ...`); it always dispatches an intent/event, and the ViewModel is the
   only writer of its own state.
4. **Loading/Error/Empty/Success are derived from one state shape**, not four different sealed
   "screen state" variants that lose previously-loaded data on transition — e.g. `isRefreshing` lets
   the UI show existing data *and* a refresh indicator simultaneously, instead of replacing content
   with a spinner.
5. **Side effects belong only in the ViewModel** (or UseCase it calls), never inside a Composable's
   body outside of `LaunchedEffect` scoped strictly to collecting the ViewModel's `effect` flow.

## Saved State

Any state that must survive process death (e.g. a partially-filled PPDB form, see `ERP/PPDB.md`)
uses the platform's `SavedStateHandle`-equivalent or persists as a local draft via SQLDelight —
never relies solely on in-memory `ViewModel` state, since iOS/Desktop/Android all have different
process lifecycle guarantees.

## Cross-Screen / Shared State

App-wide state (current logged-in user/role, theme, locale) lives in a single `AppSessionState`
holder injected via DI, exposed as a `StateFlow`, observed by any screen that needs it — never
duplicated/recreated per screen.

## Anti-Patterns

- Exposing `MutableStateFlow` publicly from a ViewModel (always expose the read-only `StateFlow`).
- Combining unrelated screens' state into one mega-ViewModel "to share code" — extract a shared
  UseCase/Repository instead.
- Using `LiveData` — EduOcto is multiplatform; `StateFlow`/`Flow` only.
