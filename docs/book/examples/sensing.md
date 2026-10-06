---
name: Sensing
---

# Sensing

An example which shows the usage of `isTouchingMousePointer` and custom hitboxes.

![sensing](/assets/sensing.gif)

## Run it here

Click the stage first, so that it gets the keys. Move one of the heroes with W, A,
S and D, turn it with R and let it walk forward with space; it says "Hit" while it
touches another hero. Point at a hero and it changes its costume. 0 and 1 zoom
out and in.

<!-- demo: sensing -->
:::onlineide{height="640px" libraries="scratch" speed="-1"}

@file dest="sprites/hero.png" src="/examples/sensing/sprites/hero.png"
@file dest="sprites/hero2.png" src="/examples/sensing/sprites/hero2.png"

```java Sensing.java

void main() {
  new Sensing();
}

class Sensing extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private UIHero uiH;
  // scratch4j:end fields

  public static Hero h, m;

  public Sensing() {
    super(800, 800);
    Window.getInstance().setDebug(true);
    h = new Hero();
    m = new MovableHero();
    this.add(h);
    this.add(m);

    // scratch4j:begin setup (managed by the stage designer)
    uiH = new UIHero();
    uiH.setPosition(300, 300);
    this.add(uiH);
    // scratch4j:end setup
  }

  public void run() {
    this.display("Move the hero with WASD and rotate him with R");

    if (isKeyPressed(KeyCode.DIGIT_0)) {
      this.getCamera().changeZoom(-1);
    }
    if (isKeyPressed(KeyCode.DIGIT_1)) {
      this.getCamera().changeZoom(1);
    }
  }
}

class Hero extends Sprite {
  public Hero() {
    super("hero", "sprites/hero.png");
    this.addCostume("hero2", "sprites/hero2.png");
    this.setSize(50);
    this.setDirection(45);
    this.move(80);

    this.setHitbox(0, 0, 300, 0, 300, 570, 0, 570, 150, 275);
  }

  public void run() {
    if (this.isTouchingMousePointer()) {
      this.switchCostume("hero2");
    } else {
      this.switchCostume("hero");
    }
  }
}

class MovableHero extends Hero {
  public MovableHero() {
    super();
    this.setPosition(-100, -100);
    this.setDirection(0);
    this.setHitbox(new Ellipse(0, 0, 615, 570));
  }

  public void run() {
    super.run();
    if (this.isKeyPressed(KeyCode.SPACE)) {
      this.move(1);
    }
    if (this.isKeyPressed(KeyCode.A)) {
      this.changeX(-1);
    }
    if (this.isKeyPressed(KeyCode.D)) {
      this.changeX(1);
    }
    if (this.isKeyPressed(KeyCode.W)) {
      this.changeY(1);
    }
    if (this.isKeyPressed(KeyCode.S)) {
      this.changeY(-1);
    }
    if (this.isKeyPressed(KeyCode.R)) {
      this.turnRight(1);
    }
    if (this.isTouchingSprite(Hero.class)) {
      this.say("Hit");
    } else {
      this.say(null);
    }
  }
}

/** A Hero pinned to the user interface layer. */
class UIHero extends Hero {
  public UIHero() {
    this.setUI(true);
  }
}
```

:::

To run it on your own computer, copy the folder `sprites` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/sensing) next to the program.

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/sensing
