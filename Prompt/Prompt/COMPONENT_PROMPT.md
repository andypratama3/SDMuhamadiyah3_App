# COMPONENT_PROMPT

Use this template when asking the agent to build or extend a shared design-system component.

---

```
Build/extend the [COMPONENT NAME] component.

Context to read first:
- /AI-EDUOCTO-MASTER/07-COMPONENT-SYSTEM.md (anatomy and composition rules)
- /AI-EDUOCTO-MASTER/20-COMPONENT-LIBRARY.md (check whether this already exists or should extend
  an existing entry — do not duplicate)
- /AI-EDUOCTO-MASTER/03 through 06 (tokens: design system, color, typography, spacing)
- /AI-EDUOCTO-MASTER/08-GLASSMORPHISM.md and 09-ANIMATION.md if this component has elevation/motion
- /AI-EDUOCTO-MASTER/26-ACCESSIBILITY.md

Before writing code, confirm:
1. Why this component doesn't already exist / why an existing one can't be extended.
2. Its required states per `20-COMPONENT-LIBRARY.md`'s table format (list them explicitly).

Deliver, complete (no placeholders):
1. The full Composable function, slot-based API per `07-COMPONENT-SYSTEM.md`, consuming
   `EduOctoTheme` tokens exclusively (no raw hex/dp/sp values).
2. Every required state implemented (default, pressed/focused, disabled, loading/error if
   applicable).
3. `@Preview` functions covering: default, long-text overflow, every state variant, and a dark-
   background context for forward dark-mode compatibility.
4. Accessibility semantics (content descriptions, touch target sizing, live regions if applicable).
5. An updated row for /AI-EDUOCTO-MASTER/20-COMPONENT-LIBRARY.md adding/updating this component's
   entry.

Self-check against /AI-EDUOCTO-MASTER/Checklists/DESIGN_SYSTEM_CHECKLIST.md before presenting.
```
