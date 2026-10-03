---
type: patch
---

A sprite's size now also applies to costumes added after `setSize(...)`.
`setSize` only resized the costumes the sprite already had, so a sprite that
set its size in the constructor and got its costume later, for example from a
subclass, was drawn at 100 %. `addCostume(...)` and `addCostumes(...)` now give
a new costume the sprite's current size, as the Online IDE already did.
