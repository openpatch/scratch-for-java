---
type: patch
---

Draw stamps at the sprite's size.

A sprite scaled with `setSize` left stamps at the costume file's natural size:
shrink a sprite to a quarter and every stamp it left behind was still four
times too big. The stamp was drawn straight from the untouched source image,
which ignores the costume's current width and height.

Stamps now use the costume's size, so a stamp looks like the sprite that left
it.

A stamp drawn more than once also kept turning. Each draw subtracted 90 degrees
from the stamp's own heading and stored it back, so the angle drifted a quarter
turn per frame and a stamp on screen spun. The heading is now worked out
without changing the stamp.
