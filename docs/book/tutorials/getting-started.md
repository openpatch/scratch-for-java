---
name: Getting Started
index: 1
---

# Your first program

If you have used [Scratch](https://scratch.mit.edu) before, you already know
most of what is on this page. A sprite, a stage, costumes, `move`, `say`,
`when key pressed` — Scratch for Java keeps all of those names. What changes is
that you write them instead of dragging them.

This page gets a character moving on screen. It takes about ten minutes, and you
do not have to download a single picture.

## What you need

1. A development environment. [BlueJ](https://bluej.org) is the friendliest one
   to start with, and this page assumes it.
2. The Scratch for Java library, added to BlueJ once.

Both are on the [Setup](/setup) page. Come back here when BlueJ is running.

## Step 1: A sprite on the stage

In BlueJ, create a new class called `MyStage` and replace everything in it with
these lines:

```java
import org.openpatch.scratch.*;

public class MyStage extends Stage {
  public MyStage() {
    Sprite bunny = new Sprite();
    bunny.addCostume("bunny1_stand");
    this.add(bunny);
  }
}
```

Compile, then right-click the `MyStage` box and choose `new MyStage()`. A window
opens with a rabbit in it:

![a rabbit standing in the middle of the stage](/assets/getting-started-1.gif)

That is a whole program. Seven lines, and no image files anywhere.

:::alert{info}

`"bunny1_stand"` is one of 838 pictures that come with Scratch for Java. You
never have to find one on the internet or put it in a folder. Browse them all on
the [Sprites](/sprites) page — click any one to copy the line that uses it.

:::

Line by line:

- **Line 1** brings in Scratch for Java. One import is enough for everything on
  this page.
- **Line 3** says `MyStage` *is a* `Stage`. In Scratch every project has one
  stage; here you make your own, and it can already do everything a stage does.
- **Line 5** creates a sprite, the same idea as clicking "add sprite".
- **Line 6** gives it a costume, by name.
- **Line 7** puts it on the stage. In Scratch a new sprite appears by itself; in
  Java you say so.

## Step 2: Make it move

In Scratch each sprite has its own scripts. In Java each sprite gets its own
class.

Create a second class called `Bunny`:

```java
import org.openpatch.scratch.*;

public class Bunny extends Sprite {
  public Bunny() {
    this.addCostume("bunny1_stand");
    this.setSize(50);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(4);
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(4);
    }
    this.ifOnEdgeBounce();
  }
}
```

The important part is `run()`. It is called about 60 times a second for as long
as the sprite is on a stage, so it is the Java version of this:

:::scratchblock
when green flag clicked
forever
if <key (right arrow v) pressed?> then
point in direction (90)
move (4) steps
:::

There is no `forever` in Java, because `run()` is already the inside of the
forever loop.

Now change `MyStage` to use the new sprite, and give the stage a backdrop:

```java
import org.openpatch.scratch.*;

public class MyStage extends Stage {
  public MyStage() {
    this.addBackdrop("background");
    this.add(new Bunny());
  }

  public static void main(String[] args) {
    new MyStage();
  }
}
```

Run it again and hold the left and right arrow keys:

![a rabbit walking left and right in front of a sky backdrop](/assets/getting-started-2.gif)

## What just happened

| In Scratch you… | In Java you… |
|---|---|
| clicked "add sprite" | write `new Bunny()` |
| dragged a costume in | write `addCostume("bunny1_stand")` |
| dropped blocks under `when green flag clicked` | write them inside `run()` |
| used a `forever` loop | let `run()` repeat for you |
| gave a sprite a name you rarely used | need the name to talk to it at all |

## The finished project

If something will not run, you can compare against the finished version.
It needs no assets, only the library itself.

::archive[Project: Your first program 100%]{name="getting-started-100"}

## Where to go next

- **[Bouncing Hedgehog](/tutorials/bouncy-hedgehog)** — your first real game,
  step by step.
- **[Sprites](/sprites)** and **[Sounds](/sounds)** — everything built in, with
  a search box.
- **[Differences to Scratch](/differences-scratch)** — the few places where Java
  makes you think differently.

:::alert{warn}

Stuck? The two most common first mistakes are a typo in a costume name — Scratch
for Java will tell you and suggest the closest match — and forgetting
`this.add(...)`, which leaves the sprite created but not on the stage, so
nothing appears.

:::
