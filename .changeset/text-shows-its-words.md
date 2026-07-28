---
type: patch
---

A text shows the words it was built with.

`new Text("Hello World", 0, 0, 400)` put nothing on the stage - the constructor
left it hidden until `showText()`. A text built with words is visible now; one
built with nothing to say still starts hidden, which is what a speech bubble
waiting for `say()` wants.
