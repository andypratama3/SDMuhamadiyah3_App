# ACCESSIBILITY_PROMPT

Use this template when asking the agent to audit or fix accessibility issues.

---

```
Audit/fix accessibility for [SCREEN/COMPONENT/FLOW].

Context to read first:
- /AI-EDUOCTO-MASTER/26-ACCESSIBILITY.md (the full standard and verification procedure)
- /AI-EDUOCTO-MASTER/04-COLOR-SYSTEM.md (contrast requirements)
- /AI-EDUOCTO-MASTER/13-FORM-STANDARDS.md if this is a form (error announcement requirements)

Walk the verification procedure from `26-ACCESSIBILITY.md` explicitly:
1. Contrast — check every text/background and icon/background pairing against the 4.5:1 / 3:1
   thresholds. List any failures with the specific token pair and measured/estimated ratio.
2. Semantics — confirm every interactive element has a meaningful contentDescription; confirm
   decorative elements explicitly null out their description; confirm headings use semantic roles.
3. Touch targets — confirm every interactive element meets the 48x48dp minimum.
4. Keyboard navigation (Desktop) — confirm full tab-order coverage and visible focus indicators.
5. Dynamic type — confirm the layout doesn't clip/overlap at 130% system font scale.
6. Motion — confirm reduced-motion preference is respected with an equivalent state-change signal.
7. Live regions — confirm errors/status changes are announced via liveRegion semantics.

Report findings as a table: [Issue, Location, WCAG criterion, Fix].

Implement fixes directly in the component/screen — accessibility fixes are not deferred to a future
pass. Re-run the checklist mentally after fixing and confirm clean status.
```
