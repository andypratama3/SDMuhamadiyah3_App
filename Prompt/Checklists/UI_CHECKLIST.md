# UI Checklist

Run this before considering any new/changed screen visually complete.

## Layout & Tokens
- [ ] No raw hex color values — all colors via `EduOctoTheme.colors` (`04-COLOR-SYSTEM.md`)
- [ ] No raw `Dp`/`sp` literals outside the spacing/typography token sets (`05`, `06`)
- [ ] Corner radii use the defined scale (`06-SPACING-SYSTEM.md`) — no arbitrary radius values
- [ ] Spacing is on the 8dp grid (4dp only for inline icon/label gaps)
- [ ] Screen margins match breakpoint rules (16dp mobile / 24dp tablet / 32dp desktop)

## Hierarchy & Five Questions (`02-DESIGN-PHILOSOPHY.md`)
- [ ] Screen clearly answers "Where am I?" (title/context visible)
- [ ] Primary action is visually obvious within 2 seconds of looking
- [ ] State changes are visually perceptible (not silent)
- [ ] One clear visual hierarchy — not all elements equally weighted
- [ ] There is always a next step, even from an empty/error state

## Components
- [ ] Reuses existing `EduOcto*` components per `20-COMPONENT-LIBRARY.md` — no ad hoc duplicate
      layouts where a shared component already exists
- [ ] Any new component added to `20-COMPONENT-LIBRARY.md` in the same change

## States
- [ ] Loading state implemented (skeleton matching final layout, `18-LOADING-STATES.md`)
- [ ] Empty state implemented with appropriate tone (`17-EMPTY-STATES.md`)
- [ ] Error state implemented with retry path (`19-ERROR-STATES.md`)
- [ ] Success/populated state implemented

## Motion
- [ ] Entry animation uses shared `EduOctoMotion` tokens (`09-ANIMATION.md`), not magic values
- [ ] Reduced-motion fallback present

## Responsiveness
- [ ] Verified at Compact, Medium, and Expanded window size classes
- [ ] No text truncation/overlap at 130% system font scale

## Glass/Elevation
- [ ] Glass surfaces used only where appropriate (`08-GLASSMORPHISM.md`) — not overused
- [ ] Elevation level matches the component's role (`06-SPACING-SYSTEM.md` elevation table)
