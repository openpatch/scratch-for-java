---
type: minor
---

Fill three more gaps in the block palette, with demos for each.

- **`glide(seconds, x, y)`** slides a sprite to a place instead of jumping
  there, with `isGliding()` to tell whether it has arrived. Like `ask`, it does
  not hold the sprite up: `run()` keeps being called on the way.
- **`setVolume`, `changeVolume`, `getVolume`** on `Sprite` and `Stage`. `Sound`
  had volume all along, it was simply never reachable. Values are in percent,
  as in Scratch, and are kept between 0 and 100.
- **`Stage.waitUntil(condition)`** waits for something to become true. Like
  `wait(millis)` it holds up only the code that calls it, so it belongs in a
  constructor rather than in `run()`.

New demos, none of which need an asset file:

- `demos/quiz` — asks three questions and reacts to the answers
- `demos/glide` — an alien that glides to wherever you click, and a coin
  patrolling between two corners
- `demos/volume` — space to play, arrow keys to change the volume

**`touching color` was attempted and dropped.** It needs the whole screen read
back off the graphics card, which can only be done while the stage is drawing
and costs a full-screen copy every frame. That is too slow for the machines this
library is meant for. `getPixels()` carries the same limitation, now written
down in its documentation.
