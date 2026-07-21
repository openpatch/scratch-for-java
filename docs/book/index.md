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
