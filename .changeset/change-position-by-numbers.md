---
type: minor
---

`changePosition(double x, double y)` sits alongside `changePosition(Vector2 v)`.

Moving by an amount only worked when the amount was already a `Vector2`, even
though `setPosition` has taken two numbers all along. Sprites moved by a step
worked out on the spot had to build a throwaway vector first, or fall back to two
`changeX`/`changeY` calls.

The documentation for the transparency methods has also been corrected.
`Stage.setTransparency` claimed a range of `[0...1]`; it is `[0...255]`, and it is
an opacity rather than a ghost effect — 255 is the fully solid sprite you start
with, and 0 is invisible. The reference examples for the four transparency
methods passed values that read as if the scale ran the other way.
