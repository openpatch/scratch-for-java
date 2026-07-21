---
type: major
---

Replace the stamp plumbing on `Stage` with one documented method.

`addStampsToBackground` and `addStampsToForeground` were public, in two
overloads each, so that `TiledMap` could stamp map layers from another package.
They are now package-private, and one documented method took their place:

```java
stage.stamp(stamps, Layer.BACKGROUND);
```

`Layer` is a new enum in the core package with `BACKGROUND`, `FOREGROUND` and
`UI`, so choosing a layer is now something you say rather than something you
pick a method name for.

`Sprite` and `Stage` are down to 192 public methods, from 293 before v5.
