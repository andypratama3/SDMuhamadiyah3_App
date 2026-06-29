# PERFORMANCE_PROMPT

Use this template when asking the agent to investigate or improve performance.

---

```
Investigate/improve performance of [SCREEN/FLOW/QUERY].

Context to read first:
- /AI-EDUOCTO-MASTER/25-PERFORMANCE.md (the full set of standards to check against)
- /AI-EDUOCTO-MASTER/22-COMPOSE-STANDARDS.md (recomposition/stability rules)
- /AI-EDUOCTO-MASTER/21-KMP-STANDARDS.md (offline-first data flow, dispatcher rules)

Diagnose, in order:
1. Recomposition — are UI models `@Immutable`/`@Stable`? Are stable collection types used in lists?
   Are unnecessary lambdas/objects created on every recomposition?
2. Data layer — is the query paginated? Is the UI reading through SQLDelight (offline-first) rather
   than blocking on network? Are dispatchers correctly assigned (no main-thread I/O)?
3. List/grid specifics — are stable `key`s and `contentType`s supplied? Are images size-constrained
   and cached?
4. Cold start — is anything network-dependent blocking first frame unnecessarily?
5. Animation — are hardware-accelerated properties used (graphicsLayer) rather than layout-affecting
   ones for purely visual animations?

Report findings as a table: [Issue, Location, Standard Violated (cite the doc section), Fix].

Then implement the fixes, smallest safe increments, confirming no behavior change beyond the
performance characteristic being fixed (pair with `29-REFACTORING-RULES.md` if the fix is structural).

Report before/after: what was measured or reasoned about (recomposition count, query plan, frame
timing) to justify the fix, even if only qualitative reasoning was possible in this environment.
```
