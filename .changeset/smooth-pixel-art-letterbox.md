---
type: patch
---

Smooth pixel art scales the window's letterbox to fit, like its camera zoom, instead of whole
numbers: with `Window.useSmoothPixelArt()` the stage fills the window at any size, while plain
`usePixelArt()` keeps stepping whole pixels.
