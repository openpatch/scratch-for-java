---
type: minor
---

Add built-in sprites, so a costume no longer needs an image file to get started.

Four CC0 sprite sheets from [kenney.nl](https://kenney.nl) ship inside the jar,
giving 838 sprites that can be used by name:

```java
this.addCostume("alienGreen_walk1");
this.addBackdrop("bg_castle");
```

- `Sprite.addCostume(String)` and `Stage.addBackdrop(String)` take a built-in
  name directly.
- The existing two-argument forms accept a built-in name in place of a path, so
  `addCostume("player", "bunny1_jump")` also works. Anything with a file
  extension is still treated as a path, so existing projects are unaffected.
- `AnimatedSprite.addAnimation(name, pattern, frames)` works with built-ins too,
  for example `addAnimation("walk", "alienGreen_walk%d", 2)`.
- Names are matched ignoring case. Three names (cactus, rock, spring) exist on
  two sheets; qualify them with the sheet, for example `"jumper/cactus"`, to
  pick the other one.
- A name that is not a built-in reports the closest matching names instead of
  failing with a file-not-found error.
