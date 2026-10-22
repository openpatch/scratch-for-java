# LWJGL spike

Checks that LWJGL can give the library the window it needs
before the renderer is rewritten. Standalone Maven project, not part of the library build.

What it does: `Main` opens a 480×360 window and returns, as `new Window(480, 360)` does today. The
window's own thread draws a turning cat into a 4× multisampled stage layer, letterboxes it into
the window, runs at 60 FPS without vsync and prints the input it gets.

```sh
cd spikes/lwjgl
mvn -q compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
java --enable-native-access=ALL-UNNAMED -cp "target/classes:$(cat target/cp.txt)" spike.Main
```

On Windows, use `;` instead of `:` in the classpath. On macOS, start it **without**
`-XstartOnFirstThread`: whether `glfw_async` makes that work is the main question there.

| Property | Effect |
|---|---|
| `spike.exitAfter=N` | close after N seconds |
| `spike.capture=file.png` | save the stage at frame 60, as `Pixels` would read it |
| `spike.hidden=true` | never show the window (Studio's stage preview) |
| `spike.platform=x11` | force X11; needed under `xvfb-run` in a Wayland session |
| `spike.vsync=true` | vsync on top of the 60 FPS clock |
| `spike.shader=path` | a fragment shader body in the 7.0 format (default `src/main/resources/pixelate.frag`) |
| `spike.studentShader=true` | start with that shader on (key P toggles it) |
| `spike.costume=path` | the costume to draw |

Things to try by hand:

- Type on a German keyboard: `Z` must map to KeyCode 90, `ä` must arrive as typed text, holding a
  key must not repeat presses.
- Click without moving (click) and with moving (drag-release); scroll the wheel.
- Resize the window or drag it to a scaled monitor: the stage stays letterboxed and click
  coordinates stay in stage units.
- macOS: the window opens, `main` returns, and the program keeps running.
- Windows at 150 %: compare the window, framebuffer and content scale it prints.

## Further checks (Phase 1, 2026-10-21)

All run the same way as `spike.Main`, with another main class.

| Class | Checks |
|---|---|
| `spike.GameLoopCheck` | P1-04, P1-05, P1-07: window thread + game thread in lockstep, the tick clock (snapping, catch-up), a watchdog, `whenExits` on the game thread, pen strokes on a CPU canvas with changed regions uploaded. Prints fps, ticks per frame, slowest swap and slowest batch every second. |
| `spike.Launcher <main class>` | P1-03: the window loop on the process's first thread, the learner's `main` on a second one (Studio's route). |
| `spike.AudioCheck` | P1-08: OpenAL Soft device, decoding (own WAV parser, stb_vorbis), source pool, start latency, overlapping copies, gain, output-device switch. |
| `spike.TextCheck` | P1-06: stb_truetype metrics, own line breaking, layout at the nominal size, rasterised at several scales into `target/text/*.png`. |

`GameLoopCheck` properties: `spike.hangAt=N` (learner code never returns from tick N),
`spike.vsync=true`, `spike.speed=2` (game speed), `spike.exitAfter=N` (also while learner code
hangs), `spike.capture=file.png` (the presented frame at tick 120), `spike.platform=x11`.
`AudioCheck` properties: `audio.wav`, `audio.ogg`, `audio.switchSeconds=N` (loops a sound for N
seconds and reports output-device changes).

### On a Mac (gate G1)

`.github/workflows/macos-spike.yml` runs the steps below on GitHub's macOS runners (Apple Silicon
and Intel) when started by hand; a runner without OpenGL makes that run inconclusive. By hand,
from `spikes/lwjgl`, with `CP="target/classes:$(cat target/cp.txt)"`:

1. Route 1, plain `java` (`glfw_async`):
   `java --enable-native-access=ALL-UNNAMED -cp "$CP" -Dspike.exitAfter=5 spike.GameLoopCheck`
   — expect `main: returns`, a window with a circling dot drawing a blue ellipse, ~60 fps lines,
   `window: closed`.
2. Route 2, Studio's (gate G1):
   `java -XstartOnFirstThread --enable-native-access=ALL-UNNAMED -cp "$CP" spike.Launcher spike.GameLoopCheck`
   — expect `window: runs on thread 'main'`, the same window; close it with the red button or
   Cmd+Q: `learner: whenExits ran on scratch4j-game`.
3. Route 2 with a hang: add `-Dspike.hangAt=60`. After 2 s the watchdog names
   `MySprite.run (MySprite.java:29)`; the window must still move, resize and close (red button),
   ending with `did not answer, skipped after 1000 ms`. No beach ball.
4. Retina: the window must be sharp and letterboxed, the content scale printed by `spike.Main`
   should be 2.0.
5. `spike.AudioCheck`: default device, overlap count > 1; with `-Daudio.switchSeconds=20`, plug
   in or remove headphones while it loops.

Write down macOS version, chip, Java version and the output of each step.
