---
type: major
---

💥 BREAKING CHANGE: `Window.addStage`, `switchStage` and `removeStage` are gone.

They were deprecated in 4.0.0 in favour of `setStage`, which holds the stage you
give it rather than a name you have to remember, and `transitionToStage` when you
want the change to fade. Keeping them meant the window carried a second, parallel
list of stages that nothing else in the library ever looked at.

Replace

```java
myWindow.addStage("menu", menu);
myWindow.addStage("level", level);
myWindow.switchStage("level");
```

with a variable and one call:

```java
Stage menu = new Stage();
Stage level = new Stage();
myWindow.setStage(level);
```

`removeStage` has no replacement and needs none: a stage the window is not
showing is not running, and one nothing refers to any more is collected.
