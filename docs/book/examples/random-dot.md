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
  Window myWindow = new Window(800, 600);
  myWindow.setStage(new RandomDot());
}

class RandomDot extends Stage {

  // begin stage designer
  private RandomDotSprite randomDotSprite;
  // end stage designer

  public RandomDot() {
    // begin stage designer
    randomDotSprite = new RandomDotSprite();
    this.add(randomDotSprite);
    // end stage designer
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
