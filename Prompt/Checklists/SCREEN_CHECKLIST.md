# Screen Checklist

The single checklist to run on every individual screen before marking it "done." Combines the most
critical items from `UI_CHECKLIST.md`, `UX_CHECKLIST.md`, `26-ACCESSIBILITY.md`, and
`30-FULL-OUTPUT-ENFORCEMENT.md` into one pass/fail gate.

## Architecture
- [ ] Follows the `Screen` (wiring) / `Content` (pure UI) split (`22-COMPOSE-STANDARDS.md`)
- [ ] `UiState`/`Intent`/`Effect` defined per `24-STATE-MANAGEMENT.md`
- [ ] No business logic inside Composables; logic lives in ViewModel/UseCase/Repository
- [ ] No Ktor/SQLDelight types leaked into the presentation layer

## States (mandatory — no exceptions)
- [ ] Loading state
- [ ] Empty state
- [ ] Error state (with retry)
- [ ] Success/populated state

## Design System
- [ ] Tokens only — no raw hex/dp/sp
- [ ] Existing components reused where possible; new ones added to `20-COMPONENT-LIBRARY.md`
- [ ] Answers all five questions from `02-DESIGN-PHILOSOPHY.md`

## Business Rules
- [ ] Implements the exact business rule(s) from the relevant `ERP/*.md` — no simplification
- [ ] Permission matrix enforced both visually (what's shown) and at the action level (what's
      allowed to execute)
- [ ] Audit logging present if this screen performs an action requiring it

## Accessibility
- [ ] Contrast, touch targets, semantics, live regions verified per `26-ACCESSIBILITY.md`
- [ ] Keyboard navigation works on Desktop

## Cross-Platform
- [ ] Reasoned through or verified on Android, iOS, and Desktop

## Output Integrity (`30-FULL-OUTPUT-ENFORCEMENT.md`)
- [ ] No `TODO`/placeholder/omitted-implementation anywhere in the delivered code
- [ ] Any genuinely out-of-scope piece is explicitly flagged, not silently dropped

## Previews
- [ ] At least 4 `@Preview`s: default, loading, empty, error
