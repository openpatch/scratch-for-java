---
name: Welcome
hide: true
---

# Scratch for Java

To ease the transition from the block-based programming environment
[Scratch](scratch.mit.edu) to Java this
library was created. Therefore, the core elements of Scratch are remodeled.

![](/assets/intro.gif)

:::::tabs{id="029602"}

:::tab{title="Java" id="689648"}

```java
import org.openpatch.scratch.*;

public class MyStage extends Stage {
    public MyStage() {
        Sprite cat = new Cat();
        this.add(cat);
    }
}

class Cat extends Sprite {
    public Cat() {
        this.addCostume("stand", "cat.png");
        this.setOnEdgeBounce(true);
    }

    public void run() {
        move(10);
    }
}
```

:::

::::tab{title="Scratch" id="689649"}

:::scratchblock
when green flag clicked
forever
    move (10) steps
    if on edge, bounce
:::


::::

:::::
