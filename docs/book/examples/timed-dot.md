---
name: Timed Dot
---

# Timed Dot

An example which makes use of timers.

![timed dot](/assets/timed_dot_60.gif)

## Run it here

Two timers drawing at different rates, and a third on the stage that wipes
everything every 2.4 seconds.

<!-- demo: timedDot -->
:::onlineide{height="460px" libraries="scratch"}

```java TimedDot.java

public class TimedDot extends Stage {

  // begin stage designer
  private DotSprite dotSprite;
  // end stage designer

  public TimedDot() {
    // begin stage designer
    dotSprite = new DotSprite();
    this.add(dotSprite);
    // end stage designer
  }

  public void run() {
    if (this.getTimer().every(2.4)) {
      this.eraseAll();
    }
  }

  public static void main(String[] args) {
    Window myWindow = new Window(400, 200);
    myWindow.setStage(new TimedDot());
  }
}

class DotSprite extends Sprite {
  private final Timer timerTimer1 = new Timer();
  private final Timer timerTimer2 = new Timer();
  public DotSprite() {
    super();
    this.getPen().setSize(40);
    this.setDirection(65);
  }

  public void run() {
    this.ifOnEdgeBounce();
    if (this.timerTimer2.every(0.6)) {
      this.getPen().setColor(78);
      this.move(20);
      this.getPen().down();
      this.getPen().up();
    }
    if (this.timerTimer1.every(1.2)) {
      this.getPen().setColor(39);
      this.move(20);
      this.getPen().down();
      this.getPen().up();
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/timedDot
