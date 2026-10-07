---
name: Differences to Scratch
index: 4
---

# Differences to Scratch

Most of Scratch carries over to Scratch for Java unchanged: the stage, sprites,
costumes, events and messages. This page is about the few places where Java asks
you to think differently. If you remember only one thing, remember this:

:::alert{info}
**`run()` is called again and again, about 60 times a second.** Every call should
do one frame's worth of work and then finish. There is no `forever`, and a sprite
never waits.
:::

## No `forever`: use `run()`

:::scratchblock
when green flag clicked
forever
move (10) steps
:::

Everything you would put in a `forever` loop goes into `run()`. The library calls
it for you, once per frame, for as long as the program runs:

```java
public class Cat extends Sprite {
  public Cat() {
    this.addCostume("cat", "cat_idle_1");
  }

  public void run() {
    this.move(10);
  }
}
```

Do not write a `while (true)` loop inside `run()`. It never finishes, so the
next frame is never drawn and the program freezes.

## No `wait` in a sprite: use a timer

:::scratchblock
when green flag clicked
forever
next costume
wait (1) seconds
:::

A sprite cannot stop and wait, because `run()` has to finish every frame. Instead
you ask a timer whether enough time has passed:

```java
public class Blinker extends Sprite {
  public Blinker() {
    this.addCostume("closed", "alienGreen_stand");
    this.addCostume("open", "alienGreen_front");
  }

  public void run() {
    if (this.getTimer().everyMillis(1000)) {
      this.nextCostume();
    }
  }
}
```

`everyMillis(1000)` is true once every second and false in all the frames in
between. Times in Scratch for Java are usually in **milliseconds**: 1000 is one
second. [Make it Walk](/tutorials/make-it-walk) builds on this.

## Things that do not hold up the script

In Scratch some blocks pause the script until they are done. In Java they start
something and return straight away, and `run()` carries on:

| Block | In Java | How to find out that it is done |
| --- | --- | --- |
| `ask [] and wait` | `ask("...")` | `isAsking()` becomes false, see [Guess the Number](/tutorials/guess-the-number) |
| `glide () secs to x: () y: ()` | `glide(1, 100, 0)` | `isGliding()` becomes false |
| `say [] for () seconds` | `say("Hi!", 2000)` | The speech bubble disappears by itself |

If something should happen one after another, keep a variable that remembers
which step you are at, and move on to the next step when the current one is done.

## Variables belong to an object

In Scratch you choose between *for this sprite only* and *for all sprites*. In
Java a variable is written at the top of a class, and every object of that class
gets its own.

- **For this sprite only** is a variable in the sprite's class. Each coin of the
  class `Coin` has its own `speed`.
- **For all sprites** is a variable in the stage's class. The sprites reach it
  through their stage, like the score in
  [Catch the Coins](/tutorials/catch-the-coins).

```java
public class Coin extends Sprite {
  private int speed = 3;

  public Coin() {
    this.addCostume("coin", "coinGold");
  }

  public void run() {
    this.changeY(-this.speed);
    if (this.isTouchingEdge()) {
      ((CoinStage) this.getStage()).addPoint();
      this.setY(180);
    }
  }
}

public class CoinStage extends Stage {
  private int points = 0;

  public CoinStage() {
    this.add(new Coin());
  }

  public void addPoint() {
    this.points = this.points + 1;
  }
}
```

Variables are not shown on the stage by themselves, because a Java variable has
no checkbox. `showVariable` puts one in the top left corner, like in Scratch:

```java
public class Player extends Sprite {
  private int lives = 3;

  public Player() {
    this.addCostume("player", "bunny1_stand");
    this.showVariable("lives", () -> this.lives);
  }
}
```

## Clones

Clones work like in Scratch. `clone()` is *create clone of myself*,
`whenStartsAsClone()` is *when I start as a clone*, and `deleteThisClone()`
removes it again. A clone starts with a copy of the original's variables. Its
constructor does not run again.

:::scratchblock
when green flag clicked
forever
create clone of [myself v]
wait (1) seconds

when I start as a clone
go to [random position v]
:::

```java
public class Star extends Sprite {
  public Star() {
    this.addCostume("star", "star1");
  }

  public void whenStartsAsClone() {
    this.goToRandomPosition();
  }

  public void run() {
    if (!this.isClone() && this.getTimer().everyMillis(1000)) {
      this.clone();
    }
  }
}
```

Often you do not need clones at all: in Java you can simply create several
objects of the same class with `new`, as the coins in
[Catch the Coins](/tutorials/catch-the-coins) do.

## Pictures and sounds

**922 pictures and 266 sounds are built in.** Browse them on the
[Sprites](/sprites) and [Sounds](/sounds) pages and use them by name, for example
`addCostume("bunny1_stand")`. You can also use your own files, see
[Costumes, Backdrops and Sounds](/costumes-backdrops-sound).

## Bringing a Scratch project along

Studio can import a `.sb3` file from Scratch. It turns costumes, sounds and many
scripts into Java and makes a list of the places that need your attention, such
as waits and scripts running at the same time.
[Continue a Scratch project](/migration) explains how to work through that list.
