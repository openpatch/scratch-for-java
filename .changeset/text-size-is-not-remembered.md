---
type: patch
---

Drawing a text no longer writes its measured size back into it.

A box drawn after a bubble came out the size of that bubble, and `getWidth()`
answered with whatever was last drawn rather than the width that was set.
