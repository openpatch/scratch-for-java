---
type: patch
---

A stage's ghost and colour effects survive a change of backdrop.

`setTransparency`, `setTint` and `changeTint` reached only the backdrop that was
showing, so switching backdrop quietly undid them. They now reach all of them, as
a sprite's have always reached all its costumes.
