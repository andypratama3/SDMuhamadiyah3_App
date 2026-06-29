# Dashboard Example

A worked example for the Finance Staff dashboard hero section, demonstrating
`Blueprints/DashboardBlueprint.md` and `12-DASHBOARD-STANDARDS.md` applied concretely, including the
per-section independent loading pattern.

## UI State

```kotlin
@Immutable
data class FinanceDashboardUiState(
    val heroState: SectionState<HeroStat> = SectionState.Loading,
    val secondaryStats: SectionState<ImmutableList<SecondaryStat>> = SectionState.Loading,
    val actionableItems: SectionState<ImmutableList<ActionableItem>> = SectionState.Loading,
    val recentActivity: SectionState<ImmutableList<ActivityEvent>> = SectionState.Loading,
)

sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Loaded<T>(val data: T) : SectionState<T>
    data class Failed(val error: AppError) : SectionState<Nothing>
}
```

Each dashboard section is independently `Loading`/`Loaded`/`Failed` — this is the concrete
implementation of the `12-DASHBOARD-STANDARDS.md` rule that one slow/failed section never blocks the
rest of the screen.

## ViewModel (fetch independence)

```kotlin
class FinanceDashboardViewModel(
    private val calculateOutstandingSpp: CalculateOutstandingSppUseCase,
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FinanceDashboardUiState())
    val uiState: StateFlow<FinanceDashboardUiState> = _uiState.asStateFlow()

    init {
        loadHero()
        loadSecondaryStats()
        loadActionableItems()
        loadRecentActivity()
    }

    private fun loadHero() = viewModelScope.launch {
        runCatching { calculateOutstandingSpp.totalForSchool() }
            .onSuccess { stat ->
                _uiState.update { it.copy(heroState = SectionState.Loaded(
                    HeroStat("Total SPP Tertunggak Bulan Ini", formatCurrencyIdr(stat), trend = null)
                )) }
            }
            .onFailure { e -> _uiState.update { it.copy(heroState = SectionState.Failed(e.toAppError())) } }
    }

    private fun loadSecondaryStats() = viewModelScope.launch {
        dashboardRepository.observeFinanceSecondaryStats()
            .map<ImmutableList<SecondaryStat>, FinanceDashboardUiState> { stats ->
                _uiState.value.copy(secondaryStats = SectionState.Loaded(stats))
            }
            .catch { e -> emit(_uiState.value.copy(secondaryStats = SectionState.Failed(e.toAppError()))) }
            .collect { newState -> _uiState.value = newState }
    }
    // loadActionableItems() / loadRecentActivity() follow the identical independent pattern
}
```

## Composable (hero section only, illustrating section-level state handling)

```kotlin
@Composable
fun HeroStatSection(state: SectionState<HeroStat>, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Box(Modifier.padding(EduOctoTheme.spacing.lg)) {
            when (state) {
                is SectionState.Loading -> EduOctoSkeleton(Modifier.fillMaxWidth().height(64.dp))
                is SectionState.Loaded -> Column {
                    Text(state.data.label, style = EduOctoTheme.typography.bodyMedium, color = EduOctoTheme.colors.onSurfaceMuted)
                    Text(state.data.value, style = EduOctoTheme.typography.displayMedium, color = EduOctoTheme.colors.onSurface)
                }
                is SectionState.Failed -> EduOctoErrorState(
                    title = "Gagal memuat data",
                    description = state.error.toUserMessage(),
                    onRetry = onRetry,
                )
            }
        }
    }
}
```

## Preview Coverage

```kotlin
@Preview @Composable private fun HeroLoadingPreview() = EduOctoTheme { HeroStatSection(SectionState.Loading, {}) }
@Preview @Composable private fun HeroLoadedPreview() = EduOctoTheme {
    HeroStatSection(SectionState.Loaded(HeroStat("Total SPP Tertunggak Bulan Ini", "Rp42.500.000")), {})
}
@Preview @Composable private fun HeroErrorPreview() = EduOctoTheme {
    HeroStatSection(SectionState.Failed(AppError.NoConnection), {})
}
```

This demonstrates the independent-section loading pattern that the full Dashboard screen (covering
all four sections: hero, secondary stats, actionable items, recent activity) extends identically.
