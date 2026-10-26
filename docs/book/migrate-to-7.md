---
name: Moving from 6.x to 7.0
index: 14
---

# Moving from 6.x to 7.0

Scratch for Java 7.0 no longer runs on Processing. It has its own renderer on
[LWJGL](https://www.lwjgl.org) (OpenGL, OpenAL, GLFW), and the extensions became
jars of their own. Most programs only need new imports and seconds instead of
milliseconds, but 7.0 does not try to stay compatible with 6.x: where a cleaner API
for learners needed a change, the change was made. Projects that should not move
stay on 6.x.

## Moving a project

The repository has a script that does the mechanical part. Run it once per project
(it is not meant to run twice over the same files):

```bash
python3 scripts/migrate-7.py src/            # dry run: shows what would change
python3 scripts/migrate-7.py --write src/    # changes the files
python3 scripts/migrate-7.py --out new/ src/ # writes changed copies to new/
```

It changes packages, renames methods, divides literal millisecond arguments by
1000, turns `clone()` into `createClone()`, `extends AnimatedSprite` and
`extends UISprite` into `extends Sprite`, rewrites Processing fragment shaders into
the new format and the ```` ```java ```` blocks of Markdown pages. Everything it
cannot decide gets a comment above the line:

```
// TODO 7.0: setTint(hue) is now setColorEffect(…) (Scratch's colour effect, 0–200); setTint(Color) multiplies.
```

Then compile, work through the `TODO 7.0` comments and run the program.

## What a computer needs

- **OpenGL 3.3** (4.1 on macOS). 6.x needed OpenGL 2. Computers from the last ten
  years have it; very old school PCs and some virtual machines do not, and the
  program then says so when it starts.
- **Java 17** or newer, as before.
- **macOS:** the window has to run on the first thread. Studio starts every program
  that way. Started by hand, use the launcher:
  `java -XstartOnFirstThread -cp game.jar org.openpatch.scratch.Launcher MyGame`.
  AWT and Swing windows (`JOptionPane`, `JFrame`) do not work next to the game window
  on macOS.
- **BlueJ** is no longer supported. Scratch for Java Studio is the IDE.

## Jars and imports

Everything a learner uses is in one package, as before: `import org.openpatch.scratch.*;`.
Camera, sound, text, pixels, shaders and sorting moved into it from
`extensions.*`. The remaining extensions are jars of their own with short package
names:

| 6.x | 7.0 | Maven artifact |
|---|---|---|
| `org.openpatch.scratch.extensions.camera.Camera` | `org.openpatch.scratch.Camera` | `scratch` |
| `…extensions.shader.Shader`, `Shaders` | `org.openpatch.scratch.Shader`, `Shaders` | `scratch` |
| `…extensions.pixels.Pixels` | `org.openpatch.scratch.Pixels` | `scratch` |
| `…extensions.sound.SoundPlayback` | `org.openpatch.scratch.SoundPlayback` | `scratch` |
| `…extensions.sorting.Sorting` | `stage.sortByY()`, `sortBy(…)`, `stopSorting()` | `scratch` |
| `…extensions.tiled.*` | `org.openpatch.scratch.tiled.*` | `scratch-tiled` |
| `…extensions.fs.*` (`SaveData`, `File`) | `org.openpatch.scratch.save.*` | `scratch-save` |
| `…extensions.recorder.GifRecorder` | `org.openpatch.scratch.recorder.GifRecorder` | `scratch-recorder` |
| `…extensions.drawing.*`, `…extensions.text.BitmapText` | `org.openpatch.scratch.drawing.*` | `scratch-drawing` |

The jar from the download page (`scratch-{{VERSION}}-all.jar`) contains all of them,
as does the NRW jar. With Maven, add the extensions you use next to `scratch`; they
switch themselves on, there is nothing to register.

`FrameRecorder` and `FFmpegRecorder` are gone; `GifRecorder` stays.

## Time is in seconds

Everything that took milliseconds now takes seconds, as Scratch does.

| 6.x | 7.0 |
|---|---|
| `say("Hi", 2000)`, `think(…, 2000)` | `say("Hi", 2)`, `think(…, 2)` |
| `display(…, 2000)`, `showText(…, 2000)` | `display(…, 2)`, `showText(…, 2)` |
| `stage.wait(500)` | `stage.waitSeconds(0.5)` |
| `timer.everyMillis(500)` | `timer.every(0.5)` |
| `timer.forMillis(500)` | `timer.during(0.5)` |
| `timer.afterMillis(500)` | `timer.after(0.5)` |
| `timer.intervalMillis(…)` | `timer.interval(…)` |
| `setAnimationInterval(100)` | `setAnimationInterval(0.1)` |
| `transitionToStage(stage, 500)` | `transitionToStage(stage, 0.5)` |
| `Timer.millis()` | `getTimer().getSeconds()` |

Named timers (`getTimer("name")`) are gone: keep a `Timer` in a field. `getDeltaTime()`
is gone too: `run()` is called exactly 60 times per second of game time, on every
computer, so every tick is 1/60 s. `getFrameRate()` is gone, because frames and ticks
are no longer the same; F12 shows the frame rate.

## Sprites

- **Animations are on every sprite.** `AnimatedSprite` is gone; `addAnimation`,
  `playAnimation` and the rest are on `Sprite`. `addAnimation` takes numbered files
  (`"walk%d.png"` and 4 frames, counting from 1; or the first and last number,
  `"hit%d.png", 0, 4`) or a sprite sheet (frame width, height, then the frame numbers,
  counted row by row from 0). The form with a function that names each file is gone.
- **`UISprite` is gone.** Extend `Sprite`, call `setLayer(Layer.UI)`; `setWidth`,
  `setHeight` and `setNineSlice` are on every sprite.
- **Clones:** `createClone()` instead of `clone()`, without a cast:
  `Bullet b = this.createClone();`. The clone is placed behind the original, as in
  Scratch.
- **Direction:** `setDirection(degrees)`, `pointTowards(sprite)`,
  `pointTowards(position)`, `pointTowardsMousePointer()`. `pointInDirection`,
  `pointTowardsSprite`, `setDirection(Vector2)` and `move(Vector2)` are gone;
  `Vector2.direction()` gives a vector's direction in Scratch degrees.
- **Colour effect and tint are two things:** `setColorEffect(…)` and
  `changeColorEffect(…)` are Scratch's colour effect, exactly as Scratch draws it: the
  hue turns, lightness and saturation stay (200 is once around). `setTint(Color)` or
  `setTint(r, g, b)` multiplies the costume by a colour, for the whole sprite rather
  than one costume. `setTint(hue)` and `changeTint` are gone.

  6.x's `setTint(hue)` multiplied by a fully saturated colour, which darkens a costume
  and pushes it towards that colour; the colour effect looks lighter, as in Scratch,
  and its numbers mean something else (6.x went once around at 255). For the 6.x look
  unchanged, use `setTint(Color.fromHSB(hue / 2.55, 100, 100))`.
- **Hitboxes:** `setHitbox(Shape)`, for example
  `setHitbox(new Polygon(new double[] {0, 40, 40}, new double[] {0, 0, 30}))`.
  `getHitbox()` returns the shape where it is on the stage now. The `Hitbox` class is
  gone.
- `getWidth()` and `getHeight()` are `double` (35 % of 128 pixels is 44.8).
- `whenClicked()` runs when the mouse button goes **down** on the sprite, as in
  Scratch, and only for the frontmost sprite under the pointer. 6.x waited for the
  button to come up and called every sprite under it.

## Stage and window

- **Layers** replace the UI flags and the pen's background/foreground:
  `setLayer(Layer.BACKGROUND | MAIN | FOREGROUND | UI)` on sprites, texts and pens.
  `setUI`, `Text.setIsUI`, `Pen.goToBackground` and `goToForeground` are gone;
  `goToFrontLayer` and the like order within a layer.
- **Debug mode** is on the window only: `Window.getInstance().setDebug(true)`.
- `Stage.exit()` is gone: `Window.getInstance().exit()`.
- `stage.getPixels()` is `Pixels.of(stage)`; `Pixels.of("terrain.png")` reads an
  image's pixels without `ImageIO`.
- `stage.getSorting().byY()` is `stage.sortByY()`; sorting works within each layer.
- `removeAll()` removes sprites, texts and pens.
- `whenMouseWheelMoved(double)` takes a `double`.
- **Transitions:** `transitionToStage(() -> new Level2(), Transition.SLIDE_LEFT, 0.5)` or a transition
  shader (`Transition.fromShader("wipe.frag")`). The new stage's `run()` starts once
  the transition is over.
- **Display modes:** `Window.usePixelArt()` or `Window.useSmoothPixelArt()`, called
  before `new Window(…)`, replace `useTextureSampling` and `Text.useSmoothing`.
  `Window.isTransitioning()` is an instance method now.

## Text

The text engine is new: it reads sizes and kerning from the font file and breaks lines
itself. Spacing and line breaks differ a little from 6.x.

- Sizes are `double`: `setTextSize(double)`, `getTextSize()`.
- Colours take a `Color` or `(red, green, blue)` from 0 to 255:
  `setTextColor(new Color(0, 0, 0))`. A single number used to mean a hue, so
  `setTextColor(0)` was red; that form is gone.
- `Text.useFontSizes` and `getDefaultFontSizes` are gone: sizes are made as needed.
- `Text.useFont(path, n)` now names a pixel font's grid (the size of one font pixel),
  not a default size.
- Speech bubbles and texts in the world are drawn in front of the foreground pen, as
  in Scratch.
- `new Text(sprite)` and the copy constructor are gone.

## Colours

`Color` cannot be changed any more: `color.withHue(…)`, `withRed(…)` and so on return
a new colour, and the `HtmlColor` constants are `final`. There is one colour model,
Scratch's: hue, saturation and brightness from 0 to 100 (`getHue()`,
`Color.fromHSB(…)`), next to red, green and blue from 0 to 255. `getH`, `getS`, `getL`,
`getHSB`, `setHSB`, `setRGB`, `changeColor` and `new Color(hue)` are gone.
`toString()` gives `#rrggbb`.

A pen's hue (`pen.setColor(hue)`) goes from 0 to 100, as in Scratch; 6.x used 0 to 255.

## Shaders

Shaders have a new, shorter format: only the body of a fragment shader, with these
names provided:

```glsl
// uniform sampler2D costume;  the costume (or the stage) drawn by the shader
// uniform vec2 resolution;    its size in pixels
// uniform float time;         game time in seconds
// in vec2 uv;                 0,0 top left to 1,1 bottom right
// out vec4 color;             the result, straight alpha

void main() {
  vec4 c = texture(costume, uv);
  color = vec4(1.0 - c.rgb, c.a);
}
```

`time` and `resolution` are filled in by the library; setting them with `Shader.set`
is an error. `migrate-7.py` converts Processing shaders (`texture2D(texture, …)`,
`vertTexCoord`, `gl_FragColor`). Vertex shaders and `getPShader()` are gone.

## Sound and images

- Sound plays through OpenAL: many copies of a sound at once, short latency, the same
  on every operating system. **WAV and OGG** are supported; AIFF and AU are not any
  more (MP3 never was).
- Images: PNG, JPEG, GIF (first frame), BMP and TGA.

## Keys

`KeyCode` is a plain enum: `getCode()` and `fromCode(int)` (AWT's key numbers) are
gone. Letters follow the keyboard layout.

## Tiled maps

`map.showLayer("ground", Layer.BACKGROUND)` draws a map layer every frame and
`hideLayer` removes it; `stampLayerToBackground` and `stampLayerToForeground` are
gone. Drop the code that stamped a layer again after `eraseAll()`.

## Processing is gone

Everything that exposed Processing (`PGraphics`, `PImage`, `PShader`) is gone:
`Pen.draw`, `Shape.draw`, `Text.draw`, `Shader.getPShader`, `new Pixels(PGraphics, …)`.
To draw by hand, use `Drawing` or `DrawingSprite` from `scratch-drawing`. Writing an
extension of your own: see the extension starter in the repository
(`templates/extension-starter`).
