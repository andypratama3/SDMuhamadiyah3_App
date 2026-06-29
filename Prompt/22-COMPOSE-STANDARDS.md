# 22 — Compose Multiplatform Standards

## Composable Function Rules

1. **Stateless by default.** A Composable representing a piece of UI should be a pure function of
   its parameters wherever possible (`@Composable fun StudentCard(student: StudentUiModel, onClick: () -> Unit)`).
   State is hoisted to the nearest screen-level `@Composable` or its ViewModel.
2. **Screen-level Composables wire state**, e.g.:

```kotlin
@Composable
fun StudentListScreen(
    viewModel: StudentListViewModel = koinViewModel(),
    onStudentClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StudentListContent(
        state = state,
        onStudentClick = onStudentClick,
        onRetry = viewModel::onRetry,
        onSearchQueryChange = viewModel::onSearchQueryChange,
    )
}

@Composable
private fun StudentListContent(
    state: StudentListUiState,
    onStudentClick: (String) -> Unit,
    onRetry: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) { /* pure UI, easily previewable */ }
```

`StudentListContent` is the one previewed and tested in isolation; `StudentListScreen` is the thin
wiring layer.

3. **Stability matters.** All UI model data classes passed into Composables are `@Immutable` or
   `@Stable` with `val` properties and stable collection types (`ImmutableList` via kotlinx or a
   wrapped stable list type) to avoid unnecessary recomposition. See `25-PERFORMANCE.md`.
4. **Modifier contract:** every public Composable's first optional parameter is
   `modifier: Modifier = Modifier`, applied to the outermost layout node, and never consumed
   conditionally in a way that silently drops it.
5. **No business logic inside Composables.** Calculations beyond trivial display formatting
   (currency formatting via a shared `formatCurrency()`, date formatting) belong in the ViewModel or
   domain layer, not inline in a `@Composable`.

## Recomposition Hygiene

- Wrap expensive computed values in `remember`/`derivedStateOf` rather than recomputing on every
  recomposition.
- Pass lambdas that don't capture changing state as stable references (member function references
  `viewModel::onRetry`) rather than fresh lambdas created inline where avoidable in hot paths
  (large lists).
- Use `key()` inside conditional branches/loops where Compose might otherwise misattribute state
  across swapped content.

## Resource Handling

- All strings via Compose Multiplatform resources (`stringResource(Res.string.xxx)`) — no hardcoded
  user-facing text, ever, even temporarily. Default resource file is `id` (Bahasa Indonesia);
  `en` is the secondary resource file. See `01-PROJECT-CONTEXT.md`.
- All images/icons via `composeResources` (vector preferred) — no platform-specific drawable
  duplication when a shared vector will do.

## Previews

Every screen-level Composable ships with at least:
- A default-state preview with realistic sample data (never literally "Lorem ipsum" for a school
  ERP — use plausible Indonesian names/values).
- A loading-state preview.
- An empty-state preview (where applicable).
- An error-state preview (where applicable).

## Testing

- Pure `*Content` Composables (per the wiring pattern above) are tested via Compose Multiplatform
  UI testing (`createComposeRule`) asserting on semantics, not pixel snapshots, as the primary method
  — pixel/screenshot tests are a supplementary, not primary, verification tool.
- ViewModels are tested in `commonTest` with fake repositories — no Compose dependency needed for
  business logic tests.

## Anti-Patterns

- `LaunchedEffect(Unit) { /* fetch data */ }` directly inside a deeply nested Composable instead of
  the screen-level ViewModel owning the data load.
- Mutable `var` properties on classes passed as Composable parameters (breaks stability inference).
- Conditionally calling a Composable function from regular Kotlin control flow without `key()` where
  identity matters (causes state loss/leak across recompositions).
