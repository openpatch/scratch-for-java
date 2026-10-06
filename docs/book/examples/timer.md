---
name: Timer
---

# Timer

An example which makes use of the many methods of the timer.

![timer](/assets/timer.gif)

## Run it here

One row per timer method, all of them running at the same time. The stage class is
called `TimerStage` rather than `Timer`, because the library has a `Timer` of its
own. The stage is 1800 pixels wide, so it is scaled down to fit.

<!-- demo: timer -->
:::onlineide{height="520px" libraries="scratch"}

```java TimerStage.java

void main() {
  new TimerStage();
}

class TimerStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private TimerSprite timerSprite;
  // scratch4j:end fields

  public TimerStage() {
    super(1800, 360);
    // scratch4j:begin setup (managed by the stage designer)
    timerSprite = new TimerSprite();
    this.add(timerSprite);
    // scratch4j:end setup
  }
}

class TimerSprite extends Sprite {
  int x = -900;

  public TimerSprite() {
    super();
    this.getPen().setSize(40);
  }

  public void run() {
    int y = -140;
    this.x += 20;
    if (this.getTimer("every").everyMillis(600)) {
      this.getPen().setColor(20); // Orange
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("for").forMillis(600)) {
      this.getPen().setColor(60); // Hellgrün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("after").afterMillis(600)) {
      this.getPen().setColor(100); // Grün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval1").intervalMillis(600)) {
      this.getPen().setColor(140); // Hellblau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval2").intervalMillis(600, true)) {
      this.getPen().setColor(180); // Blau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval3").intervalMillis(600, 300)) {
      this.getPen().setColor(220); // Pink
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval4").intervalMillis(600, 300, true)) {
      this.getPen().setColor(255); // Rot
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/timer

