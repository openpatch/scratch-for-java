---
name: Multiple Approach Design
index: 11
---

# Multiple Approach Design

Scratch for Java does not decide how you teach objects. You can start with one
class and bring in classes of your own later, or start with classes from the
first lesson. This page shows the same small project written three ways, so you
can see what each approach asks of your students.

The project: a bunny you steer with the arrow keys, and a bee that flies back
and forth.

Every version starts the same way. The program opens a window and puts a stage
into it:

```java
Window myWindow = new Window(480, 360);
myWindow.setStage(new Garden());
```

## Classes later: one class

The whole project is one class, the stage. The sprites are plain `Sprite`
objects, and the stage's `run()` steers all of them.

```java
import org.openpatch.scratch.*;

public class Garden extends Stage {
  private Sprite bunny = new Sprite();
  private Sprite bee = new Sprite();

  public Garden() {
    this.addBackdrop("background");
    bunny.addCostume("bunny1_stand");
    bunny.setSize(50);
    bunny.setPosition(-150, -100);
    this.add(bunny);
    bee.addCostume("bee");
    bee.setSize(50);
    bee.setRotationStyle(RotationStyle.LEFT_RIGHT);
    bee.setPosition(0, 100);
    this.add(bee);
  }

  // about 60 times a second, like a forever loop
  public void run() {
    if (this.isKeyPressed(KeyCode.LEFT)) {
      bunny.changeX(-4);
    }
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      bunny.changeX(4);
    }
    bee.move(3);
    bee.ifOnEdgeBounce();
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new Garden());
  }
}
```

**What students meet:** one `extends Stage`, fields, the constructor, `run()`,
and calls on objects (`bunny.changeX(-4)`). There are no classes of their own
to design yet.

**The trade-off:** in Scratch every sprite has its own scripts. Here all
behaviour sits in one place, so the program grows into one long `run()` as
sprites are added. That is the natural moment to move to the next approach.

## Classes first: one class per sprite

Each sprite is a class of its own, like a sprite in Scratch with its own
scripts. The stage only puts the sprites on the stage.

```java
import org.openpatch.scratch.*;

class Bunny extends Sprite {
  public Bunny() {
    this.addCostume("bunny1_stand");
    this.setSize(50);
    this.setPosition(-150, -100);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.changeX(-4);
    }
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.changeX(4);
    }
  }
}

class Bee extends Sprite {
  public Bee() {
    this.addCostume("bee");
    this.setSize(50);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setPosition(0, 100);
  }

  public void run() {
    this.move(3);
    this.ifOnEdgeBounce();
  }
}

public class Garden extends Stage {
  public Garden() {
    this.addBackdrop("background");
    this.add(new Bunny());
    this.add(new Bee());
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new Garden());
  }
}
```

**What students meet:** inheritance from the first lesson (`extends Sprite`),
`this`, and one `run()` per class.

**Why it works anyway:** you can put Scratch and Java side by side.

| In Scratch | In Java |
| --- | --- |
| You click the cat icon to add a sprite and pick its costume in a tab. | The constructor sets the sprite up: `this.addCostume("bunny1_stand")`. Code replaces the clicking. |
| A sprite exists once you create it. | A sprite is on the stage once it is added: `this.add(new Bunny())`. |
| Every sprite has its own scripts. | Every sprite class has its own `run()`. |
| The blocks are there from the start. | `extends Sprite` brings the methods along (`move`, `say`, `ifOnEdgeBounce`). |
| A sprite's name is a label. | A name is how the code reaches an object: `Bunny bunny = new Bunny();` |
| A custom block, or a broadcast to another sprite. | A method, called on an object: `bunny.jump()`. |
| `wait 1 seconds` pauses one script. | `run()` must not wait. A sprite counts time with a timer instead: `this.getTimer().everyMillis(1000)`. |

## Going further: the window and animations

Both approaches lead to the same next steps, once one screen and still pictures
are not enough.

**More than one screen.** The window is in every program already. A class of its
own for it can decide which screen comes first, and any stage can switch to
another one:

```java
import org.openpatch.scratch.*;

class TitleStage extends Stage {
  public TitleStage() {
    this.addBackdrop("background");
    this.display("Press SPACE to start");
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Window.getInstance().transitionToStage(new Garden(), 500);
    }
  }
}

class Garden extends Stage {
  public Garden() {
    this.addBackdrop("background");
  }
}

public class GardenWindow extends Window {
  public GardenWindow() {
    super(480, 360);
    this.setStage(new TitleStage());
  }

  public static void main(String[] args) {
    new GardenWindow();
  }
}
```

**Animations.** An `AnimatedSprite` plays its costumes one after another, at its
own speed. It does the timing that a `wait` block did in Scratch:

```java
import org.openpatch.scratch.*;

class Bunny extends AnimatedSprite {
  public Bunny() {
    this.addAnimation("walk", "bunny1_walk%d", 2);
    this.setSize(50);
    this.setPosition(-150, -100);
  }

  public void run() {
    this.playAnimation("walk");
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.changeX(4);
    }
  }
}
```

The [Dodge the Rocks](/tutorials/dodge-the-rocks) tutorial takes these steps
with a whole game: a title screen, a game screen and a window class that holds
them together.
