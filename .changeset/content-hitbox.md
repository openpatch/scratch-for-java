---
type: minor
---

Ignore transparent pixels when building the default hitbox.

The default hitbox used to be the whole costume. Costumes are often drawn into
a larger canvas - a standing pose in a costume tall enough to also hold a
jumping one - so a sprite collided with empty space well above its head. The
default hitbox is now the smallest rectangle around the pixels that are
actually painted.

This makes collisions match what a player can see. A sprite whose costume fills
its canvas is unaffected. Hitboxes set with `setHitbox(...)` are unaffected as
well.

The measurement is done once per costume image and then cached.
