---
type: major
---

Make `TextAlign` an enum, and stop marking deliberately invisible sprites.

`TextAlign` held four **mutable** public ints — `TextAlign.CENTER = 5` compiled
and would have broken alignment everywhere — and `setAlign(int)` accepted any
number at all. It is an enum now, like `TextureSampling`, so a wrong value
cannot be written:

```java
myText.setAlign(TextAlign.LEFT);
```

Code that already wrote `TextAlign.LEFT` keeps working unchanged. `getAlign()`
reports the current setting.

Also fixed: the question-mark marker added for costume-less sprites appeared on
sprites that have no costume **on purpose** — invisible walls and triggers built
from a hitbox alone, of which the tiled demo has many. A sprite with a hitbox and
no costume is now left alone; only a sprite with neither gets the marker.
