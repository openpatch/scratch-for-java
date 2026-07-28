---
type: minor
---

`Window.addStage`, `switchStage` and `removeStage` are gone.

Deprecated since 4.0.0 in favour of `setStage`, which holds the stage you give it
rather than a name you have to remember, and `transitionToStage` when the change
should fade.

    myWindow.addStage("level", level);   // was
    myWindow.switchStage("level");
    myWindow.setStage(level);            // now

`removeStage` needs no replacement: a stage the window is not showing does not
run.
