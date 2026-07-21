---
type: major
---

Move sorting and pixel access off `Stage`, and make timers create themselves.

Third pass of the v5 API slimming, 10 methods.

- **`getSorting()`** replaces `setSorter`, `enableYSort`, `disableSort` and
  `isSortEnabled`:

  ```java
  this.getSorting().byY();
  ```

- **`getPixels()`** now returns a `Pixels` object instead of an `int[]`, and
  covers the background and foreground layers too:

  ```java
  int[] colours = this.getPixels().main();
  ```

- **`addTimer` and `removeTimer` are gone.** `getTimer(name)` creates the timer
  the first time it is asked for, so a named timer no longer has to be declared
  before it can be used:

  ```java
  if (this.getTimer("countdown").everyMillis(600)) { ... }
  ```

- **`Stage.setTextureSampling` is gone**; it duplicated
  `Window.TEXTURE_SAMPLING_MODE`.

`Sprite` and `Stage` are down to 208 public methods, from 293 before v5.
