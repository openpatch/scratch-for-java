---
type: minor
---

Transparency is the Scratch ghost effect.

It ran from 0 to 255, where 0 was invisible and 255 the solid sprite you start
with — upside down and on the wrong scale next to the `set [ghost v] effect to`
shown beside it. It now runs 0 to 100, 0 solid and 100 invisible, and a value
outside that is pinned rather than wrapped.

`mySprite.setTransparency(50)` used to be nearly invisible; it is now half
see-through.
