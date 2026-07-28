---
type: patch
---

A text shows the words it was built with, in every style.

`new Text("Hello World", 0, 0, 400)` put nothing on the stage. The constructor
left the text hidden, and only `showText()` ever revealed it - so the obvious way
to write the obvious first program drew a blank stage. A text built with words is
visible now. One built with nothing to say still starts hidden, which is what a
speech bubble waiting for `say()` and the stage's own display line both want.

Speech and thought bubbles also work on a text of their own. `drawBubble` gave up
unless the text belonged to a sprite, because it took its position from that
sprite, so `setStyle(TextStyle.SPEAK)` on a text you placed yourself drew
nothing. Such a text now keeps the position it was given, the way the other two
styles do, and the bubble grows up and to the right of it.

A framed text is drawn around the words it holds rather than filling the width it
was given, which is what the browser has always done. The width is where the
words wrap: `new Text("Hello World", 0, 0, 400)` wraps at 400 and comes out as
wide as "Hello World", instead of as a mostly empty 400 pixel frame running off
the side of the stage. The stage's own display line still reaches from edge to
edge - it is a band, not a label.

Drawing no longer writes the measured size back into the text. A box drawn after
a bubble used to come out the size of that bubble, and `getWidth()` answered with
whatever was last drawn instead of the width that was set.

`setAlign` works on the framed styles. It only ever reached the words inside the
frame, and the frame itself always started at the position it was given, so a
centred box was not centred on anything - and its words were centred eight pixels
from its left edge, half of them outside it. The box now sits where the alignment
asks: its left edge on the position, its middle, or its right edge.

An aligned text also sat a line too high. Choosing a horizontal alignment put the
vertical one back to the baseline, and only a text that was left alone kept the
top edge it was drawn against.

A box is rounded on all four corners. The stage's display line keeps its two
bottom corners square, because they sit on the edge of the stage, where a curve
would only show what is behind it.
