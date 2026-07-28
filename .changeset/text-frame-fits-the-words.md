---
type: patch
---

A framed text is drawn around its words rather than filling the width it was given.

The width is where the words wrap. `new Text("Hello World", 0, 0, 400)` came out
as a mostly empty 400 pixel frame running off the side of the stage; it now comes
out as wide as "Hello World". The stage's display line still reaches from edge to
edge, because it is a band rather than a label.
