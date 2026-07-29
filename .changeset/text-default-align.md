---
type: minor
---

Every `Text` is now centred on its position, whichever constructor built it and
whichever style it is in — the way a sprite put at the same place is, and the way
everything in a Scratch project is placed. Only `new Text()` used to be centred:
`new Text(words, x, y, width)` and the framed styles sat to the left of their
position instead, so the same words moved sideways depending on how they had been
created, and the browser port drew them somewhere else again.

The texts the library builds for itself keep the edge they are anchored by and
now say so where they are built: the stage's `display()` and `ask()` bands reach
from side to side, and a sprite's speech bubble hangs off the tail in its bottom
left corner.

Add `setAlign(TextAlign.LEFT)` to text that was relying on starting at its
position rather than being centred on it.
