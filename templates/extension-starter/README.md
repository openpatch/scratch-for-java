# Extension starter

A complete Scratch for Java extension to copy and change: a countdown in the corner of the stage,
and a `whenTimeIsUp()` for the stage and the sprites when it reaches zero.

```java
public class Level extends Stage implements WhenTimeIsUp {
  public Level() {
    Countdown.of(this).start(30);
  }

  public void whenTimeIsUp() {
    this.display("Time is up!");
  }
}
```

## The files

| File | What it is |
|---|---|
| `Countdown.java` | What learners use. One per stage, made on first use (`stage.extension(...)`). |
| `WhenTimeIsUp.java` | A listener interface: a stage or sprite implements it to be told. |
| `CountdownExtension.java` | The extension: counts before each tick, calls the listeners, draws the corner. |
| `META-INF/services/org.openpatch.scratch.extension.Extension` | Names the extension class, so the library finds the jar's extension by itself. |
| `CountdownTest.java` | The countdown's logic, tested without a window. |

## Making your own

1. Rename `com.example` / `scratch-countdown` in `pom.xml`, the package, and the class name in the
   services file.
2. Set `scratch.version` in `pom.xml` to the Scratch for Java you build against.
3. Give the extension a unique `id()` (your package is a good one) and say in `requires()` which
   Scratch for Java it needs at least; older ones skip it with a message.
4. `mvn package`. The jar in `target/` goes into a project's `+libs` folder (Studio) or onto its
   classpath. Nothing else to do: the library finds it. A project can also register it by hand
   with `Window.use(new CountdownExtension())` before `new Window(...)`.

## What an extension may do

Everything is in `ExtensionContext` (see its Javadoc):

- **Hooks on the game thread**, where learner code runs: `onStageCreated`, `onStageDisposed`,
  `beforeTick`, `onKey`, `onMouse` (observing only), `onExit`, `onLearnerError`.
- **Drawing** through a `Canvas` into a layer (`addLayerDrawer(Layer, ...)`) or above everything
  (`addOverlayDrawer`). There is no OpenGL: a canvas records images, polygons, outlines and text.
- **Listener interfaces**: `dispatch(stage, SomeInterface.class, call)` calls the stage and every
  sprite on it that implements the interface, in drawing order.
- **Devices**: `onInputPoll` runs on the window thread (poll GLFW there, e.g. a gamepad; no GLFW
  callbacks); `input(key)` reads what was polled, on the game thread.
- **State per stage or sprite**: `stage.extension(Type.class, Type::new)`, the same on `Sprite`.
- **More**: `runOnGameThread`, `afterFrame` (the finished picture), `registerImageFormat`,
  `getTickCount`, `isLoading`.

And what it may not:

- use anything from `org.openpatch.scratch.internal` (it changes without notice);
- call `getAll()`, `find(...)` or `getTouchingSprites(...)`: in the NRW build of the library they
  return a different type, so the extension would fail there with a `NoSuchMethodError`. Reach the
  sprites through `dispatch` instead.
