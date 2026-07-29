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

## Run it here

Hold the mouse down anywhere and the alien glides to it over one second. The
coin needs nobody: it patrols between the two corners on its own.

:::onlineide{height="560px" libraries="scratch"}

```java Glide.java

void main() {
  new Glide();
}

class Glide extends Stage {

  public Glide() {
    super(600, 400);
    this.addBackdrop("background");
    this.add(new Walker());
    this.add(new Patroller());
  }
}

/** Glides to wherever the mouse was clicked. */
class Walker extends Sprite {
  public Walker() {
    this.addCostume("alienGreen_stand");
    this.setSize(35);
  }

  public void whenClicked() {
    // whenClicked fires on the sprite; glide to the middle from wherever it is
    this.glide(1, 0, 0);
  }

  public void run() {
    if (this.isMouseDown() && !this.isGliding()) {
      this.glide(1, this.getMouseX(), this.getMouseY());
    }
  }
}

/** Glides back and forth between two corners, for ever. */
class Patroller extends Sprite {
  private boolean toTheRight = true;

  public Patroller() {
    this.addCostume("coinGold");
    this.setSize(50);
    this.setPosition(-220, 140);
  }

  public void run() {
    if (this.isGliding()) {
      return;
    }
    if (this.toTheRight) {
      this.glide(2, 220, 140);
    } else {
      this.glide(2, -220, 140);
    }
    this.toTheRight = !this.toTheRight;
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/glide
