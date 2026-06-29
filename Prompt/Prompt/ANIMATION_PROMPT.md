# ANIMATION_PROMPT

Use this template when asking the agent to add or refine motion on a screen/component.

---

```
Add/refine motion for [SCREEN/COMPONENT/INTERACTION].

Context to read first:
- /AI-EDUOCTO-MASTER/09-ANIMATION.md (easing, durations, animation type selection table)
- /AI-EDUOCTO-MASTER/10-HAPTICS.md if the motion pairs with a tactile interaction
- /AI-EDUOCTO-MASTER/25-PERFORMANCE.md (rendering performance rules for animation)

Before implementing:
1. Identify which animation type from the `09-ANIMATION.md` table fits this interaction (Fade,
   Slide, Blur Reveal, Scale, Shared Transition, Stagger, Spring) and state why.
2. Confirm the duration token (`durationInstant/Fast/Default/Slow`) matches the interaction's
   actual cause-effect relationship — never pick a duration arbitrarily.
3. Confirm `EduOctoEasing` is used unless a spring is more appropriate per the table (drag/swipe
   interactions use spring, not the cubic-bezier).

Implement:
1. The animation using shared `EduOctoMotion` tokens — no magic duration/easing values inline.
2. A reduced-motion fallback (simple cross-fade at `durationFast`) per the accessibility rule in
   `09-ANIMATION.md`.
3. Confirm hardware-accelerated properties are used (graphicsLayer) where the animation is purely
   visual, per `25-PERFORMANCE.md`.
4. Pair with a haptic per `10-HAPTICS.md`'s mapping table if this is a discrete user-triggered
   action (not for passive/ambient or scroll-following motion).

State explicitly that the animation does not loop indefinitely (unless it is a legitimate loading
indicator) and does not block interaction longer than `durationSlow`.
```
