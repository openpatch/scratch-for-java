---
type: patch
---

A text built with words can be drawn at all.

`new Text(words, x, y, width)` never set a style, and drawing switches on one, so
a text built that way threw on every frame - from inside the loading screen,
which the program then never left. A white stage, nothing drawn, and no error
where anyone would look for it.
