---
type: major
---

Make the stage's render-loop methods private.

`Stage.draw`, `pre`, `keyEvent` and `mouseEvent` were public only so that
`Applet`, which lives in another package, could call them. They are now private.

`Stage` hands the render loop a private object implementing the new
`internal.StageHooks` interface instead. `Stage` does not implement the
interface itself, which would force the methods public again.

Nothing new became public in exchange. Every path that gives `Applet` a stage
already starts inside the core package — `Stage`'s constructor, and
`Window.setStage`, `transitionToStage` and `addStage` — so the hooks can be
package-private and passed through.

`Sprite` and `Stage` are down to 195 public methods, from 293 before v5.
