# 19 — Error States & Recovery

## Principle

Every error tells the user (1) what happened, in plain language, (2) whether their data/action was
preserved, and (3) what to do next. Raw exception messages, error codes alone, or a bare "Something
went wrong" are never shown to end users (technical detail can be logged/available via a
"Show details" disclosure for support purposes, never as the primary message).

## Error Categories & Treatment

| Category | Example | UI Pattern |
|---|---|---|
| Network/connectivity | No internet, timeout | Inline banner with retry button; cached data (if any) stays visible per `18-LOADING-STATES.md` |
| Server/API error (5xx) | Backend failure | Inline banner: "Server sedang bermasalah, coba lagi dalam beberapa saat" + retry |
| Validation error (4xx) | Bad input | Inline field error, see `13-FORM-STANDARDS.md` |
| Permission/authorization | Role lacks access | Dedicated state: "Anda tidak memiliki akses ke halaman ini" + link back, never a raw 403 |
| Not found | Deep link to deleted record | Empty-state-style screen: "Data tidak ditemukan, mungkin telah dihapus" |
| Conflict (e.g. double payment submission) | Concurrent edit | Explicit dialog explaining the conflict and the current correct state, asking user to confirm intent |

## Component

```kotlin
@Composable
fun EduOctoErrorState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    retryLabel: String? = "Coba Lagi",
    onRetry: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
)
```

## Placement Rules

1. **Section-level errors stay local.** If only the "recent activity" feed on a dashboard fails to
   load, only that section shows the error — the rest of the dashboard remains fully usable.
2. **Full-screen errors** are reserved for cases where the *entire* screen's data depends on the
   failed call (e.g. a detail screen whose subject failed to load).
3. **Toast/snackbar errors** are used only for errors triggered by a transient action where the user
   remains on the same screen with the same content (e.g. "Gagal mengirim, periksa koneksi Anda" after
   tapping send on a message) — never for errors that invalidate the visible content.

## Retry Behavior

- Retry re-attempts the exact same operation, not a full screen reload, where feasible (e.g. retry
  just the failed network call, preserving scroll position and any other already-loaded sections).
- Exponential backoff is used for automatic background retries (sync), but **user-initiated retry
  (tapping the button) always fires immediately**, never queued behind a backoff timer.

## Logging (pairs with `27-SECURITY.md`)

Every error shown to a user is logged with enough context (request id, endpoint, role, timestamp —
never raw PII like full student data) to be investigated, but the **user-facing message and the log
detail are decoupled** — improving one never requires sacrificing the other.

## Anti-Patterns

- Generic native `Toast.makeText("Error")`-style messages with no recovery path.
- Crashing or showing a white/blank screen on an unhandled exception — every screen must have an
  error boundary that falls back to `EduOctoErrorState`.
- Silently swallowing an error and leaving stale/incorrect data on screen with no indication it
  failed to refresh.
