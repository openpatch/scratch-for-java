---
name: Random Dot
---

# Random Dot

An example which makes use of timers.

![random dot](/assets/random_dot.gif)

## Run it here

A dot every tenth of a second, in the next colour along, wherever the sprite
happened to land.

:::onlineide{height="640px" libraries="scratch"}

```java RandomDot.java

void main() {
  new RandomDot();
}

class RandomDot extends Stage {
  public RandomDot() {
    super(800, 600);
    this.add(new RandomDotSprite());
  }
}

class RandomDotSprite extends Sprite {
  public void run() {
    if (this.getTimer().everyMillis(100)) {
      this.getPen().up();
      this.getPen().setSize(20);
      this.goToRandomPosition();
      this.getPen().changeColor(2);
      this.getPen().down();
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/randomDot
