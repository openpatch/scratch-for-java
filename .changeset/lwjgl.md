---
type: major
---

Scratch for Java no longer runs on Processing. It draws with its own renderer on LWJGL (OpenGL 3.3, 4.1 on macOS), plays sound through OpenAL and needs no `java.desktop`. `run()` is called exactly 60 times per second of game time on every computer, and what a program sees (collisions, colour sensing, text layout) no longer depends on the graphics card or the screen.

The extensions are jars of their own: `scratch-tiled`, `scratch-save`, `scratch-recorder` and `scratch-drawing`, with short packages (`org.openpatch.scratch.tiled`, …). Camera, shaders, sound, pixels and sorting are part of `org.openpatch.scratch`. The `-all` jar still contains everything, and a public extension API lets others write extensions.

The API was cleaned up for learners, and programs written for 6.x need changes: time is in seconds everywhere (`say("Hi", 2)`, `waitSeconds(0.5)`, `timer.every(0.5)`), animations and nine-slicing are on every sprite (`AnimatedSprite` and `UISprite` are gone), `createClone()` replaces `clone()`, layers replace the UI flags, `Color` cannot be changed, the colour effect and the tint are separate, and shaders have a new, shorter format. The page "Moving from 6.x to 7.0" lists every change, and `scripts/migrate-7.py` makes the mechanical ones.

BlueJ is no longer supported; Scratch for Java Studio is the IDE.
