---
type: patch
---

`Sprite` (and therefore `AnimatedSprite`) can now be constructed and used without a running `Window`/`Applet`, which previously crashed the whole JVM via `System.exit()`.

- **Root cause**: `Sprite()`'s constructor builds a `Text`, whose constructor eagerly loaded the library's default font through the Processing asset pipeline. With no live `Applet`, that load failed and the asset-error reporter called `System.exit()`. `Font` now defers loading the underlying font file until it is actually needed (the first `getFont()` call during rendering), instead of doing it synchronously in its constructor. This matches how the library already preloads assets in the background once a `Window` is running (`Applet.loadAssets()`), and doesn't change behavior for normal (non-test) usage.
- **`Sprite.ifOnEdgeBounce()`** used to throw a `NullPointerException` if called before the sprite was added to a stage (it read stage border fields unconditionally). It's now a no-op in that case, consistent with how other stage-dependent drawing code already guards on `stage == null`.

Together these make it possible to unit test a `Sprite` subclass's movement/geometry logic (and now `run()`, as long as it doesn't touch other stage-dependent queries) without spinning up a real window - which is exactly what the newly added `SpriteTest`, `AnimatedSpriteTest`, and `FontTest` do.

Note: sprites with costumes still require a live `Applet` to construct, since loading actual image pixel data (width/height) is inherent to adding a costume - this only unblocks bare `Sprite`/`AnimatedSprite` construction and costume-free logic.
