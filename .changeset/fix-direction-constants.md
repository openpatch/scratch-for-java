---
type: patch
---

Fix `Sprite.DIRECTION_RIGHT`/`DIRECTION_UP`/`DIRECTION_LEFT`/`DIRECTION_DOWN` (introduced in the previous release) having their degree values swapped relative to how `Sprite.move()` and `setDirection()` actually behave. A sprite's default direction is `90`, which moves it along +x ("right"), and `move()`'s trig confirms the real convention — matching real Scratch — is `0 = up, 90 = right, 180 = down, 270 = left`. The constants previously claimed `0 = right, 90 = up, 180 = left, 270 = down`, the opposite of reality. Also corrected the matching descriptions in `setDirection()`'s and `pointInDirection()`'s Javadoc, which had the same swap.

No shipped example or demo used these constants, so this only affects code written against the just-released values.
