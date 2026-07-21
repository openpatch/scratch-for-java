---
type: patch
---

Show the Scratch block beside the Java call, on 113 reference pages.

91 methods gained a `@scratchblock` annotation, so their reference page now
renders the block a learner already knows next to the Java that replaces it —
`.ifOnEdgeBounce()` beside `if on edge, bounce`.

Only methods with a genuine Scratch equivalent were annotated. `Vector2.dot`,
`Color.getHSB` and `Operators.lerp` have no block and are left bare on purpose.

Reference coverage, against the state before v5:

| | Before | Now |
|---|---:|---:|
| Entries | 914 (429 stale) | 485 |
| With a scratch block | 23 | 113 |
| With a worked example | 1 | 108 |
