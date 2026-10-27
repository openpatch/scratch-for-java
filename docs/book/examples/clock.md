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

public class ClockStage extends Stage {

  // begin stage designer
  private ClockSprite clockSprite;
  private SecondHandSprite secondHandSprite;
  private MinuteHandSprite minuteHandSprite;
  private HourHandSprite hourHandSprite;
  // end stage designer

  public ClockStage() {
    // begin stage designer
    clockSprite = new ClockSprite();
    this.add(clockSprite);
    secondHandSprite = new SecondHandSprite();
    this.add(secondHandSprite);
    minuteHandSprite = new MinuteHandSprite();
    this.add(minuteHandSprite);
    hourHandSprite = new HourHandSprite();
    this.add(hourHandSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 800);
    myWindow.setStage(new ClockStage());
  }
}
```

```java ClockSprite.java

public class ClockSprite extends Sprite {
  public ClockSprite() {
    // begin sprite editor
    this.addCostume("clock", "sprites/clock.png");
    // end sprite editor
  }
}
```

```java SecondHandSprite.java

public class SecondHandSprite extends Sprite {

  public SecondHandSprite() {
    // begin sprite editor
    this.addCostume("hand", "sprites/second.png");
    // end sprite editor
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
```

```java MinuteHandSprite.java

public class MinuteHandSprite extends Sprite {
  public MinuteHandSprite() {
    // begin sprite editor
    this.addCostume("hand", "sprites/minute.png");
    // end sprite editor
  }

  public void run() {
    int minute = Clock.getMinute();
    this.setDirection(90 + minute / 60.0 * 360);
  }
}
```

```java HourHandSprite.java

public class HourHandSprite extends Sprite {
  public HourHandSprite() {
    // begin sprite editor
    this.addCostume("hand", "sprites/hour.png");
    // end sprite editor
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
