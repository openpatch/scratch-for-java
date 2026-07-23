---
name: Changelog
index: 63
lang: en
---

## 5.0.2



Let the same sound be heard from several sprites at once.

Ten sheep that all added the sound `meh` and bleated together only made one
bleat. Every sound was cached as a single Java `Clip`, and a `Clip` is one
playback line: whichever sprite got there first held it, and the rest were
skipped until it was done.

Only the decoded samples are shared now. Each sound object gets a line of its
own, so ten sheep are ten bleats, while a single sheep still only bleats once at
a time, as before.

Lines are pooled and reused once a sound has finished, and are opened on a
background thread, so playing a sound never stalls the drawing of a frame -
opening one takes tens of milliseconds. A sound holds at most 16 lines, fewer
for a long one, which keeps a stage full of clones from filling memory with
copies of the same music.


## 5.0.1



Make stages with hundreds of sprites run at a usable frame rate.

Four things were costing more than they had to, all of them growing with the
number of sprites:

- Every sprite's costume ended its draw with `resetShader()`, which flushes the
  renderer. That turned what could be a few batched draw calls into one per
  sprite. The shader is now only reset when one was actually set.
- `getHitbox()` rebuilt the sprite's collision shape from scratch on every call,
  and collision checks call it once per pair - so a stage with 200 sprites built
  40,000 shapes a frame. A sprite that has not moved, turned, resized or changed
  costume now reuses the shape it had.
- `Shape.intersects` compared two outlines by building a `java.awt.geom.Area` for
  each. Sprite hitboxes are convex, so they are now compared with separating
  axes instead, which gives the same answer about 30x faster.
- Building a `Polygon` from a list of corners rebuilt its path once per corner.

A benchmark of 200 sprites moving, bouncing off the edges and checking collisions
against each other every frame went from 96ms per frame to 25ms.


## 5.0.0



Scratch for Java 5 is about what a beginner meets first.

Version 4 grew by addition. Every capability arrived as more public methods on
`Sprite` and `Stage`, more constructors, more public fields to assign before
starting, and more packages to import. A learner opening `Sprite` in BlueJ met
289 public methods, most of which had no Scratch block behind them, and a
mistyped costume name closed BlueJ itself.

v5 removes what nobody needed, moves what belonged elsewhere, and fills the
gaps that were actually stopping projects. `Sprite` and `Stage` are down from
**289 public methods to 193**, and everything a first course needs is behind one
import.

Nothing here is a rename for its own sake: every removal is either a second way
to do something that already had a first way, or something with no Scratch
equivalent and no demonstrated use.

### Migrating from v4

#### One import

Eight packages moved into `org.openpatch.scratch`, so `Pen`, `Text`, `Timer`,
`AnimatedSprite`, `Vector2`, `Random`, `Color`, `Hitbox` and the shapes all come
from one line:

```java
import org.openpatch.scratch.*;
```

Delete any `import org.openpatch.scratch.extensions.animation.*;` and its
siblings for `color`, `hitbox`, `math`, `pen`, `shape`, `text` and `timer`.

Five packages are still opt-in, for things a first course does not meet:
`camera`, `fs`, `recorder`, `shader` and `tiled`, joined by the new `pixels` and
`sorting`.

#### Handlers are overridden, not registered

The whole `setWhenX` family is gone — 16 setters across `Sprite`, `Stage` and
`Window`, and the 16 nested `When*Handler` interfaces that went with them. Every
event already had an overridable method, which is the form that matches Scratch:

```java
// v4
this.setWhenKeyPressed((keyCode) -> { ... });

// v5
public void whenKeyPressed(KeyCode keyCode) { ... }
```

`setRun` goes the same way — override `run()`. And `setOnEdgeBounce(true)`
becomes a call inside `run()`, the way the block works:

```java
this.ifOnEdgeBounce();
```

#### Settings are methods, chosen before the window opens

Public fields you assigned before starting did nothing at all if you assigned
them afterwards — no error, no warning. They are methods now, and they say so
when used too late:

| v4 | v5 |
|---|---|
| `new Stage(true, 800, 600)` | `Window.useFullScreen();` then `new Stage(800, 600)` |
| `Window.TEXTURE_SAMPLING_MODE = 2;` | `Window.useTextureSampling(TextureSampling.POINT);` |
| `Text.DEFAULT_FONT = "f.ttf";` | `Text.useFont("f.ttf");` |
| `Text.DEFAULT_FONT_SIZE = 11;` | `Text.useFont("f.ttf", 11);` |
| `Text.FONT_SIZES = new int[]{14, 20};` | `Text.useFontSizes(14, 20);` |
| `Text.SMOOTHING = false;` | `Text.useSmoothing(false);` |
| `Stage.setTextureSampling(...)` | `Window.useTextureSampling(...)` |

`TextureSampling` and `TextAlign` are enums rather than loose ints, so a wrong
value can no longer be written. Code that already said `TextAlign.LEFT` compiles
unchanged.

#### Methods that moved to a class of their own

Nine clock values and nine shader methods existed on **both** `Sprite` and
`Stage` — 36 methods for 18 ideas.

| v4 | v5 |
|---|---|
| `this.getCurrentYear()` | `Clock.getYear()` |
| `this.addShader(...)`, `this.switchShader(...)` | `this.getShaders().add(...)`, `.switchTo(...)` |
| `this.setSorter(...)`, `this.enableYSort()` | `this.getSorting().byY()` |
| `this.getPixels()` → `int[]` | `this.getPixels().main()` → `int[]` |
| `this.stampToUI()` | `this.stamp(Layer.UI)` |
| `this.getWindow()` | `Window.getInstance()` |
| `stage.countSpritesOf(C.class)` | `stage.count(C.class)` |
| `stage.findPensOf(C.class)` | `stage.find(C.class)` |

`Layer` is a new enum with `BACKGROUND`, `FOREGROUND` and `UI`, so choosing a
layer is something you say rather than a method name you pick.

#### Overloads that made the wrong thing easy

- `broadcast(Object)` and `whenIReceive(Object)` are gone. Broadcasting a
  non-`String` used to call only the `Object` handler, so a sprite overriding
  `whenIReceive(String)` silently heard nothing. Messages are names, as in
  Scratch.
- `whenAddedToStage(Stage)` and `whenRemovedFromStage(Stage)` are gone —
  override the no-argument forms; the argument is what `getStage()` returns.
- `setHitbox(Hitbox)` and `setHitbox(double[], double[])` are gone.
  `setHitbox(Shape)` and `setHitbox(double... points)` cover both.

#### Interface parts have their own class

Buttons and bars want to be drawn on top, ignore the camera, and be sized in
pixels rather than percent. That is now `UISprite`:

```java
public class Button extends UISprite {
  public Button() {
    this.addCostume("button", "ui/button.png");
    this.setNineSlice(12, 24, 12, 24);
    this.setWidth(600);
  }
}
```

`setWidth`, `setHeight`, `setNineSlice` and `disableNineSlice` are `protected` on
`Sprite` and public on `UISprite`; `changeWidth` and `changeHeight` are on
`UISprite` only; `isUI(boolean)` became `protected setUI(boolean)`; and
`Stage.goToUILayer` is gone.

#### Fewer constructors

`Stage` had seven and `Window` eight, four of each differing only by a leading
`boolean fullScreen`. `Stage` keeps `Stage()`, `Stage(width, height)` and
`Stage(width, height, assets)`; `Window` keeps those three plus
`Window(assets)`. Full screen is `Window.useFullScreen()`.

#### Plumbing that was public only to cross a package

`Stage.draw`, `pre`, `keyEvent` and `mouseEvent` were public only so the render
loop could reach them from another package. They are private now, reached
through an internal interface the stage hands over.

`Stage.stamp(Queue<Stamp>, Layer)` went the same way. It existed so `TiledMap`
could put a map's tiles into the stage's stamp queues, which meant a method
taking `internal.Stamp` — a type nobody outside the library can build — sat in
autocomplete next to the real blocks. `TiledMap` now goes through an internal
accessor, and the method is package-private.

Neither was reachable in any useful way before, so no project has to change.
`Sprite.stamp(Layer)` is still the stamp block.

### Behaviour that changed without the code changing

These need no edit, but a v4 project may look or behave differently:

- **Hitboxes now wrap the painted pixels**, not the whole costume. Costumes are
  often drawn into a larger canvas — a standing pose in a costume tall enough to
  hold a jumping one — and a sprite used to collide with that empty space.
  Collisions now match what a player can see. Hitboxes set with `setHitbox(...)`
  are unaffected.
- **A sprite with no costume is no longer invisible.** It draws a marked box and
  explains once on the console what to add, because drawing nothing looks exactly
  like a broken program. A sprite with a hitbox and no costume — an invisible
  wall — is left alone.
- **Asset failures throw `ScratchException` instead of calling `System.exit`.**
  Ending the process took BlueJ's virtual machine with it, so a mistyped costume
  name used to close everything the student had open.
- **The loading screen fades in and out**, which adds about half a second to
  starting a project.

### What is new

- **838 built-in sprites and 266 built-in sounds** from [kenney.nl](https://kenney.nl)
  ship inside the jar, so a first project needs no downloads at all:

  ```java
  this.addCostume("alienGreen_walk1");
  this.addAnimation("walk", "alienGreen_walk%d", 2);
  this.addSound("footstep_carpet_000");
  ```

  Names are matched ignoring case, a wrong name suggests the closest matches
  instead of reporting a missing file, and anything with a file extension is
  still treated as a path — so existing projects are unaffected. Ogg Vorbis is
  now a supported sound format.

- **`ask` and `answer`**, so a program can take typed input. Unlike Scratch it
  does not pause the script, so check `isAsking()` or wait for `getAnswer()` to
  change.

- **`glide(seconds, x, y)`** with `isGliding()`, **`setVolume` / `changeVolume` /
  `getVolume`**, and **`Stage.waitUntil(condition)`**.

- **Your own logo on the loading screen**, with `Window.useSplashLogo(...)`.

- **Named timers create themselves** — `getTimer("countdown")` no longer has to
  be declared with `addTimer` first.

`touching color` was attempted and dropped: it needs the whole screen read back
off the graphics card every frame, which is too slow for the machines this
library is meant for. `getPixels()` carries the same limitation, now written
down in its documentation.

### Fixes

- `ifOnEdgeBounce()` no longer leaves a sprite stuck half outside the stage. It
  used to reposition using the size of the hitbox alone, which only works while
  the hitbox is centred on the sprite; it now nudges the sprite by however far
  its hitbox pokes over the border.
- Setting a stage no longer makes the window flash. On a scaled or HiDPI screen
  the letterbox bars came out grey for one frame, because a render buffer's
  OpenGL framebuffer was being allocated after the canvas had already been
  painted black.
- `GifRecorder.stop()` no longer truncates the last frame of a recording.
- Ogg files below roughly 8 kB used to decode to silence with no error, which
  covers most short sound effects.
- The shaded "all" jar merges `META-INF/services`, so the Ogg decoder survives
  shading.
- Sprites can be constructed and tested without a running window.

### Documentation

The book is now seven tutorials, and every Java block on the hand-written pages
is compiled by `mvn test`, so an example cannot quietly stop working — which had
already happened twice.

- **Your first program**, **Make it Walk**, **Catch the Coins**, **Red Light,
  Green Light**, **Guess the Number**, **Bouncing Hedgehog**, **Dodge the
  Rocks**. All but the hedgehog need no downloaded files, and each links a
  finished project to compare against.
- **Browsable pages for the built-in sprites and sounds**, generated from the
  jar's own resources, searchable, with a click copying the `addCostume(...)` or
  `addSound(...)` line.
- **The reference site shows the Scratch block beside the Java call** on 113
  pages, and 108 pages now carry a worked example, against one before v5. 429
  stale pages for methods that no longer exist were deleted.
- `./scripts/run.sh` picks any of the 153 demos, finished projects and reference
  examples and runs it.


## 4.28.2



`Sprite` (and therefore `AnimatedSprite`) can now be constructed and used without a running `Window`/`Applet`, which previously crashed the whole JVM via `System.exit()`.

- **Root cause**: `Sprite()`'s constructor builds a `Text`, whose constructor eagerly loaded the library's default font through the Processing asset pipeline. With no live `Applet`, that load failed and the asset-error reporter called `System.exit()`. `Font` now defers loading the underlying font file until it is actually needed (the first `getFont()` call during rendering), instead of doing it synchronously in its constructor. This matches how the library already preloads assets in the background once a `Window` is running (`Applet.loadAssets()`), and doesn't change behavior for normal (non-test) usage.
- **`Sprite.ifOnEdgeBounce()`** used to throw a `NullPointerException` if called before the sprite was added to a stage (it read stage border fields unconditionally). It's now a no-op in that case, consistent with how other stage-dependent drawing code already guards on `stage == null`.

Together these make it possible to unit test a `Sprite` subclass's movement/geometry logic (and now `run()`, as long as it doesn't touch other stage-dependent queries) without spinning up a real window - which is exactly what the newly added `SpriteTest`, `AnimatedSpriteTest`, and `FontTest` do.

Note: sprites with costumes still require a live `Applet` to construct, since loading actual image pixel data (width/height) is inherent to adding a costume - this only unblocks bare `Sprite`/`AnimatedSprite` construction and costume-free logic.


## 4.28.1



Fix incorrect math in `Operators` and `Color`, found while adding a unit test suite for the library's pure-logic classes (`Operators`, `extensions/math`, `extensions/shape`, `extensions/hitbox`, `extensions/color`).

- **`Operators.asinOf`, `Operators.acosOf`, `Operators.atanOf`**: these applied the degrees conversion to the input before taking the inverse trig function, and never converted the result back to degrees, so `asinOf`/`acosOf` returned `NaN` for almost any real input, and `atanOf` returned radians instead of degrees. They now correctly return the degree-valued inverse of `sinOf`/`cosOf`/`tanOf` (e.g. `asinOf(1.0) == 90.0`).
- **`Operators.max(double...)`**: seeded its accumulator with `Double.MIN_VALUE` (the smallest *positive* double) instead of `Double.NEGATIVE_INFINITY`, so it returned the wrong result whenever every input was negative. Fixed to seed with `Double.NEGATIVE_INFINITY`.
- **`Color(double r, double g, double b)`**: passed `g` twice to `setRGB`, silently dropping the blue channel and replacing it with the green value. Fixed to pass `r, g, b`. This also corrects `GifRecorder.transparent`, which is constructed with this constructor.


Fix `Sprite.DIRECTION_RIGHT`/`DIRECTION_UP`/`DIRECTION_LEFT`/`DIRECTION_DOWN` (introduced in the previous release) having their degree values swapped relative to how `Sprite.move()` and `setDirection()` actually behave. A sprite's default direction is `90`, which moves it along +x ("right"), and `move()`'s trig confirms the real convention — matching real Scratch — is `0 = up, 90 = right, 180 = down, 270 = left`. The constants previously claimed `0 = right, 90 = up, 180 = left, 270 = down`, the opposite of reality. Also corrected the matching descriptions in `setDirection()`'s and `pointInDirection()`'s Javadoc, which had the same swap.

No shipped example or demo used these constants, so this only affects code written against the just-released values.


`MapObject.getProperty()` now throws a helpful error message naming the missing property and, when available, the properties that do exist on that map object — instead of a bare `NoSuchElementException` with no context. It also now gives a clear message when the map object has no custom properties at all, rather than a raw `NullPointerException`. This matches the library's existing beginner-friendly error reporting for missing assets.


## 4.28.0

Improve beginner-friendliness of the library.

- KeyCode enum: Replaced the static final int constants class with a proper Java enum. Key constants now have short, readable names (KeyCode.SPACE, KeyCode.UP, KeyCode.A, etc.) with full IDE auto-complete support. The whenKeyPressed(KeyCode), whenKeyReleased(KeyCode), and isKeyPressed(KeyCode) methods now accept KeyCode directly.
- Direction constants: Added Sprite.DIRECTION_RIGHT, Sprite.DIRECTION_UP, Sprite.DIRECTION_LEFT, and Sprite.DIRECTION_DOWN as named constants for setDirection().
- debug() method: Added debug(Object... values) to Sprite, Stage, and Window. Prints a prefixed message to stdout (e.g. [CatSprite] x = 100.0) only when debug mode is enabled (F12 or setDebug(true)).
- Better asset error messages: When an image, sound, or font file cannot be loaded, the error now shows the resolved absolute path, checks whether the parent folder exists, lists files in the same folder, and suggests a "did you mean?" correction for case mismatches.
- More helpful runtime warnings: Methods that previously failed silently now print a clear warning with a tip. Affected cases include calling broadcast(), layer methods, or visual methods (setTint, setTransparency, setHeight, setWidth) before a sprite is on a stage or has a costume; switchShader() with an unknown name; addTimer("default"); and setTextureSampling() with an invalid value.
- Suppress Processing noise: Internal Processing and JOGL messages (window resize notices, missing-file messages, X11 shutdown output) are now filtered from the console so beginners only see output relevant to their code.
- Fix debug mode font size: Enabling debug mode no longer changes the font size of sprites that display text.


## 4.26.0

- Update dependencies

## 4.25.0

- Add beginner-friendly error messages for common mistakes, such as missing
assets or incorrect file paths. This will help new users to quickly identify
and fix issues in their code.

## 4.24.2

- Fix jogl and gluegen dependencies.

## 4.24.1

- Fix wrong implemention of Polygon.

## 4.24.0

Move towards supporting Java modules. Therefore we added the shape extension
and remove public facing java.awt classes. This is a breaking change, since you
can not use the java.awt classes anymore. Instead, you can use the new shape
extension, which provides the same functionality.

```java
import org.openpatch.scratch.extensions.shape.Rectangle;
import org.openpatch.scratch.extensions.shape.Polygon;
import org.openpatch.scratch.extensions.shape.Shape;
import org.openpatch.scratch.extensions.shape.Circle;
import org.openpatch.scratch.extensions.shape.Ellipse;

Shape rect = new Rectangle(10, 10, 100, 50);
Shape poly = new Polygon(new double[] { 0, 100, 50 }, new double[] { 0, 0, 100 });
Shape circle = new Circle(50, 50, 25);
Shape ellipse = new Ellipse(50, 50, 100, 50);
Shape triangle = new Triangle(0, 0, 100, 0, 50, 100);
```

## 4.23.0

We moved the build tools from Ant to Maven. We also restructured the releases.
You can now find the final jar containing all depdencies and all operation
system dependent libraries at Github releases. This means, that you can more easily 
share your project with others, since you only need to include the final jar file.

We also released the library on Maven Central, so you can easily include it in
your project with Maven or Gradle.

## 4.22.0

- 🚀 Feat: Add functional interfaces to the Sprite and Stage class. This allows you to use lambda expressions for when methods and the run method. This is especially useful if you do not want to use inheritance. For example, you can now write:
```java
Sprite sprite = new Sprite();
sprite.setWhenKeyPressed((s, keyCode) -> {
    if (keyCode == KeyEvent.VK_SPACE) {
        s.move(10);
    }
});
```

All methods receive the sprite or stage as the first parameter, so you can access the sprite or stage directly in the lambda expression. If you use inheritance you are better off overwriting when and run methods, like before:

```java
class MySprite extends Sprite {
    @Override
    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            this.move(10);
        }
    }
}

```

- 🚀 Feat: You can now check the library version with `getLibraryVersion()` on the window class. This is useful if you want to check the version of the library at runtime.

```java
window.getLibraryVersion(); // returns the version as a string
```

## 4.21.0

- 🚀 Feat: Add `addAnimation` with a builder function to the AnimatedSprite
class. This allows you to add animations with a more flexible API. The function
receives the frame number and returns the path to the image for that frame.
This is useful if you want to generate the image path dynamically or if you
want to use a naming convention for your images. To be consistant with the
pattern the frames start counting at 1.

```java
mySprite.addAnimation("run", frame -> "test" + frame + ".png", 10);
```


## 4.20.0

- 🚀 Feat: Add 9-Slice scaling to the Sprite class. This allows you to scale a sprite in a way that the corners are not stretched, but the middle part is stretched. This is useful for UI elements like buttons or panels.

```java
sprite.setNineSlice(10, 10, 10, 10); // top right bottom left;
sprite.disableNineSlice();
```

- 🚀 Feat: Add `setWidth`, `setHeight`, `changeWidth` and `changeHeight` methods to the Sprite class. This allows you to set the width and height of a sprite without changing the aspect ratio.

## 4.19.0

- 🚀 Feat: Add transitionToStage method to Window class.

You can now use the transitionToStage method for a smoothing transition between the current and the new stage.

```java
window.transitionToStage(new MyStage(), 500);
```

The DonutIO example shows the usage of the transitionToStage method.

## 4.18.1

- 🐛 Fix: isTouchingSprite not working correctly, when the sprite is resized.
- 🐛 Fix: The build is now comaptible with Java 17 again.
- 🐛 Fix: Missing previousCostume method on the Sprite class.

## 4.18.0

- 🚀 Feat: Add texture sampling modes. These are useful if you want for example develop a pixelart game. The texture sampling mode can be set via the new global settings.
- 🚀 Feat: Introducing global settings.

You can set theses static attributes in your main-method. Take a look at the [documentation](https://openpatch.github.io/scratch-for-java/) for more information.

```java
Window.TEXTURE_SAMPLING_MODE = 2;
Text.DEFAULT_FONT = "Tiled/assets/Retro Gaming.ttf";
Text.DEFAULT_FONT_SIZE = 11;
Text.FONT_SIZES = new int[] { 11 };
Text.SPEAK_BUBBLE_MAX_LIMIT = 200;
Text.SMOOTHING = false;
```


## 4.17.0

- 🚀 Feat: Add better fullscreen mode. You can now set a target resolution, when using the fullscreen mode. This renders the stage at this resolution and scales it to the fullscreen size keeping the aspect-ratio.

In the following code the stage is rendered at 800x600 and then scaled to fit the fullscreen window.

```java
public class MyWindow extends Window {
    public MyWindow() {
        super(true, 800, 600);
    }
}
``` 

This is also possible when you are working with only the stage class.

```java
public class MyStage extends Stage {
    public MyStage() {
        super(true, 800, 600);
    }
}
``` 

## 4.16.0

- 🚀 Feat: add Operators.round(value, precision) to round values. `Operators.round(2.3456, 2)` will return `2.35`.
- 🐛 Fix: Random.randomInt not return the maximum value.

## 4.15.1

- 🐛 Fix: Edge detection not working correctly.

## 4.15.0

- 🚀 Feat: All methods return a color now return a Color object.
- 🚀 Feat: Add `get`-method to color object for comparing pixels.

## 4.14.0

- 🚀 Feat: Add `enableYSort`-method to the stage. This sorts all sprites regarding their y-coordinate.

```java
Stage stage = new Stage();
stage.enableYSort();
```


## 4.13.0

- Update dependencies
    - Processing 4.3.1
    - Jackson 2.18.2
    - Jogl 2.5.0
    - Gluegen 2.5.0
- Add template for VSCode

## 4.12.0

- 🚀 Feat: There is a new color extension providing a color class and all named HTML colors for easy reference.

```java
Color c = new Color("#FF0000"); // red
mySprite.setTint(c);

mySprite.setTint(HtmlColor.AZURE);
```

- 🚀 Feat: Add shaders to sprites and to stages. You can find the shader example in the examples directory. For a quick overview you can look at the following code snippet, which works for sprites and for stages.

```java
class ShaderSprite extends Sprite {
    public ShaderSprite() {
        var shader = this.setShader("blur", "blur.glsl");
        shader.set("radius", 10.0);
        this.switchShader("blur");
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_N) {
            this.nextShader();
        } else if (keyCode == KeyEvent.VK_R) {
            this.resetShader();
        }
    }
}
```

## 4.11.0

- 🚀 Feat: A broadcast and whenIReceive methods for the Object-class, which makes it possible to send anything as a broadcast.

```java
sprite1.broadcast(new Event());

class AnotherSprite extends Sprite {
    public void whenIReceive(Object message) {
        if (message instanceof Event) {
            // ...
        }
    }
}
```

## 4.10.0

- 🚀 Feat: Add new random methods for get a random position with respect to the window size.

```java
Random.randomPosition();
Random.randomX();
Random.randomY();
```

- 🐛 Fix: setSize not caching the new image, which lead to low framerates.


## 4.9.0

- 🚀 Feat: Add fullscreen mode. You can now define in the constructor of your window, if you want to operate in fullscreen mode.

```java
new Window(true); // fullscreen
new Window(true, assets) // fullscreen with assets loading
```

- 🐛 Fix: keyCodes for some keys were off.
- 🐛 Fix: File extension not working on Windows.

## 4.8.3

- 🐛 Fix: pointTowardsSprite pointing to the opposite direction 

## 4.8.2

- 🐛 Fix: Pen position and state when using the copy-constructor.

## 4.8.1

- 🐛 Fix: Debug text influencing text size of other texts.
- 🐛 Fix: Mouse tracking not working correctly when zooming in and out.

## 4.8.0

- 💥 BREAKING CHANGE: Pen behavior was changed to work more similar to the behavior of the scratch pen. 
    - You should double the size of your pen in your sketches to get the same visuals as in the previous versions. 
    - You should also put the pen down after you have set the initial position, since the pen now draws an initial point, when it is put down.

## 4.7.0

- 🚀 Feat: Add methods to the text class to be more consistent with the sprite class in regard to positioning.
- 🚀 Feat: Update sound library to add support for mp3 and ogg files
- 🐛 Fix: Think- and say-bubble were not at the right place

## 4.6.0

- 🚀 Feat: Add setCursor method to the stage class.
- 🚀 Feat: Add extension camera. The camera object allows you to move the view of the stage without manipulating the coordinates of e.g. sprites.

You can now get the camera from a stage object.

```java
var myStage = new Stage();
Camera myCamera = myStage.getCamera();
```

The three main abilities of the camera are:

1. Set the position of the camera (setX, setY, changeX, changeY, setPosition)
2. Set the zoom of the camera (setZoom, resetZoo, changeZoom, setZoomLimit)
3. Transform coordinates from local (Camera-Space) to global (Window-Space) (toLocalPosition, toLocalX, toLocalY, toGlobalPosition, toGlobalX, toGlobalY)

## 4.5.0

- 💥 BREAKING CHANGE: Only use double for methods instead of supporting both float and doubles. This was unnecessary and cluttered the BlueJ interface with "duplicated" methods. This could potentially break your project if you were using floats, just replace them with doubles, and you are good to go.

## 4.4.0

- 🚀 Feat: Revert BREAKING CHANGE and add addStage, removeStage and getStage again.
- 🚀 Feat: Add methods for counting sprites (countSprites, countSpritesOf), pens (countPens, countPensOf) and texts (countTexts, countTextsOf).
- 🚀 Feat: Add more methods for finding sprites (findSpritesOf), pens (findPensOf) and texts (findTextsOf) of a given class.
- 🚀 Feat: Add exit to Stage for easier access.
- 🚀 Feat: Add getDeltaTime to the Sprite and Stage class.
- 🚀 Feat: You can now stretch a backdrop to the window size by using `addBackdrop("name", "path", true)`.
- 🐛 Fix: Path resolution of assets
- 🐛 Fix: whenMouseMoved not working

## 4.3.0

- 🚀 Feat: Random can now create a new random unit vector (Random.randomVector2()).
- 🚀 Feat: You can now set the width of a text after calling the constructor (text.setWidth(40)).

## 4.2.1

- 🐛 Fix: AnimatedSprite throws an error, because the animationFrame is too high.

## 4.2.0

- 💥 BREAKING CHANGE: Replace addStage, removeStage and getStage in favour of setStage, which simplifies the handling of multiple stages.
- 🐛 Fix: First frame of an AnimatedSprite is skipped.

## 4.1.0

- 🚀 Feat: New extension tiled.
- 🐛 Fix: Fix isTouchingMousePointer.

## 4.0.2

- 🐛 Fix: Missing stamp method on Sprite

## 4.0.1

- 🐛 Fix: Scratch for Java Window not creating on Windows

## 4.0.0

- 💥 BREAKING CHANGE: Move (0, 0) to the center of the stage to be in-line with Scratch.
- 🚀 Feat: Add stamp method to the pen `pen.stamp()`. This is also available on a Sprite with `stamp()`, `stampToForeground()` or `stampToBackground()`.
- 🚀 Feat: Unify constructors of Window and Stage.
- 🚀 Feat: Load all tiles from a sprite sheet as costumes with addCostumes.
- 🚀 Feat: Add goToMousePointer to the sprite class.
- 🚀 Feat: Add goToRandomPosition to the sprite class.
- 🚀 Feat: switchCostumes now also accepts an integer for switching to a specific index.
- 🚀 Feat: Allow for custom sorting of sprites by providing a Comparator via the `setSorter`-method of the stage.
- 🐛 Fix: Random.randomInt did not work correct.

## 3.9.0

- 🚀 Feat: Add support for broadcast and when i receive blocks to the Sprite and the Stage class.

## 3.8.0

- 🚀 Feat: Support for vertical sprite sheets was added.
- 🚀 feat: Loading Animation got improved. The loading screen now shows the percentage of files, which are already loaded.

## 3.7.0

#scratch4j v3.7.0 got released:

- 💥 BREAKING CHANGE: AnimatedSprite, Hitbox, Text and Timer were moved into the org.openpatch.scratch.extensions package. Each extension got its package. For example, org.openpatch.scratch.extensions.animation. In the main package, we only want the classes with emulate the current Scratch behavior. This should lead to a smoother transition. The extensions package should be used, we the normal functionality is not enough.
- 🚀 Feat: New class Vector2 for the math extension. Every so often, it is easier to work with vectors. Therefore, this class got introduced. The Sprite class was updated to make use of the Vector2 class. You can, for example, call the move and setDirection methods with a Vector2.
- 🚀 Feat: New class Operators. This class has several simple methods for transforming data and working with mathematical operations – just like the Scratch operator blocks. For example, mapping values, lerping between values or using sine and cosine.
- 🚀 Feat: New class Random in the math extension. This class contains several methods for generating random numbers. This makes working with randomness outside a Sprite or Stage class easier.
- 🚀 Feat: Introduce the method ifOnEdgeBounce for the class Sprite. This method works like the if on edge, bounce-block in Scratch in is an alternative to the setOnEdgeBounce-method.
- 🚀 Feat: Introduce pointTowardsMousePointer and pointTowardsSprite methods for the class Sprite.
- 🐛 Fix: Jar files missing version information.
- 🐛 Fix: Speak and Think-bubble rendering. Speak and Think-bubbles now behave like they do in Scratch. Meaning, they will ever leave the Stage.
- 📝 Docs: Add more documentation

## 3.6.0

- 🚀 Feat: Add method `setTextSize` to the Text-class

```java
Text myText = new Text();
myText.setTextSize(48);
```

- 🚀 Feat: Allow custom fonts for Text-objects. Custom fonts can be added like a costume for a Sprite-object. Each Text-object does have a default font named `default`.

```java
Text myText = new Text();
myText.addFont("playful", "opensans.odt");
myText.switchFont("playful");

// get the text-object, which is used for the think and speak bubble
Text spriteText = new Sprite().getText();
```

- 📝 Docs: Add example `Shakespeare` which shows the usage of the new methods of the Text-class

## 3.5.0

- 💻 OS: Added Linux ARM 32-bit and ARM 64-bit versions

## 3.4.0

- 🧹 Chore: Update Processing core to version 4.2
- 💻 OS: Added MacOS aarch64 version

## 3.3.0

- 🚀 Feat: Added `isSoundPlaying` and `stopSound` to the Sprite and Stage Class.

## 3.2.2

- 🎨 Visual: Improve Pen rendering
- 🐎 Perf: Pen does not draw everything again, but only the last additions.
- 🐛 Fix: isTouchingSprite threw an error, if a sprite did not have a costume.
- 🐛 Fix: exit method not found for Window class.

## 3.2.1

- 🐛 Fix: Pen only drawing dots

## 3.2.0

- 🚀 Feat: Introducing the new class `Window`. Every Scratch for Java program can have on `Window`-object. A `Window`-object contains one stage object, which can be change during the runtime. This allows for using multiple stages in your program. For example, you could have one stage for the menu, one for the game and one for the credits.

```java
public class Prog {
    public static void main(String[] args) {
        Window myWindow = new Window();
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        myWindow.addStage("one", stage1);
        myWindow.addStage("two", stage2);

        myWindow.switchStage("two");
    }
}
```

- 🚀 Feat: The class Text became more usable as a standalone Drawable. You can now create a Text-object with the default constructor `new Text()`. The created Text-object will in `TextStyle.PLAIN`, meaning having nothing drawn around it and be freely placeable on the stage.

  - The background color can now be set by passing a Color-object. `setBackgroundColor(Color c)`
  - The text color can now be set by passing a Color-object. `setTextColor(Color c)`

- 🐎 Perf: Scratch for Java now used an OpenGL renderer, which increases the performance by many times.
- 🐎 Perf: The collision detection got improved.
- 💥 BREAKING: `setSize` got removed from the Stage class. Since the switch to the OpenGL Renderer you can only set the size of the Stage or Window at the constructor.

```java
public class MyStage extends Stage {
    public MyStage() {
        super(800, 400)
        // this.setSize(800, 400);
    }
}
```

## 3.1.1

- 🐛 Fix: setSize did not affect getWidth and getHeight.

## 3.1.0

- 🚀 Feat: The debug modus now shows the current FPS.
- 🚀 Feat: `isTouchingSprite(Class)`. IsTouchingSprite accepts also a Class. So you can check the collision with all objects of this class.

## 3.0.0

Scratch for Java will from now on focus on being a standalone library. Therefore, it can not be used in Processing anymore.

## 2.1.0

### Changes

- 🚀 Feat: Image, Text and Pen can now be added without being used in a Sprite. Example:

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class PenStandalone {
    public static void main(String[] args) {
        Stage s = new Stage(400, 400);
        Pen p = new Pen();
        p.down();
        p.setPosition(40, 40);
        p.setPosition(40, 100);
    }
}
```

- 🚀 Feat: AnimatedSprite and Sprite now support SpriteSheets. Example:

```java
import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class SpriteSheet {
    public static void main(String[] args) {
       Stage stage = new Stage() ;
       stage.add(new AnimatedBee());
    }
}

class AnimatedBee extends AnimatedSprite {
    public AnimatedBee() {
        this.addAnimation("idle", "bee_idle.png", 6, 36, 34);
    }

    public void run() {
        this.playAnimation("idle");
    }
}
```

- 🐛 Fix: Pen did not include the first point

### BREAKING CHANGES

- 💥 Prefix Scratch is removed. For exmaple: ScratchSprite -> Sprite, ScratchStage -> Stage
- 💥 getInstance and init got removed from Stage. You now have to instantiate a Stage like a normal Object `new Stage(this) // Processing` or `new Stage(400, 400) // Standalone`. Be aware that you can only have one Stage at a time.

## 2.0.0

- BREAKING CHANGE: Rename package to `org.openpatch.scratch`

## 1.15.0

- Add standalone version

## 1.14.1

- Improve Text rendering

## 1.14.0

- add think and say to Sprite
- add display to Stage
- add whenClicked to Sprite
- add whenBackdropSwitches to Sprite

## 1.13.0

- add stage.removeSprites
- add stage.findSprites
- use CopyOnWriteArrayList instead of ArrayList

## 1.4.0

- add more timer methods
- add a timer example
