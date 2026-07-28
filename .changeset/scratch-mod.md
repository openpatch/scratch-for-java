---
type: minor
---

Operators.mod answers the way the Scratch block does.

It was Java's `%`, whose answer takes the sign of the first input, so
`mod(-7, 3)` was -1 where the block says 2. The answer now takes the sign of the
second input, which is what makes it wrap a value into a range without a special
case: `mod(-1, 10)` is 9.
