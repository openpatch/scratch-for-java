---
type: patch
---

Make stages with hundreds of sprites run at a usable frame rate.

Four things were costing more than they had to, all of them growing with the
number of sprites:

- Every sprite's costume ended its draw with `resetShader()`, which flushes the
  renderer. That turned what could be a few batched draw calls into one per
  sprite. The shader is now only reset when one was actually set.
- `getHitbox()` rebuilt the sprite's collision shape from scratch on every call,
  and collision checks call it once per pair - so a stage with 200 sprites built
  40,000 shapes a frame. A sprite that has not moved, turned, resized or changed
  costume now reuses the shape it had.
- `Shape.intersects` compared two outlines by building a `java.awt.geom.Area` for
  each. Sprite hitboxes are convex, so they are now compared with separating
  axes instead, which gives the same answer about 30x faster.
- Building a `Polygon` from a list of corners rebuilt its path once per corner.

A benchmark of 200 sprites moving, bouncing off the edges and checking collisions
against each other every frame went from 96ms per frame to 25ms.
