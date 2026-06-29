# REFACTOR_PROMPT

Use this template when asking the agent to refactor existing code.

---

```
Refactor [FILE/MODULE/FEATURE].

Context to read first:
- /AI-EDUOCTO-MASTER/29-REFACTORING-RULES.md (when/how refactors are justified and executed safely)
- /AI-EDUOCTO-MASTER/23-CLEAN-ARCHITECTURE.md and 24-STATE-MANAGEMENT.md (target architecture shape)
- /AI-EDUOCTO-MASTER/28-CODING-STANDARDS.md

Before touching code:
1. State which of the four justified reasons from `29-REFACTORING-RULES.md` applies here. If none
   clearly applies, say so and ask whether to proceed anyway (speculative refactors are discouraged).
2. Identify whether existing test coverage is sufficient to verify behavior-preservation; if not,
   write characterization tests first.
3. Describe the target end-state structure before changing anything.

Execute the refactor:
1. In the smallest safe increments, each independently verifiable.
2. Behavior-preserving — all existing tests pass unchanged in their assertions.
3. Do not introduce new features in the same change.
4. Update any /AI-EDUOCTO-MASTER doc whose documented pattern this refactor changes.

Report at the end:
- What changed and why (the justified reason).
- Confirmation that behavior is unchanged (tests, or manual verification steps taken).
- Any follow-up refactor opportunities noticed but deliberately deferred (with reason).
```
