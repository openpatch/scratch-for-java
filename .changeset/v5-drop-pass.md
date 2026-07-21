---
type: major
---

Remove 41 public methods from `Sprite` and `Stage`, plus 15 public handler interfaces.

First pass of the v5 API slimming. Everything removed here either had no Scratch
equivalent and no demonstrated use, or was a second way to do something that
already had a first way.

- **The `setWhenX` family is gone** (17 setters and 15 nested `When*Handler`
  interfaces). Every event already has an overridable `whenX()`, which is the
  form that matches Scratch. Override it instead.
- **`setRun` is gone** — override `run()`.
- **`setOnEdgeBounce` is gone** — call `ifOnEdgeBounce()` inside `run()`, the way
  the Scratch block works.
- **Twelve typed collection queries on `Stage`** (`countSpritesOf`, `findPensOf`,
  `removeAllTexts`, ...) are gone in favour of the generic `find(Class)` and
  `count(Class)` that sat beside them.
- **`Sprite.getWindow()`, `Stage.getWindow()`** — use `Window.getInstance()`.
- **`removeSound`, `Stage.removeBackdrop`** — no Scratch equivalent, no use.
- `Sprite.keyEvent` / `mouseEvent` are now package-private; they were never
  meant to be called by hand.
- `Stage.goToFrontLayer(Sprite)` and its three siblings, and the three
  `removeAllX()` helpers, are no longer public; the `Sprite` methods and
  `removeAll()` still do the same job.
