---
name: Glide
---

# Glide

Sprites sliding to a new place with `glide()` instead of jumping there. Hold the
mouse down and the alien glides to it over one second; the coin glides between
two corners for as long as the program runs.

![an alien and a coin gliding across the stage](/assets/glide.gif)

`glide()` does not hold the sprite up, so `run()` keeps being called while it is
on its way. `isGliding()` says whether it has arrived, which is how the coin
knows to turn around:

```java
public void run() {
  if (this.isGliding()) {
    return;
  }
  this.glide(2, 220, 140);
}
```

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/glide
