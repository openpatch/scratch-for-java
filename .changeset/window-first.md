---
type: major
---

💥 BREAKING CHANGE: A stage no longer opens the window itself. Create the window
first and put the stage into it:

```java
Window myWindow = new Window(480, 360);
myWindow.setStage(new MyStage());
```

`new Stage(width, height)` and `new Stage(width, height, assets)` are gone: a
stage is always as big as the window it is shown in, so the size (and the assets
folder) go to `new Window(...)`. Remove `super(width, height)` from your stages.
A stage made before there is a window says how to fix it, and a window nobody
put a stage into says so too.
