---
type: major
---

Cut the constructor count from 15 to 7.

`Stage` had seven constructors and `Window` eight, four of each differing only by
a leading `boolean fullScreen`. In BlueJ's "new Stage()" dialog that is the first
thing a beginner sees.

Full screen is now a setting, next to the ones that were already settings:

```java
Window.FULL_SCREEN = true;
new MyStage();
```

`Stage` keeps `Stage()`, `Stage(width, height)` and `Stage(width, height,
assets)`. `Window` keeps those three plus `Window(assets)`. It has to be set
before the window or the first stage is created, like
`Window.TEXTURE_SAMPLING_MODE`.

This also removed a duplicated copy of the "only one Window" check, which had
been maintained in two constructors at once.
