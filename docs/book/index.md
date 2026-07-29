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

That is a complete program, and it needs no image files: **838 pictures and 266
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

- **[Your first program](/tutorials/getting-started)** — ten minutes, nothing to
  download
- **[Setup](/setup)** — BlueJ, VS Code or plain Java
- **[Sprites](/sprites)** and **[Sounds](/sounds)** — the built-in library
- **[Documentation](/reference)** — every method with the Scratch block it
  replaces

## Seeing it side by side

The following video shows a Scratch project and a similar BlueJ project using
the Scratch for Java library.

::youtube[Comparision Scratch and Scratch for Java]{#3wKw2WWQcXk}

If you want to compare it yourself, you can take a look inside both projects:

- Scratch: https://scratch.mit.edu/projects/338613208
- BlueJ: [Source Code on GitHub](https://github.com/openpatch/scratch-for-java/blob/main/examples/archives/Halloween/) or [Project Halloween.zip](/archives/Halloween.zip)

## Special Thanks

The Scratch for Java library is profiled using [Java Profiler](https://www.ej-technologies.com/products/jprofiler/overview.html)

[![Java Profiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png)](https://www.ej-technologies.com/products/jprofiler/overview.html)
