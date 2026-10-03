---
type: patch
---

A broadcast now reaches every sprite on the stage and the stage itself, as in
Scratch and in the Online IDE. `Sprite.broadcast` left out the sprite that sent
it, and `Stage.broadcast` left out the stage, so a sprite or stage that reacts
to its own message in `whenIReceive` never heard it. A sprite that broadcasts
the same message from its own `whenIReceive` now calls itself again, as the
same script does in Scratch.
