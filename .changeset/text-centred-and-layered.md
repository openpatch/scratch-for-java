---
type: minor
---

Plain text now sits on the position it was given. It used to be drawn eight
pixels down and to the right of it - the padding a framed style needs between
its border and its words, which plain words have no use for - so a centred label
never landed on the thing it labelled: a number put at the middle of a line came
out beside it. The words are now centred on their position, the way a sprite put
there is.

A width too narrow to hold a single letter no longer wraps the words at all. It
used to turn them into a column of one letter per line: `new Text("42", x, y, 1)`
drew a 4 above a 2. Line breaks written into the text are still kept.

`Text` can also be layered like a sprite: `goToFrontLayer()`, `goToBackLayer()`,
`goLayersForwards(int)` and `goLayersBackwards(int)` decide which of several
overlapping texts is on top. Texts are drawn above the sprites, so this orders a
text against the other texts.
