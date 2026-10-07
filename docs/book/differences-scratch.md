---
name: Differences to Scratch
index: 3
---

# Differences to Scratch

Scratch for Java keeps stages, sprites, costumes and events. Java gives each
object fields and methods; the library calls `run()` once per game frame. At
normal speed that is about 60 frames per second. Pause, single-step and game
speed control game time, including timers, animation and timed speech.

- Use [Timers](/reference/Sprite/getTimer) to delay a sprite's next action across frames. Keep each `run()` call short.

If you want to achieve something like this inside a Sprite:

:::scratchblock
when green flag clicked
forever
next costume
wait (1) seconds
:::

You can use a timer.

```java
public class Cat extends Sprite {
    // Called once per game frame while the sprite is on a running stage.
    public void run() {
        if (this.getTimer().everyMillis(1000)) {
            this.nextCostume();
        }
    }
}
```


- **922 pictures and 266 sounds** ship with the library. Studio's asset browser
  provides previews; the [Sprites](/sprites) and [Sounds](/sounds) pages show the
  same assets. Call `addCostume("bunny1_stand")` to use one.
- Studio includes costume and sound tools. Files created in other tools can be
  imported as ordinary assets. Browser workspaces can carry image, audio, font
  and shader files; project ZIPs preserve their bytes.
- Save and share a project ZIP or its ordinary Java folder. The browser also
  supports workspace JSON. Review [compatibility](/compatibility) before
  switching environments: file-system, recording and Tiled APIs require desktop.
- A tight `while (true)` inside `run()` blocks later frames. Put one frame's work
  in `run()` and use timers or state fields for sequencing. Finite loops that
  finish quickly are useful in constructors and methods.

If you want to achieve something like this inside a Sprite:

:::scratchblock
when green flag clicked
forever
move (10) steps
:::

You can use the run-method of the Sprite-class.

```java
public class Cat extends Sprite {
    // Called once per game frame while the sprite is on a running stage.
    public void run() {
        this.move(10);
    }
}
```

- Shared state can live in a stage object passed to its sprites, or in a static
  field. Instance fields belong to one object. Clones copy field values; a copied
  reference still points to the same array or object, so plan shared state.

```java
public class Cat extends Sprite {
    public static int hitCounter = 1;
}

public class MyProgram {
    public MyProgram() {
        Cat.hitCounter += 1;
    }
}
```

- Clones work like in Scratch: `clone()` creates a clone of the same class,
  `whenStartsAsClone()` is "when I start as a clone", and `deleteThisClone()`
  removes it again. The clone gets a copy of your variables; the constructor
  does not run again.

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

- Variable monitors are not shown automatically: a variable in Java has no
  checkbox. Show one with `showVariable`, and it appears in the top left corner
  of the stage like in Scratch.

```java
public class Player extends Sprite {
    int lives = 3;

    public Player() {
        this.addCostume("player", "bunny1_stand");
        this.showVariable("lives", () -> lives);
    }
}
```

## Importing a Scratch project

Studio keeps the original `.sb3` and records migration tasks beside generated
Java. Open a task to locate its original block and the Java line that needs
attention. Timed and concurrent scripts need decisions about frame updates,
timers and shared state. See [Migration lessons](/migration) and test each
sequence before sharing the result.
