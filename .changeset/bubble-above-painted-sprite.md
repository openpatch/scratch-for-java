---
type: patch
---

A speech or thought bubble now hangs off the top right corner of what is
painted on the sprite's costume, not off its hitbox. A sprite whose hitbox was
set to just its feet - so that it can stand in front of a tree without getting
stuck on it - got its bubble at its feet instead of above its head. The Online
IDE already placed the bubble this way. Collisions still use the hitbox.
