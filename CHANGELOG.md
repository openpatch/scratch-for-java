---
name: Changelog
index: 4
lang: en
---

## 4.15.0

- 🚀 Feat: All methods return a color now return a Color object.
- 🚀 Feat: Add `get`-method to color object for comparing pixels.

## 4.14.0

- 🚀 Feat: Add `enableYSort`-method to the stage. This sorts all sprites regarding their y-coordinate.

```java
Stage stage = new Stage();
stage.enableYSort();
```


## 4.13.0

- Update dependencies
    - Processing 4.3.1
    - Jackson 2.18.2
    - Jogl 2.5.0
    - Gluegen 2.5.0
- Add template for VSCode

## 4.12.0

- 🚀 Feat: There is a new color extension providing a color class and all named HTML colors for easy reference.

```java
Color c = new Color("#FF0000"); // red
mySprite.setTint(c);

mySprite.setTint(HtmlColor.AZURE);
```

- 🚀 Feat: Add shaders to sprites and to stages. You can find the shader example in the examples directory. For a quick overview you can look at the following code snippet, which works for sprites and for stages.

```java
class ShaderSprite extends Sprite {
    public ShaderSprite() {
        var shader = this.setShader("blur", "blur.glsl");
        shader.set("radius", 10.0);
        this.switchShader("blur");
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_N) {
            this.nextShader();
        } else if (keyCode == KeyEvent.VK_R) {
            this.resetShader();
        }
    }
}
```

## 4.11.0

- 🚀 Feat: A broadcast and whenIReceive methods for the Object-class, which makes it possible to send anything as a broadcast.

```java
sprite1.broadcast(new Event());

class AnotherSprite extends Sprite {
    public void whenIReceive(Object message) {
        if (message instanceof Event) {
            // ...
        }
    }
}
```

## 4.10.0

- 🚀 Feat: Add new random methods for get a random position with respect to the window size.

```java
Random.randomPosition();
Random.randomX();
Random.randomY();
```

- 🐛 Fix: setSize not caching the new image, which lead to low framerates.


## 4.9.0

- 🚀 Feat: Add fullscreen mode. You can now define in the constructor of your window, if you want to operate in fullscreen mode.

```java
new Window(true); // fullscreen
new Window(true, assets) // fullscreen with assets loading
```

- 🐛 Fix: keyCodes for some keys were off.
- 🐛 Fix: File extension not working on Windows.

## 4.8.3

- 🐛 Fix: pointTowardsSprite pointing to the opposite direction 

## 4.8.2

- 🐛 Fix: Pen position and state when using the copy-constructor.

## 4.8.1

- 🐛 Fix: Debug text influencing text size of other texts.
- 🐛 Fix: Mouse tracking not working correctly when zooming in and out.

## 4.8.0

- 💥 BREAKING CHANGE: Pen behavior was changed to work more similar to the behavior of the scratch pen. 
    - You should double the size of your pen in your sketches to get the same visuals as in the previous versions. 
    - You should also put the pen down after you have set the initial position, since the pen now draws an initial point, when it is put down.

## 4.7.0

- 🚀 Feat: Add methods to the text class to be more consistent with the sprite class in regard to positioning.
- 🚀 Feat: Update sound library to add support for mp3 and ogg files
- 🐛 Fix: Think- and say-bubble were not at the right place

## 4.6.0

- 🚀 Feat: Add setCursor method to the stage class.
- 🚀 Feat: Add extension camera. The camera object allows you to move the view of the stage without manipulating the coordinates of e.g. sprites.

You can now get the camera from a stage object.

```java
var myStage = new Stage();
Camera myCamera = myStage.getCamera();
```

The three main abilities of the camera are:

1. Set the position of the camera (setX, setY, changeX, changeY, setPosition)
2. Set the zoom of the camera (setZoom, resetZoo, changeZoom, setZoomLimit)
3. Transform coordinates from local (Camera-Space) to global (Window-Space) (toLocalPosition, toLocalX, toLocalY, toGlobalPosition, toGlobalX, toGlobalY)

## 4.5.0

- 💥 BREAKING CHANGE: Only use double for methods instead of supporting both float and doubles. This was unnecessary and cluttered the BlueJ interface with "duplicated" methods. This could potentially break your project if you were using floats, just replace them with doubles, and you are good to go.

## 4.4.0

- 🚀 Feat: Revert BREAKING CHANGE and add addStage, removeStage and getStage again.
- 🚀 Feat: Add methods for counting sprites (countSprites, countSpritesOf), pens (countPens, countPensOf) and texts (countTexts, countTextsOf).
- 🚀 Feat: Add more methods for finding sprites (findSpritesOf), pens (findPensOf) and texts (findTextsOf) of a given class.
- 🚀 Feat: Add exit to Stage for easier access.
- 🚀 Feat: Add getDeltaTime to the Sprite and Stage class.
- 🚀 Feat: You can now stretch a backdrop to the window size by using `addBackdrop("name", "path", true)`.
- 🐛 Fix: Path resolution of assets
- 🐛 Fix: whenMouseMoved not working

## 4.3.0

- 🚀 Feat: Random can now create a new random unit vector (Random.randomVector2()).
- 🚀 Feat: You can now set the width of a text after calling the constructor (text.setWidth(40)).

## 4.2.1

- 🐛 Fix: AnimatedSprite throws an error, because the animationFrame is too high.

## 4.2.0

- 💥 BREAKING CHANGE: Replace addStage, removeStage and getStage in favour of setStage, which simplifies the handling of multiple stages.
- 🐛 Fix: First frame of an AnimatedSprite is skipped.

## 4.1.0

- 🚀 Feat: New extension tiled.
- 🐛 Fix: Fix isTouchingMousePointer.

## 4.0.2

- 🐛 Fix: Missing stamp method on Sprite

## 4.0.1

- 🐛 Fix: Scratch for Java Window not creating on Windows

## 4.0.0

- 💥 BREAKING CHANGE: Move (0, 0) to the center of the stage to be in-line with Scratch.
- 🚀 Feat: Add stamp method to the pen `pen.stamp()`. This is also available on a Sprite with `stamp()`, `stampToForeground()` or `stampToBackground()`.
- 🚀 Feat: Unify constructors of Window and Stage.
- 🚀 Feat: Load all tiles from a sprite sheet as costumes with addCostumes.
- 🚀 Feat: Add goToMousePointer to the sprite class.
- 🚀 Feat: Add goToRandomPosition to the sprite class.
- 🚀 Feat: switchCostumes now also accepts an integer for switching to a specific index.
- 🚀 Feat: Allow for custom sorting of sprites by providing a Comparator via the `setSorter`-method of the stage.
- 🐛 Fix: Random.randomInt did not work correct.

## 3.9.0

- 🚀 Feat: Add support for broadcast and when i receive blocks to the Sprite and the Stage class.

## 3.8.0

- 🚀 Feat: Support for vertical sprite sheets was added.
- 🚀 feat: Loading Animation got improved. The loading screen now shows the percentage of files, which are already loaded.

## 3.7.0

#scratch4j v3.7.0 got released:

- 💥 BREAKING CHANGE: AnimatedSprite, Hitbox, Text and Timer were moved into the org.openpatch.scratch.extensions package. Each extension got its package. For example, org.openpatch.scratch.extensions.animation. In the main package, we only want the classes with emulate the current Scratch behavior. This should lead to a smoother transition. The extensions package should be used, we the normal functionality is not enough.
- 🚀 Feat: New class Vector2 for the math extension. Every so often, it is easier to work with vectors. Therefore, this class got introduced. The Sprite class was updated to make use of the Vector2 class. You can, for example, call the move and setDirection methods with a Vector2.
- 🚀 Feat: New class Operators. This class has several simple methods for transforming data and working with mathematical operations – just like the Scratch operator blocks. For example, mapping values, lerping between values or using sine and cosine.
- 🚀 Feat: New class Random in the math extension. This class contains several methods for generating random numbers. This makes working with randomness outside a Sprite or Stage class easier.
- 🚀 Feat: Introduce the method ifOnEdgeBounce for the class Sprite. This method works like the if on edge, bounce-block in Scratch in is an alternative to the setOnEdgeBounce-method.
- 🚀 Feat: Introduce pointTowardsMousePointer and pointTowardsSprite methods for the class Sprite.
- 🐛 Fix: Jar files missing version information.
- 🐛 Fix: Speak and Think-bubble rendering. Speak and Think-bubbles now behave like they do in Scratch. Meaning, they will ever leave the Stage.
- 📝 Docs: Add more documentation

## 3.6.0

- 🚀 Feat: Add method `setTextSize` to the Text-class

```java
Text myText = new Text();
myText.setTextSize(48);
```

- 🚀 Feat: Allow custom fonts for Text-objects. Custom fonts can be added like a costume for a Sprite-object. Each Text-object does have a default font named `default`.

```java
Text myText = new Text();
myText.addFont("playful", "opensans.odt");
myText.switchFont("playful");

// get the text-object, which is used for the think and speak bubble
Text spriteText = new Sprite().getText();
```

- 📝 Docs: Add example `Shakespeare` which shows the usage of the new methods of the Text-class

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

- 🐛 Fix: Pen only drawing dots

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

- BREAKING CHANGE: Rename package to `org.openpatch.scratch`

## 1.15.0

- Add standalone version

## 1.14.1

- Improve Text rendering

## 1.14.0

- add think and say to Sprite
- add display to Stage
- add whenClicked to Sprite
- add whenBackdropSwitches to Sprite

## 1.13.0

- add stage.removeSprites
- add stage.findSprites
- use CopyOnWriteArrayList instead of ArrayList

## 1.4.0

- add more timer methods
- add a timer example
