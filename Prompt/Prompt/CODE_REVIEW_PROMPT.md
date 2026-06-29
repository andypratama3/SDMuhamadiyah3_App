# CODE_REVIEW_PROMPT

Use this template when asking the agent to review a change (its own or a human's) before merge.

---

```
Review the following change: [DIFF / FILE(S) / PR DESCRIPTION]

Context to read first:
- /AI-EDUOCTO-MASTER/00-AI-CONSTITUTION.md
- /AI-EDUOCTO-MASTER/28-CODING-STANDARDS.md
- /AI-EDUOCTO-MASTER/23-CLEAN-ARCHITECTURE.md and 24-STATE-MANAGEMENT.md
- The relevant /AI-EDUOCTO-MASTER/ERP/*.md for business-rule correctness
- /AI-EDUOCTO-MASTER/Checklists/SCREEN_CHECKLIST.md, UI_CHECKLIST.md, PERFORMANCE_CHECKLIST.md,
  DESIGN_SYSTEM_CHECKLIST.md as applicable to what changed

Review against, explicitly, in this order:
1. **Correctness** — does it implement the business rule exactly as documented in the relevant
   ERP/*.md? Any deviation must be called out, not assumed acceptable.
2. **Architecture** — does it respect layer boundaries (`23-CLEAN-ARCHITECTURE.md`)? Any leaked
   Ktor/SQLDelight types into presentation/domain?
3. **State management** — single immutable UiState? One-shot effects via Channel, not StateFlow?
4. **Design system compliance** — tokens only, no raw hex/dp/sp; reuses existing components per
   `20-COMPONENT-LIBRARY.md` rather than reinventing?
5. **Completeness** — loading/empty/error/success states all present? All three platforms
   considered? No forbidden patterns from `30-FULL-OUTPUT-ENFORCEMENT.md`?
6. **Accessibility** — semantics, contrast, touch targets per `26-ACCESSIBILITY.md`?
7. **Security** — permission checks present and matching the relevant permission matrix? Audit
   logging present for any action requiring it per `27-SECURITY.md`?
8. **Tests** — adequate coverage for new business logic, especially anything in `domain`?

Output format: a list of findings, each tagged [Blocking] or [Suggestion], with the specific doc
section violated and a concrete fix. End with a clear verdict: Approve / Approve with suggestions /
Request changes (blocking issues present).
```
