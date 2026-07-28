---
type: patch
---

Speech and thought bubbles work on a text of their own.

`setStyle(TextStyle.SPEAK)` on a text you placed yourself drew nothing: the
drawing gave up unless the text belonged to a sprite, because it took its
position from that sprite. Such a text now keeps the position it was given.
