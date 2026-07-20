---
type: minor
---

Improve beginner-friendliness of the library.

- **`KeyCode` enum**: Replaced the `static final int` constants class with a proper Java enum. Key constants now have short, readable names (`KeyCode.SPACE`, `KeyCode.UP`, `KeyCode.A`, etc.) with full IDE auto-complete support. The `whenKeyPressed(KeyCode)`, `whenKeyReleased(KeyCode)`, and `isKeyPressed(KeyCode)` methods now accept `KeyCode` directly.

- **Direction constants**: Added `Sprite.DIRECTION_RIGHT`, `Sprite.DIRECTION_UP`, `Sprite.DIRECTION_LEFT`, and `Sprite.DIRECTION_DOWN` as named constants for `setDirection()`.

- **`debug()` method**: Added `debug(Object... values)` to `Sprite`, `Stage`, and `Window`. Prints a prefixed message to stdout (e.g. `[CatSprite] x = 100.0`) only when debug mode is enabled (F12 or `setDebug(true)`).

- **Better asset error messages**: When an image, sound, or font file cannot be loaded, the error now shows the resolved absolute path, checks whether the parent folder exists, lists files in the same folder, and suggests a "did you mean?" correction for case mismatches.

- **More helpful runtime warnings**: Methods that previously failed silently now print a clear warning with a tip. Affected cases include calling `broadcast()`, layer methods, or visual methods (`setTint`, `setTransparency`, `setHeight`, `setWidth`) before a sprite is on a stage or has a costume; `switchShader()` with an unknown name; `addTimer("default")`; and `setTextureSampling()` with an invalid value.

- **Suppress Processing noise**: Internal Processing and JOGL messages (window resize notices, missing-file messages, X11 shutdown output) are now filtered from the console so beginners only see output relevant to their code.

- **Fix debug mode font size**: Enabling debug mode no longer changes the font size of sprites that display text.
