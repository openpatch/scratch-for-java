---
name: Make it Walk
index: 2
---

# Make it Walk

So far your sprites have been a single frozen picture sliding around. In this
chapter they start to move their legs.

![an alien walking left and right, its legs moving](/assets/make-it-walk-2.gif)

Animation sounds complicated and is not: it is **two pictures swapped over and
over**. Once you have seen that, the rest of the chapter is about letting
Scratch for Java do the swapping for you.

Nothing here needs a downloaded file.

## Step 1: Two costumes

A sprite can hold as many costumes as you like. Give it two, and it starts with
the first one.

```java
import org.openpatch.scratch.*;

public class Walker extends Sprite {
  public Walker() {
    this.addCostume("alienGreen_walk1");
    this.addCostume("alienGreen_walk2");
    this.setSize(60);
  }
}
```

Put it on a stage the usual way and you will see the alien standing still — it is
wearing costume one and never changes.

## Step 2: Swap them by hand

`nextCostume()` moves to the following costume and wraps back to the first at the
end. It is the Scratch block of the same name:

:::scratchblock
next costume
:::

If you called it in `run()` it would swap sixty times a second, which is far too
fast to see. So it needs a timer: `everyMillis(200)` is true once every fifth of
a second and false the rest of the time.

```java
  public void run() {
    if (this.getTimer().everyMillis(200)) {
      this.nextCostume();
    }
  }
```

![an alien moving its legs on the spot](/assets/make-it-walk-1.gif)

The alien is walking on the spot. **That is all animation is.** Try changing
`200` to `50` and to `600` and watch what happens.

:::alert{info}

In Scratch you would have written this with a `forever` loop and a `wait 0.2
seconds` block inside it. Java has no per-sprite wait, so a timer does the same
job: `run()` is called constantly and the timer decides when something happens.

:::

## Step 3: Let the library do it

Two costumes and one timer is fine. Six costumes, plus a jump, plus a standing
pose, and your `run()` fills up with counting.

`AnimatedSprite` is a `Sprite` that already knows how to do this. You give the
animation a name and tell it which costumes belong to it:

```java
this.addAnimation("walk", "alienGreen_walk%d", 2);
```

The `%d` is a placeholder for a number. This one line means "the animation called
walk is made of `alienGreen_walk1` and `alienGreen_walk2`" — the library fills
the number in for you, which is why the built-in costumes are named the way they
are.

## Step 4: Walk when you walk

Now put movement and animation together. The alien should move its legs only
while it is actually going somewhere, and stand still otherwise.

```java
import org.openpatch.scratch.*;

public class Walker extends AnimatedSprite {
  public Walker() {
    this.addCostume("alienGreen_stand");
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(150);
    this.setSize(60);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setY(-60);
  }

  public void run() {
    boolean walking = false;

    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(3);
      walking = true;
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(3);
      walking = true;
    }

    if (walking) {
      this.playAnimation("walk");
    } else {
      this.switchCostume("alienGreen_stand");
    }

    this.ifOnEdgeBounce();
  }
}
```

And a stage to put it on:

```java
import org.openpatch.scratch.*;

public class WalkStage extends Stage {
  public WalkStage() {
    super(500, 260);
    this.addBackdrop("background");
    this.add(new Walker());
  }

  public static void main(String[] args) {
    new WalkStage();
  }
}
```

Three things are new here:

- **`extends AnimatedSprite`** instead of `extends Sprite`. It can still do
  everything a sprite can — `move`, `setDirection`, `ifOnEdgeBounce` are all
  there — it simply knows about animations as well.
- **`boolean walking`** remembers whether either key was held this frame, so the
  decision about which costume to wear is made once, after both checks.
- **`setAnimationInterval(150)`** is the same idea as the `200` in step 2: how
  long each picture stays on screen.

## The finished project

If something will not run, you can compare against the finished version.
It needs no assets, only the library itself.

::archive[Project: Make it Walk 100%]{name="make-it-walk-100"}

## Things to try

- Give the alien a third state. `alienGreen_jump` is a costume; show it while the
  alien is off the ground.
- Swap the alien for a different character. The
  [built-in sprites](/sprites) page has walk cycles for `bunny1_walk%d`,
  `alienBlue_walk%d` and several others — the pattern is always the same.
- Make the alien walk faster *and* animate faster when you hold shift.
