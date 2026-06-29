# 25 — Performance Standards

## Recomposition

1. UI models passed to Composables are `@Immutable`/`@Stable` data classes using stable collection
   wrappers (`kotlinx.collections.immutable`'s `ImmutableList`/`persistentListOf`), never raw
   `List<T>` (unstable to the Compose compiler) for anything bound into a `LazyColumn` `items()`.
2. Use the Compose compiler's stability reports (`-P plugin:androidx.compose.compiler...:reportsDestination`)
   during development to catch unexpectedly unstable parameters before merging a feature.
3. Hoist `remember`/`derivedStateOf` for any computation more expensive than a simple field access,
   especially inside list item Composables that recompose frequently during scroll.
4. Avoid passing new lambda instances on every parent recomposition into deeply nested children —
   prefer method references or `remember { }`-wrapped lambdas where the capture set is stable.

## Lazy Lists

- Always supply `key`. Always supply `contentType` when a `LazyColumn` mixes item shapes (e.g.
  header rows + data rows) so Compose can reuse layout nodes correctly.
- Avoid `Modifier.animateContentSize()` on items inside large lists — prefer `AnimatedVisibility`
  with bounded, predictable animation specs (see `09-ANIMATION.md`).
- Images inside list rows are loaded through a memory+disk caching image loader (Coil
  Multiplatform or equivalent) with explicit `size()`/`scale()` constraints — never load
  full-resolution images into small avatar slots.

## Memory

- SQLDelight queries are paginated (`LIMIT`/`OFFSET` or cursor) for any table that can grow large
  (students, payments, audit logs) — never `SELECT *` with no bound on a table expected to exceed a
  few hundred rows over the school's lifetime.
- Large one-off objects (e.g. a generated PDF report card) are streamed/written to disk, not held
  fully in memory as a `ByteArray` for the app's lifetime.

## Cold Start

- `app/` module's startup path does the minimum necessary before first frame: DI graph assembly,
  theme setup, and a lightweight session check (cached locally, not a blocking network call) — any
  network-dependent initialization happens after first frame, with the splash/loading state handling
  the gap per `18-LOADING-STATES.md`.
- Avoid eager initialization of feature modules' DI graphs not needed for the first screen — lazy-init
  per feature where the DI framework supports it.

## Derived State & SnapshotFlow

- Use `snapshotFlow {}` to bridge Compose state into coroutine-based side effects (e.g. reacting to
  scroll position to trigger pagination) rather than polling state in a loop.
- Prefer `derivedStateOf` over `remember(state) { compute(state) }` when the derivation reads
  multiple snapshot states and should only recompute when the *derived* value actually changes (e.g.
  "is scrolled past threshold" derived from raw scroll offset).

## Rendering & Animation Performance

- Animations defined per `09-ANIMATION.md` use hardware-accelerated properties (`graphicsLayer`
  scale/alpha/translation) rather than animating layout-affecting properties (width/height/padding)
  where a visual-only effect is sufficient.
- Glass blur (`08-GLASSMORPHISM.md`) is capped to surfaces that are not scrolling/animating
  simultaneously at high frequency — avoid applying real-time blur to a fast-scrolling list's
  background.

## Benchmarking Expectations

- Cold start to first meaningful content: target < 1.5s on a mid-range Android device.
- List scroll: target sustained 60fps (no dropped-frame jank) for lists up to a few hundred loaded
  items with pagination.
- Any feature introducing a new heavy computation (report card PDF generation, analytics
  aggregation) runs off the main/UI dispatcher, never blocking the render thread.

## Anti-Patterns

- `Modifier.recompositionHighlighter`-worthy components left unaddressed (frequent unnecessary
  recomposition discovered via Layout Inspector / Compose tooling and not fixed before merging).
- Network calls or disk I/O inside a Composable body (always go through ViewModel/Repository).
- Unbounded `LazyColumn` over an unpaginated full table fetch "because it's simpler."
