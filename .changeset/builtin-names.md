---
type: minor
---

A built-in sprite name works everywhere a picture is named.

`new Sprite("player", "slimeGreen")` threw `Could not load image: slimeGreen`:
the constructor took a path where `addCostume()` took either. It now takes
either, as do `Stage.setCursor`, `Window.useSplashLogo` and
`Sprite.addCostumes`. A string with a file extension is still a path.
