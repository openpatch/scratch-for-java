---
name: Bouncing Hedgehog
index: 6
lang: en
---

# Bouncing Hedgehog

Spike the hedgehog loves to roll up into a spiky ball and bounce on his
trampoline, but he is a bit clumsy. Can you move the trampoline so that he never
falls to the ground?

In this chapter you build a whole game on your own, using everything from the
chapters before: sprites of your own classes, positions, keys, `run()`, `if` and
touching. At the end you swap in your own pictures.

A few steps are marked **Your turn**. Try them yourself before you open the
solution.

## Step 1: The stage

Create a class `BouncyHedgehogStage`:

```java
import org.openpatch.scratch.*;

public class BouncyHedgehogStage extends Stage {
  public BouncyHedgehogStage() {
    this.addBackdrop("background");
  }
}
```

Run it. You see the backdrop, and nothing else yet.

## Step 2: The trampoline

The trampoline is a sprite, so it gets a class of its own:

```java
import org.openpatch.scratch.*;

public class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    this.addCostume("spring");
    this.setSize(70);
  }
}
```

Line by line:

- `public class TrampolineSprite extends Sprite` says that a trampoline is a kind
  of sprite. It can do everything a sprite can do.
- `public TrampolineSprite() { ... }` is the :t[constructor]{#constructor}. It
  runs once, when a trampoline is made, and sets it up: a costume and a size.

A class is only a plan, so nothing has changed on the stage. Make a trampoline
with `new` and add it to the stage:

```java
import org.openpatch.scratch.*;

public class BouncyHedgehogStage extends Stage {
  public BouncyHedgehogStage() {
    this.addBackdrop("background");
    this.add(new TrampolineSprite());
  }
}
```

Run it again. The trampoline is in the middle of the stage.

## Step 3: Your turn: the hedgehog

Add Spike the same way: a class `HedgehogSprite` with the costume `spikeBall1`
and a size of `40`, and a line in the stage that adds him.

:::collapsible{title="Solution"}

```java
import org.openpatch.scratch.*;

public class HedgehogSprite extends Sprite {
  public HedgehogSprite() {
    this.addCostume("spikeBall1");
    this.setSize(40);
  }
}
```

And in `BouncyHedgehogStage`, below the trampoline:

```java
this.add(new HedgehogSprite());
```

:::

## Step 4: Starting positions

Both sprites are in the middle, on top of each other. The trampoline belongs at
the bottom. Add a line to its constructor:

```java
this.setPosition(0, -120);
```

x: 0 is the middle from left to right, and y: -120 is 120 steps down from the
middle.

**Your turn:** put Spike at the top left, at x: -180, y: 140.

:::collapsible{title="Solution"}

In the constructor of `HedgehogSprite`:

```java
this.setPosition(-180, 140);
```

:::

## Step 5: Move the trampoline

The arrow keys should move the trampoline. `whenKeyPressed` is called by the
library every time a key is pressed, and `key` says which one it was:

```java
import org.openpatch.scratch.*;

public class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    this.addCostume("spring");
    this.setSize(70);
    this.setPosition(0, -120);
  }

  public void whenKeyPressed(KeyCode key) {
    if (key == KeyCode.LEFT) {
      this.changeX(-10);
    } else if (key == KeyCode.RIGHT) {
      this.changeX(10);
    }
  }
}
```

This is Scratch's *when [left arrow] key pressed*, but one method handles every
key, and the `if` decides what to do. Run it, click the stage so it gets the
keys, and move the trampoline.

## Step 6: Make Spike bounce

Spike should keep moving, bounce off the edges, and fly back up when he lands on
the trampoline. If he gets past it, he hits the ground:

```java
import org.openpatch.scratch.*;

public class HedgehogSprite extends Sprite {
  public HedgehogSprite() {
    this.addCostume("spikeBall1");
    this.setSize(40);
    this.setPosition(-180, 140);
    this.setDirection(160);
  }

  public void run() {
    if (this.getY() > -100) {
      this.move(2);
      this.ifOnEdgeBounce();

      if (this.isTouchingSprite(TrampolineSprite.class)) {
        this.setDirection(Random.randomInt(-45, 45));
      }
    } else {
      this.say("Ouch!");
    }
  }
}
```

Before you run it, read the code and predict what will happen. Then run it and
check.

- `setDirection(160)` points Spike down and a little to the right. 0 is up,
  90 is right, 180 is down.
- `run()` is called in every frame. As long as Spike is above y: -100 he keeps
  moving. Below that he is lower than the top of the trampoline, so he has missed
  it: he stops and says "Ouch!".
- `isTouchingSprite(TrampolineSprite.class)` asks: am I touching any trampoline?
  If so, Spike gets a random direction between -45 and 45, which is always
  upwards.

## The finished game

Here it is running. Click the stage and use the arrow keys.

:::onlineide{height="560px" libraries="scratch"}

```java BouncyHedgehogStage.java

void main() {
  new BouncyHedgehogStage();
}

class BouncyHedgehogStage extends Stage {
  public BouncyHedgehogStage() {
    this.addBackdrop("background");
    this.add(new TrampolineSprite());
    this.add(new HedgehogSprite());
  }
}

class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    this.addCostume("spring");
    this.setSize(70);
    this.setPosition(0, -120);
  }

  public void whenKeyPressed(KeyCode key) {
    if (key == KeyCode.LEFT) {
      this.changeX(-10);
    } else if (key == KeyCode.RIGHT) {
      this.changeX(10);
    }
  }
}

class HedgehogSprite extends Sprite {
  public HedgehogSprite() {
    this.addCostume("spikeBall1");
    this.setSize(40);
    this.setPosition(-180, 140);
    this.setDirection(160);
  }

  public void run() {
    if (this.getY() > -100) {
      this.move(2);
      this.ifOnEdgeBounce();

      if (this.isTouchingSprite(TrampolineSprite.class)) {
        this.setDirection(Random.randomInt(-45, 45));
      }
    } else {
      this.say("Ouch!");
    }
  }
}
```

:::

## Step 7: Your own pictures

So far every picture was built in. You can use your own just as well: a drawing
of yours, a photo, or a picture from the internet that you are allowed to use.

This needs Studio, BlueJ or VS Code, because the browser editor cannot load files
from your computer. Download the project with a hedgehog, a trampoline and a
playground as picture files:

::archive[Project: Bouncy Hedgehog]{name="bouncy-hedgehog"}

The pictures are in the project folder, next to the Java files. Give each one a
name of your choice, followed by its file name:

```java
this.addCostume("hedgehog", "hedgehog.png");
```

```java
this.addCostume("trampoline", "trampoline.png");
```

```java
this.addBackdrop("playground", "playground.jpg");
```

The name is what you use later with `switchCostume`. File names must match
exactly, including capital letters and the ending. You may need a different
`setSize`, because these pictures are smaller than the built-in ones.
[Costumes, Backdrops and Sounds](/costumes-backdrops-sound) explains more, also
about sounds.

The finished game with these pictures:

::archive[Project: Bouncy Hedgehog 100%]{name="bouncy-hedgehog-100"}

## Things to try

### Make Spike faster

:::collapsible{title="Hint"}
How far Spike moves in each frame is the number in `move`.
:::

:::collapsible{title="Solution"}
In `run()`, change `this.move(2);` to `this.move(4);`. The game gets harder, so
the trampoline may need to be faster as well: change the `10` in
`whenKeyPressed`.
:::

### Space puts the trampoline back in the middle

:::collapsible{title="Hint"}
Add a third case to the `if` in `whenKeyPressed`. `KeyCode.SPACE` is the space
bar, and `setX` sets the position from left to right.
:::

:::collapsible{title="Solution"}

```java
public void whenKeyPressed(KeyCode key) {
  if (key == KeyCode.LEFT) {
    this.changeX(-10);
  } else if (key == KeyCode.RIGHT) {
    this.changeX(10);
  } else if (key == KeyCode.SPACE) {
    this.setX(0);
  }
}
```

:::

### Count the bounces

Show how often Spike has landed on the trampoline.

:::collapsible{title="Hint"}
You need a :t[field]{#field} `bounces` in `HedgehogSprite`, and `showVariable`
shows it on the stage. Careful: Spike touches the trampoline for several frames
in a row, so count only in the frame where the touching **starts**. A second
field can remember whether he was touching it in the frame before.
:::

:::collapsible{title="Solution"}

```java
import org.openpatch.scratch.*;

public class HedgehogSprite extends Sprite {
  private int bounces = 0;
  private boolean wasTouching = false;

  public HedgehogSprite() {
    this.addCostume("spikeBall1");
    this.setSize(40);
    this.setPosition(-180, 140);
    this.setDirection(160);
    this.showVariable("Bounces", () -> this.bounces);
  }

  public void run() {
    if (this.getY() > -100) {
      this.move(2);
      this.ifOnEdgeBounce();

      boolean touching = this.isTouchingSprite(TrampolineSprite.class);
      if (touching && !this.wasTouching) {
        this.bounces = this.bounces + 1;
        this.setDirection(Random.randomInt(-45, 45));
      }
      this.wasTouching = touching;
    } else {
      this.say("Ouch!");
    }
  }
}
```

:::

When you are confident, try [setTint](/reference/Sprite/setTint) to change
Spike's colour every time he bounces.
