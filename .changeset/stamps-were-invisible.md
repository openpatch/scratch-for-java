---
type: patch
---

Stamps are visible again. Everything drawn with a stamp — the layers of a
`TiledMap`, and `Sprite.stamp(...)` — passed the ghost effect straight to
Processing as an alpha value, where 0 means "draw nothing" instead of "no ghost
effect". A fully opaque image was therefore stamped fully transparent, which is
why a tile map rendered as an empty stage.
