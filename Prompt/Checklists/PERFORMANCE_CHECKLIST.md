# Performance Checklist

Run this before merging any change touching lists, data loading, or animation.

## Compose Stability
- [ ] UI models passed into Composables are `@Immutable`/`@Stable`
- [ ] Lists bound to `LazyColumn`/`LazyVerticalGrid` use stable collection types
      (`ImmutableList`/`persistentListOf`), not raw `List<T>`
- [ ] No unnecessary lambda/object allocation on every recomposition in hot paths

## Lists
- [ ] Stable `key` supplied to every `items()` call
- [ ] `contentType` supplied where item shapes vary
- [ ] Images are size-constrained and loaded via the shared caching image loader
- [ ] Pagination implemented for any list that could exceed ~100 rows in realistic use

## Data Layer
- [ ] UI reads through SQLDelight (`Flow`), not directly from network responses
      (`21-KMP-STANDARDS.md` offline-first rule)
- [ ] Queries on large tables (students, payments, audit logs) are paginated/bounded, no
      unbounded `SELECT *`
- [ ] No network or disk I/O on the main/UI dispatcher

## Cold Start
- [ ] Nothing network-dependent blocks first frame
- [ ] DI graph initialization for unrelated feature modules is not eagerly triggered at startup

## Animation
- [ ] Animations use hardware-accelerated properties (`graphicsLayer`) where purely visual
- [ ] No animation loops indefinitely except legitimate loading indicators
- [ ] No animation blocks interaction longer than `durationSlow` (400ms)

## Verification
- [ ] Manually scrolled the affected list/screen and confirmed no visible jank
- [ ] Compose compiler stability report checked (if tooling available) with no new unstable
      parameters introduced
