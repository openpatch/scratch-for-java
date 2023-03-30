---
name: Changelog
index: 4
---

## 3.5.0

- 💻 OS: Added Linux ARM 32-bit and ARM 64-bit versions

## 3.4.0

- 🧹 Chore: Update Processing core to version 4.2
- 💻 OS: Added MacOS aarch64 version

## 3.3.0

- 🚀 Feat: Added `isSoundPlaying` and `stopSound` to the Sprite and Stage Class.

## 3.2.2

- 🎨 Visual: Improve Pen rendering
- 🐎 Perf: Pen does not draw everything again, but only the last additions.
- 🐛 Fix: isTouchingSprite threw an error, if a sprite did not have a costume.
- 🐛 Fix: exit method not found for Window class.

## 3.2.1

-  🐛 Fix: Pen only drawing dots

## 3.2.0

- 🚀 Feat: Introducing the new class `Window`. Every Scratch for Java program can have on `Window`-object. A `Window`-object contains one stage object, which can be change during the runtime. This allows for using multiple stages in your program. For example, you could have one stage for the menu, one for the game and one for the credits.

```java
public class Prog {
    public static void main(String[] args) {
        Window myWindow = new Window();
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        myWindow.addStage("one", stage1);
        myWindow.addStage("two", stage2);

        myWindow.switchStage("two");
    }
}
```

- 🚀 Feat: The class Text became more usable as a standalone Drawable. You can now create a Text-object with the default constructor `new Text()`. The created Text-object will in `TextStyle.PLAIN`, meaning having nothing drawn around it and be freely placeable on the stage.
    - The background color can now be set by passing a Color-object. `setBackgroundColor(Color c)`
    - The text color can now be set by passing a Color-object. `setTextColor(Color c)`

- 🐎 Perf: Scratch for Java now used an OpenGL renderer, which increases the performance by many times.
- 🐎 Perf: The collision detection got improved.
- 💥 BREAKING: `setSize` got removed from the Stage class. Since the switch to the OpenGL Renderer you can only set the size of the Stage or Window at the constructor.

```java
public class MyStage extends Stage {
    public MyStage() {
        super(800, 400)
        // this.setSize(800, 400);
    }
}
```

## 3.1.1

- 🐛 Fix: setSize did not affect getWidth and getHeight.

## 3.1.0

- 🚀 Feat: The debug modus now shows the current FPS.
- 🚀 Feat: `isTouchingSprite(Class)`. IsTouchingSprite accepts also a Class. So you can check the collision with all objects of this class.

## 3.0.0

Scratch for Java will from now on focus on being a standalone library. Therefore, it can not be used in Processing anymore.

## 2.1.0

### Changes

- 🚀 Feat: Image, Text and Pen can now be added without being used in a Sprite. Example:

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class PenStandalone {
    public static void main(String[] args) {
        Stage s = new Stage(400, 400);
        Pen p = new Pen();
        p.down();
        p.setPosition(40, 40);
        p.setPosition(40, 100);
    }
}
```

- 🚀 Feat: AnimatedSprite and Sprite now support SpriteSheets. Example:

```java
import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class SpriteSheet {
    public static void main(String[] args) {
       Stage stage = new Stage() ;
       stage.add(new AnimatedBee());
    }
}

class AnimatedBee extends AnimatedSprite {
    public AnimatedBee() {
        this.addAnimation("idle", "bee_idle.png", 6, 36, 34);
    }

    public void run() {
        this.playAnimation("idle");
    }
}
```

- 🐛 Fix: Pen did not include the first point

### BREAKING CHANGES

- 💥 Prefix Scratch is removed. For exmaple: ScratchSprite -> Sprite, ScratchStage -> Stage
- 💥 getInstance and init got removed from Stage. You now have to instantiate a Stage like a normal Object `new Stage(this) // Processing` or `new Stage(400, 400) // Standalone`. Be aware that you can only have one Stage at a time.

## 2.0.0

* BREAKING CHANGE: Rename package to `org.openpatch.scratch`

## 1.15.0

* Add standalone version

## 1.14.1

* Improve Text rendering

## 1.14.0

* add think and say to Sprite
* add display to Stage
* add whenClicked to Sprite
* add whenBackdropSwitches to Sprite

## 1.13.0

* add stage.removeSprites
* add stage.findSprites
* use CopyOnWriteArrayList instead of ArrayList

## 1.4.0

* add more timer methods
* add a timer example
