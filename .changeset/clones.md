---
type: minor
---

Clones work like Scratch's. `clone()` makes a clone of the same class (a clone
of a `Cat` is a `Cat`, with a copy of its variables), puts it on the stage right
behind the original and runs its new `whenStartsAsClone()`, "when I start as a
clone". `deleteThisClone()` removes a clone and leaves the original alone, and
`isClone()` tells them apart.

```java
public void whenStartsAsClone() {
  this.goToRandomPosition();
}
```

Before, `clone()` returned a plain `Sprite` that was not on the stage, so it
neither showed up nor ran the class's `run()`. A copy made with `new
Sprite(other)` also drew with the other sprite's pen; it has its own now.
