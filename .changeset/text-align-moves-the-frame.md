---
type: patch
---

`setAlign` moves the frame, not only the words inside it.

The frame always started at the position it was given, so a centred box was not
centred on anything and its words sat half outside it. An aligned text also sat a
line too high, because choosing a horizontal alignment put the vertical one back
to the baseline.
