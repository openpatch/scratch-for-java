---
type: patch
---

Hide the stamp and erase plumbing from the reference documentation.

`Sprite.stampToBackground/Foreground/UI` and `Stage.eraseBackground/Foreground/UI`
exist so that `Pen` and `TiledMap` can reach the stage's buffers. Those classes
live in other packages, so Java requires the methods to be public even though
nobody should call them by hand. They are now tagged `@ignore-in-docs`, which
takes them off the reference site.

`Sprite.stamp()` and `Stage.eraseAll()` are unaffected; both are real Scratch
pen blocks.
