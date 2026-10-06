---
name: Random Dot
---

# Random Dot

An example which makes use of timers.

![random dot](/assets/random_dot.gif)

## Run it here

A dot every tenth of a second, in the next colour along, wherever the sprite
happened to land.

<!-- demo: randomDot -->
:::onlineide{height="640px" libraries="scratch"}

```java RandomDot.java

void main() {
  new RandomDot();
}

class RandomDot extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private RandomDotSprite randomDotSprite;
  // scratch4j:end fields

  public RandomDot() {
    super(800, 600);
    // scratch4j:begin setup (managed by the stage designer)
    randomDotSprite = new RandomDotSprite();
    this.add(randomDotSprite);
    // scratch4j:end setup
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
