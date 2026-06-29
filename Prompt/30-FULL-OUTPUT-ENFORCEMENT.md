# 30 — Full Output Enforcement (Anti-Laziness Contract)

## Purpose

This document exists specifically to constrain AI coding agents (OpenCode and others) working on
EduOcto against the most common failure mode of AI-generated code: confident-looking but incomplete
output. It is referenced by `00-AI-CONSTITUTION.md` and is binding on every code-generation task.

## Forbidden Patterns

An agent must never produce any of the following, under any framing (including "to save time," "for
brevity," "as a starting point"):

- `// TODO`, `// FIXME`, `// implement this`, `// ...rest of the code`
- `// implementation omitted for brevity`
- Function bodies that just `throw NotImplementedError()` or `TODO()` presented as finished work
- Truncated file output ending mid-function with no explanation
- A claim that something is "done" when a required state (loading/empty/error per
  `18-LOADING-STATES.md`/`17-EMPTY-STATES.md`/`19-ERROR-STATES.md`) was skipped
- Silently reducing scope (e.g. asked for Android+iOS+Desktop parity, delivering Android-only code
  with no mention of the gap)
- Inventing API signatures, dependency versions, or file paths that weren't established elsewhere in
  this kit or the actual codebase, without flagging the assumption

## Required Behavior Instead

1. **If a task is large, decompose and say so explicitly** ("This screen requires: ViewModel,
   UiState, Content Composable, Screen wiring, 4 preview states, and a Repository method — producing
   each in full below, in that order.") — then actually produce all of it, not a subset.
2. **If something is genuinely out of scope for the current step** (e.g. backend endpoint doesn't
   exist yet), say so explicitly and provide a clearly-marked **interim implementation** (e.g. an
   in-memory/fake `Repository` implementing the real interface) rather than a stub that silently
   does nothing — the fake must behave correctly enough to develop the UI against.
3. **If a request is ambiguous, state the assumption made and proceed** — per the broader assistant
   behavior of resolving ambiguity with a stated default rather than stalling — but the assumption
   must be visible, not buried.
4. **Long files are built section by section but always delivered complete** — splitting work into
   multiple tool calls/messages is fine; delivering an incomplete final artifact is not.

## Self-Check Before Declaring a Task Complete

Before saying a feature/screen/file is done, verify:

- [ ] No forbidden pattern from the list above is present anywhere in the output.
- [ ] All four UI states (loading, empty, error, success) are handled for any data-driven screen.
- [ ] All three active platforms (Android, iOS, Desktop) are accounted for, or the gap is explicitly
      flagged with a reason.
- [ ] Relevant checklist(s) in `Checklists/` have been mentally run, and any failing item is either
      fixed or explicitly called out as a known gap with a reason.
- [ ] Anything touching Finance, PPDB, or Attendance has the accuracy/audit rigor expected for
      production financial/academic-record software, not prototype-quality shortcuts.

## Escalation Instead of Silent Shortcuts

If meeting the full standard genuinely isn't possible in the current step (e.g. a dependency is
missing, a backend contract is undefined), the agent states this plainly, proposes the most
defensible interim approach, and clearly labels it as interim — it does not pretend the shortcut is
the final, production-ready answer.
