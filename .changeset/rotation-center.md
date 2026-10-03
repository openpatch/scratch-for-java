---
type: minor
---

A sprite can turn around a point of its own choosing, like Scratch's rotation
center:

```java
this.setRotationCenter(54, 239); // in the costume's pixels, from its top left corner
```

The point sits at the sprite's position, the costume turns and mirrors around
it, and the hitbox, stamps, speech bubbles and colour sensing follow. It is
given like the points of `setHitbox`, grows with the sprite's size and holds
for every costume. Without it, a sprite turns around the middle of its costume
as before.
