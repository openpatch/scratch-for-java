---
type: patch
---

Draw large worlds faster. Stamps on the background and foreground and sprites that lie entirely outside the camera's view are no longer drawn, since they would not show anyway. A Tiled map stamped every frame now costs about as much as the part on screen instead of the whole map.

`isTouchingSprite(Class)`, `getTouchingSprite(Class)` and `Stage.find` check the stage's sprites with a plain loop, which is much cheaper on stages with hundreds of sprites. Sprites without variable monitors no longer create a list for them in every frame.
