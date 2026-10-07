---
type: patch
---

Sprites load their costumes without a window, so sizes, costume switching,
hitboxes and clones can be checked in plain unit tests. Without a window,
timers stand at 0.
