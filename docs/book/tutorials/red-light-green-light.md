---
name: Red Light, Green Light
index: 4
---

# Red Light, Green Light

A bee, a ladybug and a snail race across the stage — but only while the referee
says they may. This is the playground game, and it is also the first project
where sprites **send each other messages**.

![a bee, ladybug and snail racing while a sign says Stop and Go](/assets/red-light-green-light.gif)

Nothing here needs a downloaded file.

## The problem messages solve

The referee has to start and stop three racers. You could give the referee a list
of every racer and call a method on each one. That works until you add a fourth
racer, and then you have to remember to add it to the list too.

Messages turn it around. The referee **shouts** and does not care who is
listening:

:::scratchblock
broadcast [go v]
:::

Any sprite that wants to can listen:

:::scratchblock
when I receive [go v]
:::

Add a fourth racer and it starts listening on its own. The referee never changes.

## Step 1: The referee

The referee flips between stop and go every couple of seconds and announces it.
`broadcast` sends the message to every sprite on the stage.

```java
import org.openpatch.scratch.*;

public class Referee extends Sprite {
  private boolean green = false;

  public Referee() {
    this.addCostume("sign");
    this.setSize(45);
    this.setPosition(-250, 110);
  }

  public void run() {
    if (this.getTimer().everyMillis(2200)) {
      this.green = !this.green;
      if (this.green) {
        this.say("Go!");
        this.broadcast("go");
      } else {
        this.say("Stop!");
        this.broadcast("stop");
      }
    }
  }
}
```

`this.green = !this.green` flips a true into a false and back again. It is a
tidy way of saying "switch to the other one".

## Step 2: A racer that listens

`whenIReceive` is called on every sprite whenever anybody broadcasts. The message
arrives as text, so the racer checks which one it was.

```java
import org.openpatch.scratch.*;

public class Racer extends Sprite {
  private String creature;
  private double speed;
  private boolean running = false;

  public Racer(String creature, double y, double speed) {
    this.creature = creature;
    this.speed = speed;
    this.addCostume(creature);
    this.addCostume(creature + "_move");
    this.setSize(45);
    this.setPosition(-260, y);
  }

  public void whenIReceive(String message) {
    if (message.equals("go")) {
      this.running = true;
    }
    if (message.equals("stop")) {
      this.running = false;
      this.switchCostume(this.creature);
    }
  }

  public void run() {
    if (!this.running) {
      return;
    }
    this.changeX(this.speed);
    if (this.getTimer().everyMillis(150)) {
      this.nextCostume();
    }
    if (this.getX() > 170) {
      this.say("I win!");
      this.running = false;
    }
  }
}
```

:::alert{info}

Text is compared with `equals`, not with `==`. `message == "go"` asks whether
they are *the same piece of text in memory*, which is not what you mean.
`message.equals("go")` asks whether they *say the same thing*, which is.

:::

The racer never mentions the referee, and the referee never mentions the racer.
Neither knows the other exists.

## Step 3: One class, three racers

Look at the constructor: `Racer(String creature, double y, double speed)`. Until
now your sprites have been made with `new Something()` and nothing else. Giving
the constructor **parameters** means one class can produce three different
racers.

```java
import org.openpatch.scratch.*;

public class RaceStage extends Stage {
  public RaceStage() {
    super(600, 340);
    this.addBackdrop("background");

    this.add(new Referee());
    this.add(new Racer("bee", 60, 2.2));
    this.add(new Racer("ladybug", 0, 1.6));
    this.add(new Racer("snail", -60, 1.0));
  }
}
```

The costume name is a parameter too, and because every creature in the built-in
library has a matching `_move` costume, `creature + "_move"` gives the racer its
second picture without you naming it twice.

In Scratch this would have been three separate sprites, each with its own copy of
the same scripts, and a change to one meaning three edits.

## The finished project

If something will not run, you can compare against the finished version.
It needs no assets, only the library itself.

::archive[Project: Red Light, Green Light 100%]{name="red-light-green-light-100"}

## Things to try

- Add a fourth racer — `frog` and `fishGreen` both have `_move` costumes. Notice
  that the referee needs no change at all.
- Broadcast a `"reset"` message that sends everyone back to the start.
- Make the referee wait a random time instead of always 2.2 seconds, so the stop
  cannot be predicted.
