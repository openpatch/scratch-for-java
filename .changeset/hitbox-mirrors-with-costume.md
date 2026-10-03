---
type: patch
---

A sprite with rotation style `LEFT_RIGHT` that faces left now has its hitbox
mirrored along with its costume. The costume was drawn flipped but the hitbox
was not, so a costume whose painted part is not in the middle of its canvas
collided with the empty side - and a hitbox set with `setHitbox` to cover, say,
only the front of a sprite stayed at its back after it turned around. Speech
and thought bubbles follow the mirrored costume as well. The Online IDE already
mirrored the hitbox.
