---
name: Stress Test
---

# Stress Test

How many sprites can you display? And how does it effect the frame rate and memory usage of your scratch. Test it with this example.

It also shows how to animate a sprite.

![stress_test](/assets/stress_test.gif)

## Run it here

Hundreds of animated sprites at once. Point at one and it walks; press space and
all of them run. How fast does it still go in your browser?

:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="assets/dino/Idle (1).png" src="/examples/stress-test/assets/dino/Idle (1).png"
@file dest="assets/dino/Idle (2).png" src="/examples/stress-test/assets/dino/Idle (2).png"
@file dest="assets/dino/Idle (3).png" src="/examples/stress-test/assets/dino/Idle (3).png"
@file dest="assets/dino/Idle (4).png" src="/examples/stress-test/assets/dino/Idle (4).png"
@file dest="assets/dino/Idle (5).png" src="/examples/stress-test/assets/dino/Idle (5).png"
@file dest="assets/dino/Idle (6).png" src="/examples/stress-test/assets/dino/Idle (6).png"
@file dest="assets/dino/Idle (7).png" src="/examples/stress-test/assets/dino/Idle (7).png"
@file dest="assets/dino/Idle (8).png" src="/examples/stress-test/assets/dino/Idle (8).png"
@file dest="assets/dino/Idle (9).png" src="/examples/stress-test/assets/dino/Idle (9).png"
@file dest="assets/dino/Idle (10).png" src="/examples/stress-test/assets/dino/Idle (10).png"
@file dest="assets/dino/Run (1).png" src="/examples/stress-test/assets/dino/Run (1).png"
@file dest="assets/dino/Run (2).png" src="/examples/stress-test/assets/dino/Run (2).png"
@file dest="assets/dino/Run (3).png" src="/examples/stress-test/assets/dino/Run (3).png"
@file dest="assets/dino/Run (4).png" src="/examples/stress-test/assets/dino/Run (4).png"
@file dest="assets/dino/Run (5).png" src="/examples/stress-test/assets/dino/Run (5).png"
@file dest="assets/dino/Run (6).png" src="/examples/stress-test/assets/dino/Run (6).png"
@file dest="assets/dino/Run (7).png" src="/examples/stress-test/assets/dino/Run (7).png"
@file dest="assets/dino/Run (8).png" src="/examples/stress-test/assets/dino/Run (8).png"
@file dest="assets/dino/Walk (1).png" src="/examples/stress-test/assets/dino/Walk (1).png"
@file dest="assets/dino/Walk (2).png" src="/examples/stress-test/assets/dino/Walk (2).png"
@file dest="assets/dino/Walk (3).png" src="/examples/stress-test/assets/dino/Walk (3).png"
@file dest="assets/dino/Walk (4).png" src="/examples/stress-test/assets/dino/Walk (4).png"
@file dest="assets/dino/Walk (5).png" src="/examples/stress-test/assets/dino/Walk (5).png"
@file dest="assets/dino/Walk (6).png" src="/examples/stress-test/assets/dino/Walk (6).png"
@file dest="assets/dino/Walk (7).png" src="/examples/stress-test/assets/dino/Walk (7).png"
@file dest="assets/dino/Walk (8).png" src="/examples/stress-test/assets/dino/Walk (8).png"
@file dest="assets/dino/Walk (9).png" src="/examples/stress-test/assets/dino/Walk (9).png"
@file dest="assets/dino/Walk (10).png" src="/examples/stress-test/assets/dino/Walk (10).png"
@file dest="assets/knight/Idle (1).png" src="/examples/stress-test/assets/knight/Idle (1).png"
@file dest="assets/knight/Idle (2).png" src="/examples/stress-test/assets/knight/Idle (2).png"
@file dest="assets/knight/Idle (3).png" src="/examples/stress-test/assets/knight/Idle (3).png"
@file dest="assets/knight/Idle (4).png" src="/examples/stress-test/assets/knight/Idle (4).png"
@file dest="assets/knight/Idle (5).png" src="/examples/stress-test/assets/knight/Idle (5).png"
@file dest="assets/knight/Idle (6).png" src="/examples/stress-test/assets/knight/Idle (6).png"
@file dest="assets/knight/Idle (7).png" src="/examples/stress-test/assets/knight/Idle (7).png"
@file dest="assets/knight/Idle (8).png" src="/examples/stress-test/assets/knight/Idle (8).png"
@file dest="assets/knight/Idle (9).png" src="/examples/stress-test/assets/knight/Idle (9).png"
@file dest="assets/knight/Idle (10).png" src="/examples/stress-test/assets/knight/Idle (10).png"
@file dest="assets/knight/Run (1).png" src="/examples/stress-test/assets/knight/Run (1).png"
@file dest="assets/knight/Run (2).png" src="/examples/stress-test/assets/knight/Run (2).png"
@file dest="assets/knight/Run (3).png" src="/examples/stress-test/assets/knight/Run (3).png"
@file dest="assets/knight/Run (4).png" src="/examples/stress-test/assets/knight/Run (4).png"
@file dest="assets/knight/Run (5).png" src="/examples/stress-test/assets/knight/Run (5).png"
@file dest="assets/knight/Run (6).png" src="/examples/stress-test/assets/knight/Run (6).png"
@file dest="assets/knight/Run (7).png" src="/examples/stress-test/assets/knight/Run (7).png"
@file dest="assets/knight/Run (8).png" src="/examples/stress-test/assets/knight/Run (8).png"
@file dest="assets/knight/Run (9).png" src="/examples/stress-test/assets/knight/Run (9).png"
@file dest="assets/knight/Run (10).png" src="/examples/stress-test/assets/knight/Run (10).png"
@file dest="assets/knight/Walk (1).png" src="/examples/stress-test/assets/knight/Walk (1).png"
@file dest="assets/knight/Walk (2).png" src="/examples/stress-test/assets/knight/Walk (2).png"
@file dest="assets/knight/Walk (3).png" src="/examples/stress-test/assets/knight/Walk (3).png"
@file dest="assets/knight/Walk (4).png" src="/examples/stress-test/assets/knight/Walk (4).png"
@file dest="assets/knight/Walk (5).png" src="/examples/stress-test/assets/knight/Walk (5).png"
@file dest="assets/knight/Walk (6).png" src="/examples/stress-test/assets/knight/Walk (6).png"
@file dest="assets/knight/Walk (7).png" src="/examples/stress-test/assets/knight/Walk (7).png"
@file dest="assets/knight/Walk (8).png" src="/examples/stress-test/assets/knight/Walk (8).png"
@file dest="assets/knight/Walk (9).png" src="/examples/stress-test/assets/knight/Walk (9).png"
@file dest="assets/knight/Walk (10).png" src="/examples/stress-test/assets/knight/Walk (10).png"
@file dest="assets/ninja/Idle (1).png" src="/examples/stress-test/assets/ninja/Idle (1).png"
@file dest="assets/ninja/Idle (2).png" src="/examples/stress-test/assets/ninja/Idle (2).png"
@file dest="assets/ninja/Idle (3).png" src="/examples/stress-test/assets/ninja/Idle (3).png"
@file dest="assets/ninja/Idle (4).png" src="/examples/stress-test/assets/ninja/Idle (4).png"
@file dest="assets/ninja/Idle (5).png" src="/examples/stress-test/assets/ninja/Idle (5).png"
@file dest="assets/ninja/Idle (6).png" src="/examples/stress-test/assets/ninja/Idle (6).png"
@file dest="assets/ninja/Idle (7).png" src="/examples/stress-test/assets/ninja/Idle (7).png"
@file dest="assets/ninja/Idle (8).png" src="/examples/stress-test/assets/ninja/Idle (8).png"
@file dest="assets/ninja/Idle (9).png" src="/examples/stress-test/assets/ninja/Idle (9).png"
@file dest="assets/ninja/Idle (10).png" src="/examples/stress-test/assets/ninja/Idle (10).png"
@file dest="assets/ninja/Run (1).png" src="/examples/stress-test/assets/ninja/Run (1).png"
@file dest="assets/ninja/Run (2).png" src="/examples/stress-test/assets/ninja/Run (2).png"
@file dest="assets/ninja/Run (3).png" src="/examples/stress-test/assets/ninja/Run (3).png"
@file dest="assets/ninja/Run (4).png" src="/examples/stress-test/assets/ninja/Run (4).png"
@file dest="assets/ninja/Run (5).png" src="/examples/stress-test/assets/ninja/Run (5).png"
@file dest="assets/ninja/Run (6).png" src="/examples/stress-test/assets/ninja/Run (6).png"
@file dest="assets/ninja/Run (7).png" src="/examples/stress-test/assets/ninja/Run (7).png"
@file dest="assets/ninja/Run (8).png" src="/examples/stress-test/assets/ninja/Run (8).png"
@file dest="assets/ninja/Run (9).png" src="/examples/stress-test/assets/ninja/Run (9).png"
@file dest="assets/ninja/Run (10).png" src="/examples/stress-test/assets/ninja/Run (10).png"
@file dest="assets/ninja/Walk (1).png" src="/examples/stress-test/assets/ninja/Walk (1).png"
@file dest="assets/ninja/Walk (2).png" src="/examples/stress-test/assets/ninja/Walk (2).png"
@file dest="assets/ninja/Walk (3).png" src="/examples/stress-test/assets/ninja/Walk (3).png"
@file dest="assets/ninja/Walk (4).png" src="/examples/stress-test/assets/ninja/Walk (4).png"
@file dest="assets/ninja/Walk (5).png" src="/examples/stress-test/assets/ninja/Walk (5).png"
@file dest="assets/ninja/Walk (6).png" src="/examples/stress-test/assets/ninja/Walk (6).png"
@file dest="assets/ninja/Walk (7).png" src="/examples/stress-test/assets/ninja/Walk (7).png"
@file dest="assets/ninja/Walk (8).png" src="/examples/stress-test/assets/ninja/Walk (8).png"
@file dest="assets/ninja/Walk (9).png" src="/examples/stress-test/assets/ninja/Walk (9).png"
@file dest="assets/ninja/Walk (10).png" src="/examples/stress-test/assets/ninja/Walk (10).png"
@file dest="assets/outback.png" src="/examples/stress-test/assets/outback.png"

```java StressTest.java

void main() {
  Window myWindow = new Window(1200, 800, "assets");
  myWindow.setStage(new StressTest());
}

class StressTest extends Stage {

  private static int dinos = 180;
  private static int knights = 180;
  private static int ninjas = 180;

  public StressTest() {
    this.addBackdrop("outback", "assets/outback.png");

    for (int i = 0; i < dinos; i++) {
      this.add(new Dino());
    }
    for (int i = 0; i < knights; i++) {
      this.add(new Knight());
    }
    for (int i = 0; i < ninjas; i++) {
      this.add(new Ninja());
    }
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      var figures = this.find(Figure.class);
      for (var figure : figures) {
        ((Figure) figure).state = FigureState.RUN;
      }
    }
  }
}

class Figure extends AnimatedSprite {
  public FigureState state;
  int tintColor;
  boolean hasTouchedEdge = false;

  public Figure(String pathBase, int idleAnimations, int runAnimations, int walkAnimations) {
    state = FigureState.IDLE;

    this.addAnimation("idle", pathBase + "Idle (%d).png", idleAnimations);
    this.addAnimation("run", pathBase + "Run (%d).png", runAnimations);
    this.addAnimation("walk", pathBase + "Walk (%d).png", walkAnimations);

    this.tintColor = (int) this.pickRandom(0, 256);
    this.setPosition(this.pickRandom(-200, 200), this.pickRandom(-200, 200));
    this.setDirection(this.pickRandom(0, 360));
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.setTint(this.tintColor);

    if (isTouchingMousePointer()) {
      state = FigureState.WALK;
    } else if (state == FigureState.WALK) {
      state = FigureState.IDLE;
    }

    if (isTouchingEdge() && !hasTouchedEdge) {
      hasTouchedEdge = true;
    } else if (!isTouchingEdge() && hasTouchedEdge) {
      hasTouchedEdge = false;
    }

    switch (state) {
      case IDLE:
        this.setAnimationInterval(100);
        this.playAnimation("idle");
        break;
      case RUN:
        this.setAnimationInterval(50);
        this.playAnimation("run");
        move(4);
        break;
      case WALK:
        this.setAnimationInterval(100);
        this.playAnimation("walk");
        move(2);
        break;
    }
  }
}

enum FigureState {
  IDLE,
  RUN,
  WALK
}

class Dino extends Figure {
  public Dino() {
    super("assets/dino/", 10, 8, 10);
  }
}

class Knight extends Figure {
  public Knight() {
    super("assets/knight/", 10, 10, 10);
  }
}

class Ninja extends Figure {

  public Ninja() {
    super("assets/ninja/", 10, 10, 10);
  }
}
```

:::

To run it on your own computer, copy the folder `assets` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/stressTest) next to the program.

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/stressTest

