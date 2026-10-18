---
type: minor
---

Add optional drawing and bitmap text extensions: `DrawingSprite` draws cached images, filled polygons and rectangles with clipping in Scratch coordinates, and `BitmapText` displays and measures image glyphs without creating a sprite per letter.

Expose Tiled map properties, dimensions, tile IDs and tileset collision shapes, including positioned shapes with tile flips. Map data can be read without creating a window. Tiled object-layer spawning also works in the browser, with the same constructor choices and stage hooks.

Add `Sprite.playSoundCopy(name, volume)` and `Stage.playSoundCopy(name, volume)` for overlapping plays. Each returns a `SoundPlayback` with independent volume and stop controls; the owner's sound stop methods also stop its copies.
