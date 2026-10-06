---
name: Rainbow Vine
---

# Rainbow Vine

An example which makes use of mouse events and timers.

![rainbow vine example](/assets/rainbow_vine.gif)

## Run it here

Move the mouse across the stage.

<!-- demo: rainbowVine -->
:::onlineide{height="560px" libraries="scratch"}

```java RainbowVine.java

void main() {
  new RainbowVine();
}

class RainbowVine extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private VineSprite vineSprite;
  // scratch4j:end fields

  public RainbowVine() {
    super(600, 400);
    this.setColor(0, 0, 0);
    // scratch4j:begin setup (managed by the stage designer)
    vineSprite = new VineSprite();
    this.add(vineSprite);
    // scratch4j:end setup
  }
}

class VineSprite extends Sprite {
  public VineSprite() {
    this.getPen().down();
    this.getPen().setSize(3);
    this.getPen().setColor(120);
  }

  public void run() {
    this.setPosition(this.getMouseX(), this.getMouseY());
    this.turnRight(5);

    if (this.getTimer().everyMillis(60)) {
      this.getStage().add(new LeafSprite(this));
    }
  }
}

class LeafSprite extends Sprite {
  VineSprite vine;

  public LeafSprite(VineSprite vine) {
    this.vine = vine;
    this.getPen().setSize(2);
    this.setDirection(vine.getDirection());
    this.getPen().setColor(vine.getPen().getColor());
    vine.getPen().changeColor(2);
    this.setPosition(vine.getX(), vine.getY());
    this.getPen().down();
  }

  public void run() {
    this.turnRight(5);
    this.move(5);
    this.getPen().changeSize(1);
    if (this.getTimer().afterMillis(200)) {
      this.remove();
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/rainbowVine
