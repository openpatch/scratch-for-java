---
name: Welcome
hide: true
---

# Scratch for Java (Version {{VERSION}})

Scratch for Java lets you keep everything you learned in
[Scratch](https://scratch.mit.edu) — sprites, costumes, the stage, `move`,
`say`, `when key pressed` — and write it as Java instead of dragging it.

It is built for the moment a class outgrows Scratch but is not ready to start
Java from an empty file.

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

That is a complete program, and it needs no image files: **922 pictures and 266
sounds are built in**.

Here is one running. Press **▶** — then change a number or a costume name and
start it again, it is a real editor. Every tutorial and every method in the
[documentation](/reference) comes with a box like this one.

:::onlineide{height="420px" libraries="scratch"}

```java MyStage.java

void main() {
  new MyStage();
}

class MyStage extends Stage {
  public MyStage() {
    super(500, 260);
    this.addBackdrop("background");
    this.add(new Walker());
  }
}

class Walker extends AnimatedSprite {
  public Walker() {
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(150);
    this.setSize(60);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setY(-50);
  }

  public void run() {
    this.playAnimation("walk");
    this.move(3);
    this.ifOnEdgeBounce();
  }
}
```

:::

## I'm learning

1. **[Your first program](/tutorials/getting-started)**: ten minutes, right here
   in the browser, nothing to install.
2. **[Seven tutorials](/tutorials)**: from a walking alien to a game with a
   title screen, one new idea at a time.
3. **[Scratch → Java cheat sheet](/scratch-to-java)**: which Java line
   replaces which block.
4. **[It doesn't work!](/troubleshooting)**: the most common mistakes and
   how to fix them.
5. **[Glossary](/glossary)**: class, object, method and the other new words.

Then pick a picture from the [built-in sprites](/sprites) and [sounds](/sounds),
look at the [examples](/examples), or look up any method in the
[documentation](/reference). Every method there shows the Scratch block it replaces
and an example you can run.

## I'm teaching

- **[For teachers](/classroom)**: planning a unit, what each tutorial covers,
  assessment and working offline.
- **[Setup](/setup)**: browser, Studio, BlueJ or VS Code.
- **[Multiple Approach Design](/multiple-approach-design)**: classes first or
  classes later.
- **[Abiturklassen NRW](/abitur-nrw)**: the version for the NRW Zentralabitur.

:::alert{info}
**Deutschsprachiges Material** findest du in
[Hyperbook Informatik](https://informatik.openpatch.org): den Lernpfad
[Grundlagen der Programmierung mit Java](https://informatik.openpatch.org/oberstufe/oop/01-grundlagen)
und die [Spielwerkstatt](https://informatik.openpatch.org/projekte/spielwerkstatt),
in der du über ein ganzes Schuljahr dein eigenes Spiel entwickelst.
:::

## Seeing it side by side

The following video shows a Scratch project and a similar BlueJ project using
the Scratch for Java library.

::youtube[Comparison of Scratch and Scratch for Java]{#3wKw2WWQcXk}

If you want to compare it yourself, you can take a look inside both projects:

- Scratch: https://scratch.mit.edu/projects/338613208
- BlueJ: [Source Code on GitHub](https://github.com/openpatch/scratch-for-java/tree/main/docs/archives/Halloween) or [Project Halloween.zip](/archives/Halloween.zip)

## Special Thanks

The Scratch for Java library is profiled using [Java Profiler](https://www.ej-technologies.com/products/jprofiler/overview.html)

[![Java Profiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png)](https://www.ej-technologies.com/products/jprofiler/overview.html)
