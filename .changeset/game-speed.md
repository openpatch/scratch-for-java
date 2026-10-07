---
type: minor
---

The game can be paused, stepped and slowed down: `Window.getInstance().pause()`,
`resume()`, `isPaused()`, `step()` (one frame while paused) and
`setGameSpeed(0.5)` for half speed. `run()`, timers, gliding, animations and
timed speech bubbles all follow, so the whole game stands still or runs in slow
motion while the window keeps drawing; keys and clicks still arrive, so a key
can resume the game. `Timer.millis()` and `getDeltaTime()` count game time,
which is the real time as long as the game is neither paused nor sped up.
