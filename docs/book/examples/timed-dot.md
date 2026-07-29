---
name: Timed Dot
---

# Timed Dot

An example which makes use of timers.

![timed dot](/assets/timed_dot_60.gif)

## Run it here

Two timers drawing at different rates, and a third on the stage that wipes
everything every 2.4 seconds.

:::onlineide{height="460px" libraries="scratch"}

```java TimedDot.java

void main() {
  new TimedDot();
}

class TimedDot extends Stage {
  public TimedDot() {
    super(400, 200);
    this.add(new DotSprite());
  }

  public void run() {
    if (this.getTimer().everyMillis(2400)) {
      this.eraseAll();
    }
  }
}

class DotSprite extends Sprite {
  public DotSprite() {
    super();
    this.getPen().setSize(40);
    this.setDirection(65);
  }

  public void run() {
    this.ifOnEdgeBounce();
    if (this.getTimer("timer2").everyMillis(600)) {
      this.getPen().setColor(200);
      this.move(20);
      this.getPen().down();
      this.getPen().up();
    }
    if (this.getTimer("timer1").everyMillis(1200)) {
      this.getPen().setColor(100);
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
