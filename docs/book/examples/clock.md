---
name: Clock
---

# Clock

This example demonstrates the usage of the sensing time methods.

![clock](/assets/clock.gif)

## Run it here

The hands show the time of your computer. Hold space and the second hand sweeps
smoothly instead of ticking.

<!-- demo: clock -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="sprites/clock.png" src="/examples/clock/sprites/clock.png"
@file dest="sprites/hour.png" src="/examples/clock/sprites/hour.png"
@file dest="sprites/minute.png" src="/examples/clock/sprites/minute.png"
@file dest="sprites/second.png" src="/examples/clock/sprites/second.png"

```java ClockStage.java

void main() {
  new ClockStage();
}

class ClockStage extends Stage {
  public ClockStage() {
    super(800, 800);
    this.add(new ClockSprite());
    this.add(new SecondHandSprite());
    this.add(new MinuteHandSprite());
    this.add(new HourHandSprite());
  }
}

class ClockSprite extends Sprite {
  public ClockSprite() {
    this.addCostume("clock", "sprites/clock.png");
  }
}

class SecondHandSprite extends Sprite {

  public SecondHandSprite() {
    this.addCostume("hand", "sprites/second.png");
  }

  public void run() {
    int second = Clock.getSecond();
    if (this.isKeyPressed(KeyCode.SPACE)) {
      int millisecond = Clock.getMillisecond();
      this.setDirection(90 + (second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setDirection(90 + second / 60.0 * 360);
    }
  }
}

class MinuteHandSprite extends Sprite {
  public MinuteHandSprite() {
    this.addCostume("hand", "sprites/minute.png");
  }

  public void run() {
    int minute = Clock.getMinute();
    this.setDirection(90 + minute / 60.0 * 360);
  }
}

class HourHandSprite extends Sprite {
  public HourHandSprite() {
    this.addCostume("hand", "sprites/hour.png");
  }

  public void run() {
    int hour = Clock.getHour();
    this.setDirection(90 + hour / 12.0 * 360);
  }
}
```

:::

To run it on your own computer, copy the folder `sprites` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/clock) next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/clock
