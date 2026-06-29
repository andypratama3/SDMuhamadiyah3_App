# 10 — Haptics

## Principle

Haptic feedback confirms an action was registered, especially when visual feedback alone might be
missed (e.g. quick taps, swipe gestures). EduOcto uses haptics sparingly and consistently — never as
decoration, only as confirmation.

## KMP Abstraction

Define a common interface in `commonMain`, implemented per platform:

```kotlin
// commonMain
interface HapticFeedback {
    fun light()      // selection, toggle, minor confirmation
    fun medium()     // successful submit, item added/removed
    fun success()    // payment confirmed, approval completed
    fun warning()    // validation error, destructive confirmation prompt
    fun error()      // failed action, rejected submission
}

expect fun provideHapticFeedback(): HapticFeedback
```

```kotlin
// androidMain — uses HapticFeedbackConstants / Vibrator
actual fun provideHapticFeedback(): HapticFeedback = AndroidHapticFeedback(applicationContext)

// iosMain — uses UIImpactFeedbackGenerator / UINotificationFeedbackGenerator
actual fun provideHapticFeedback(): HapticFeedback = IosHapticFeedback()

// desktopMain — no-op (desktop has no haptic hardware in scope)
actual fun provideHapticFeedback(): HapticFeedback = NoOpHapticFeedback()
```

## Mapping Table

| Interaction | Haptic | Notes |
|---|---|---|
| Toggle a switch/checkbox | `light()` | |
| Select a chip/tab | `light()` | |
| Swipe-to-reveal action threshold crossed | `light()` | At the moment the action becomes "armed" |
| Submit a form successfully | `medium()` | |
| Mark attendance for a student | `light()` | Per-row; avoid spamming on bulk operations (see below) |
| Payment confirmed | `success()` | Paired with a success toast/animation |
| PPDB application approved | `success()` | |
| Validation error on submit | `warning()` | |
| Destructive action confirmation (delete student record) | `warning()` | On the confirm dialog's destructive button |
| Network/server error | `error()` | Paired with `19-ERROR-STATES.md` pattern |

## Rules

1. **Never fire haptics in a tight loop.** Bulk operations (e.g. marking 30 students present at once
   via "mark all") fire **one** haptic for the batch action, not one per row.
2. **Haptics always pair with a visual change.** Never the only signal that something happened.
3. **Respect system-level haptic settings.** If the OS reports haptics disabled/reduced, the
   `HapticFeedback` implementation must no-op silently — never crash or attempt to force it.
4. **Desktop is haptics-silent by design** — do not attempt workarounds; rely on visual + audio cues
   only on Desktop targets.
