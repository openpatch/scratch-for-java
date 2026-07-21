---
type: patch
---

Connect the reference examples to the methods they document.

126 examples already existed under `src/examples/java/reference/`, but the
doclet only emits an example when the method carries `@example.preview`, and
exactly one method did. So the reference site showed one worked example for the
whole library while 115 finished examples sat unused next to it.

107 are now wired up by name, taking example coverage from 0.1% to 22%.

Two examples were removed rather than wired: `SpriteAddTimer`, whose method no
longer exists, and `SpriteGetCurrentTime`, a duplicate of `ClockGetCurrentTime`.

Also fixed: the doclet only ever wrote reference pages, never deleted them, so
`docs/book/reference` kept pages for methods that had been removed — 429 of
them, including `addTimer()` and `getCurrentDay()`. The directory is now cleaned
before each run.
