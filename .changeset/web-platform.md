---
type: minor
---

Games can run in a web page. The core (`scratch`) no longer depends on LWJGL: the window,
OpenGL, sound and image decoding come from a platform module. `scratch-desktop` is the desktop
one (LWJGL, as before) and `scratch-web` the browser one: `org.openpatch.scratch.web.export.WebExport`
compiles a game and the library to WebAssembly with TeaVM and writes a page that runs it.
The fat jar (`scratch-<version>-all.jar`) contains the core and `scratch-desktop`, so programs that
use it change nothing. Maven projects that depend on `scratch` alone now also need `scratch-desktop`.
Text is drawn by a Java port of stb_truetype, pixel for pixel as before, on every platform.
Tiled maps work in a web page too: `scratch-tiled` reads .tmx and .tsx files itself and no longer
depends on Jackson (maps load exactly as before; checked on 78 maps and tilesets). `TiledMap.spawn`
takes the sprite classes it may make, `map.spawn("Objects", Player.class, Wall.class)`, instead of
looking classes up by the type names in the map, which a web page cannot do. NRW projects get
their own web jar, `scratch-<version>-nrw-web.jar`.
Saving needs no Jackson either: `SaveData` and `File` read and write JSON themselves (the same
rules as before: public fields, unknown or mistyped values are refused by `SaveData`). In a web
page, saves are kept in the browser's storage for that page. `File.saveXML`, `File.loadXML` and
`File.loadXMLOrCreate` are gone: use `File.save` and `File.load` (JSON). The web jar contains every
extension but gamepad.
