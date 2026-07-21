---
type: major
---

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

## Migrating from v4

### One import

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

### Handlers are overridden, not registered

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

### Settings are methods, chosen before the window opens

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

### Methods that moved to a class of their own

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

### Overloads that made the wrong thing easy

- `broadcast(Object)` and `whenIReceive(Object)` are gone. Broadcasting a
  non-`String` used to call only the `Object` handler, so a sprite overriding
  `whenIReceive(String)` silently heard nothing. Messages are names, as in
  Scratch.
- `whenAddedToStage(Stage)` and `whenRemovedFromStage(Stage)` are gone —
  override the no-argument forms; the argument is what `getStage()` returns.
- `setHitbox(Hitbox)` and `setHitbox(double[], double[])` are gone.
  `setHitbox(Shape)` and `setHitbox(double... points)` cover both.

### Interface parts have their own class

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

### Fewer constructors

`Stage` had seven and `Window` eight, four of each differing only by a leading
`boolean fullScreen`. `Stage` keeps `Stage()`, `Stage(width, height)` and
`Stage(width, height, assets)`; `Window` keeps those three plus
`Window(assets)`. Full screen is `Window.useFullScreen()`.

### Plumbing that was public only to cross a package

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

## Behaviour that changed without the code changing

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

## What is new

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

## Fixes

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

## Documentation

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
