# BUGFIX_PROMPT

Use this template when asking the agent to diagnose and fix a defect.

---

```
Fix the following bug: [DESCRIPTION / REPRO STEPS / ERROR MESSAGE]

Context to read first:
- /AI-EDUOCTO-MASTER/ERP/[RELEVANT MODULE].md if the bug touches business logic, to confirm the
  *intended* behavior before assuming the code or the bug report is correct.
- /AI-EDUOCTO-MASTER/23-CLEAN-ARCHITECTURE.md to locate which layer the root cause likely lives in.
- /AI-EDUOCTO-MASTER/27-SECURITY.md if the bug involves financial data, permissions, or PII —
  treat with elevated rigor regardless of how minor it seems.

Before fixing:
1. Reproduce/trace the root cause — identify the exact layer (presentation/domain/data) and the
   exact business rule (if any) being violated. Do not patch a symptom in the UI layer if the root
   cause is in domain logic.
2. State the root cause in one or two sentences.
3. If the bug reveals a gap in this kit's documented business rules (the rule was ambiguous or
   missing), flag it — propose the documentation fix alongside the code fix.

Deliver:
1. The minimal, correct fix at the root-cause layer.
2. A regression test that fails before the fix and passes after.
3. Confirmation the fix doesn't violate any permission matrix or business rule in the relevant
   ERP/*.md file.
4. If financial/attendance/grade data was affected by the bug historically, flag whether a data
   correction/backfill is needed (do not silently leave corrupted historical data uncorrected without
   raising it).
```
