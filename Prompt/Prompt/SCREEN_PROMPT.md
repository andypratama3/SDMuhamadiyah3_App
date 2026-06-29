# SCREEN_PROMPT

Use this template when asking the agent to build a complete screen.

---

```
Build the [SCREEN NAME] screen for the [ROLE(S)] in the [MODULE] module.

Context to read first:
- /AI-EDUOCTO-MASTER/ERP/[MODULE].md — business rules and permission matrix for this screen
- /AI-EDUOCTO-MASTER/Blueprints/[MODULE]Blueprint.md if one exists for this screen type
- /AI-EDUOCTO-MASTER/12 through /19 (Dashboard/Form/List/Table/Dialog/Empty/Loading/Error standards)
  — whichever apply to this screen's content type
- /AI-EDUOCTO-MASTER/21-KMP-STANDARDS.md, 22-COMPOSE-STANDARDS.md, 23-CLEAN-ARCHITECTURE.md,
  24-STATE-MANAGEMENT.md

Deliver, in this order, complete (no placeholders):
1. A one-paragraph design rationale: which of the five questions (`02-DESIGN-PHILOSOPHY.md`) this
   screen answers and how.
2. Domain model references used (reuse from ERP/[MODULE].md — do not invent new fields without
   flagging it).
3. Repository interface method(s) needed (or confirm existing ones suffice).
4. `[Screen]UiState`, `[Screen]Intent`, `[Screen]Effect` sealed/data definitions.
5. `[Screen]ViewModel` — full implementation, including how it determines what's visible per the
   permission matrix for [ROLE(S)].
6. `[Screen]Content` — pure, stateless Composable covering loading, empty, error, and success states.
7. `[Screen]Screen` — thin wiring Composable.
8. At least 4 `@Preview` functions: default/success, loading, empty, error.
9. A short note confirming Android/iOS/Desktop parity, or flagging any platform-specific gap.

Self-check against /AI-EDUOCTO-MASTER/Checklists/SCREEN_CHECKLIST.md before presenting the result,
and report the checklist result.
```

---

## Example Fill-In

> Build the **Record Payment** screen for **Finance Staff** in the **Payment** module.
