---
type: patch
---

The reference lists only what someone using the library can actually call.

Twenty-three pages documented the library's own plumbing. Nine of them took a
Processing `PGraphics` — `draw`, `drawDebug`, `drawShape` — which nothing in the
library hands out, so there was no way to call them and no example to show.
`addedToStage`/`removedFromStage` are the methods that *call* the documented
`whenAddedToStage`/`whenRemovedFromStage`, and having both listed invited
overriding the wrong one; on `Text` and `Pen` they carried no description at all,
so those pages were a bare signature. The rest were `Sprite`'s protected
`setWidth`, `setHeight`, `setNineSlice`, `disableNineSlice`, `setUI` and `isUI`
— the seams `UISprite` is built on — plus `Shape.invalidateCache`.

The doclet now documents public members only, so a protected helper added later
cannot leak into the reference on its own.

The extension pages that have no example keep them, because the reason is
different: `File`, `Pixels`, the recorders, `Shader`, `Sorting` and `Tiled` are
real API that simply cannot run inside a web page. Those 66 pages now say so, in
a note the doclet places from a single `@desktop-only` tag in each extension's
`package-info.java`.
