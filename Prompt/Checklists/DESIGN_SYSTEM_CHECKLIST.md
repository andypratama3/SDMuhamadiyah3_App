# Design System Checklist

Run this when adding or modifying a shared design-system component/token.

## Tokens
- [ ] No raw hex/dp/sp values — references `EduOctoTheme.colors/typography/spacing/shapes`
      exclusively
- [ ] If a new token is introduced, it's added at the correct layer (Primitive/Semantic/Component
      per `03-DESIGN-SYSTEM.md`), not skipping straight to component-level hardcoding

## Component Contract
- [ ] Accepts `modifier: Modifier = Modifier` as first optional parameter, applied outermost
- [ ] Slot-based API (leading/content/trailing) rather than boolean-flag explosion
      (`07-COMPONENT-SYSTEM.md`)
- [ ] All Required States from `20-COMPONENT-LIBRARY.md` implemented for this component
- [ ] Component is added/updated in `20-COMPONENT-LIBRARY.md`'s table in the same change

## Visual Consistency
- [ ] Matches the "Academic Intelligence" visual keywords (`02-DESIGN-PHILOSOPHY.md`) — not
      generic Material default styling
- [ ] Color usage is semantic, not decorative (`04-COLOR-SYSTEM.md`)
- [ ] Typography uses the defined scale, no off-scale font sizes (`05-TYPOGRAPHY.md`)
- [ ] Glass usage (if any) follows `08-GLASSMORPHISM.md` — used selectively, not by default

## Motion
- [ ] Uses `EduOctoMotion` easing/duration tokens (`09-ANIMATION.md`)
- [ ] Reduced-motion fallback present if the component animates

## Previews
- [ ] `@Preview` for default state
- [ ] `@Preview` for every other Required State
- [ ] `@Preview` with long-text overflow content
- [ ] `@Preview` against a dark/busy background (forward dark-mode safety check)

## Cross-Platform
- [ ] Verified (or reasoned through) on Android, iOS, and Desktop rendering — any platform-specific
      fallback (e.g. blur unsupported) is graceful and intentional-looking
