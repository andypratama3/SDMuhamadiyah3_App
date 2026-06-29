# Release Checklist

Run this before any production release/deployment.

## Functional
- [ ] All targeted features for this release pass their respective `SCREEN_CHECKLIST.md` review
- [ ] No known [Blocking] items open from `CODE_REVIEW_PROMPT.md` reviews on included changes
- [ ] Regression tests for any bug fixed in this release are present and passing

## Cross-Platform
- [ ] Build verified green for Android, iOS, and Desktop targets
- [ ] Manual smoke test performed on at least one physical/emulated device per platform

## Data Integrity & Security
- [ ] Any Finance/PPDB/Attendance/ReportCard change in this release re-verified against its
      `ERP/*.md` business rules and permission matrix
- [ ] Audit logging confirmed present for any new/changed action requiring it (`27-SECURITY.md`)
- [ ] No secrets/API keys present in client source or resources
- [ ] Database migrations (SQLDelight schema changes) are backward-compatible or have a defined
      migration path with no data loss

## Accessibility & Performance
- [ ] `Checklists/PERFORMANCE_CHECKLIST.md` run on any list/data-heavy screen touched this release
- [ ] `26-ACCESSIBILITY.md` verification procedure run on any new/changed screen, especially
      Finance/PPDB/Attendance flows

## Localization
- [ ] All new user-facing strings present in both `id` and `en` resource files
- [ ] Currency/date formatting verified correct for the `id-ID` locale

## Rollback Readiness
- [ ] A rollback plan exists for any backend contract change this release depends on
- [ ] Feature flags (if used) default to the safe/previous behavior unless explicitly enabled

## Documentation
- [ ] Any changed business rule, architecture pattern, or design-system component is reflected in
      the corresponding file under `AI-EDUOCTO-MASTER/` in the same release
