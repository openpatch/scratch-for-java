---
type: major
---

Remove eight overloads that made the wrong thing easy.

- **`broadcast(Object)` and `whenIReceive(Object)`** are gone from `Sprite` and
  `Stage`. Having them beside the `String` versions meant broadcasting anything
  other than a `String` called only the `Object` handler, so a sprite that
  overrode `whenIReceive(String)` silently heard nothing. Messages are names, as
  in Scratch:

  ```java
  this.broadcast("game-over");
  ```

- **`whenAddedToStage(Stage)` and `whenRemovedFromStage(Stage)`** are gone. One
  event had two override points, and the argument is what `getStage()` already
  returns. Override the no-argument form.

- **`setHitbox(Hitbox)` and `setHitbox(double[], double[])`** are gone.
  `setHitbox(Shape)` and `setHitbox(double... points)` cover both ideas:

  ```java
  this.setHitbox(new Ellipse(0, 0, 615, 570));
  this.setHitbox(0, 0, 10, 0, 10, 10, 0, 10);
  ```

`Sprite` and `Stage` are down to 184 public methods, from 293 before v5.
