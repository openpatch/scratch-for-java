---
name: Custom drawing
---

# Custom drawing

Use costumes for ordinary actors and `Text` for system or TrueType fonts. When
a game needs a projected road or an image font, the optional extensions
let it draw those through the same stage and sprite model.

Extend `DrawingSprite` and override `draw(Drawing drawing)`. The canvas draws
filled polygons, rectangles and cached images in local Scratch coordinates:
x points right and y points up. Images are centred on their position. Sprite
position, direction, size, transparency, camera and layer order still apply.
Put game updates in `run()`; drawing can happen while the game is paused.

```java
import org.openpatch.scratch.Color;
import org.openpatch.scratch.drawing.Drawing;
import org.openpatch.scratch.drawing.DrawingSprite;

public class Road extends DrawingSprite {
  @Override
  public void draw(Drawing drawing) {
    drawing.clip(0, 0, 320, 180);
    drawing.polygon(new double[] {-160, 160, 30, -30},
        new double[] {-90, -90, 30, 30}, new Color(70, 70, 70));
    drawing.image("slimeGreen", 0, -50, 40, 30);
  }
}
```

Add `new Road()` to a stage as usual. The clip is reset after this sprite,
and its canvas is valid only during `draw()`. A rotated clip uses the
screen-aligned bounding rectangle. A drawing sprite starts without a hitbox;
set a shape and call `enableHitbox()` when the game needs one. Keep projection,
depth ordering and racing physics in the game.

`BitmapText` is a drawing sprite for separate image glyphs. For example,
`new BitmapText("assets/font%03d.png")` loads `font065.png` for A. Set the
content with `showText()`, spacing with `setLetterSpacing()` and
`setSpaceWidth()`, and alignment with `setAlign(TextAlign.CENTER)`. Its
position anchors the top of the first line. `getWidth()` and `getHeight()`
include its size percentage. Use `setIsUI(true)` for a score that stays fixed
when the camera moves. The reference example includes a small digit font.

For overlapping sound effects, `playSoundCopy("hit", 30)` returns a
`SoundPlayback`. Each play has its own volume and `stop()` method.
`stopSound("hit")` and `stopAllSounds()` also stop copies started by that
sprite or stage. The one-argument `playSound("hit")` keeps its usual behavior.

`TiledMap` also exposes map properties, dimensions and `getTileId(layer,
column, row)`. `getTileCollisionShapes(tileId)` returns tile-local shapes;
the overload taking a layer and cell returns positioned shapes with tile flips.
The map's top-left is (0,0), with positive x right and negative y down, matching
stamping. Use `new TiledMap(path)` to read data without opening a window.

These APIs are available in the desktop library and the browser. In the browser,
put image glyphs and TMX/TSX maps into the workspace as assets or load them by URL.
`map.spawn("Objects", Player.class, Wall.class)` also works in the browser: it
makes the given sprite classes where an object's type names them, with
constructors taking `MapObject`, `(double x, double y)`, or no arguments. Construction finishes before the sprite is added to the stage.
