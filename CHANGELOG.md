## 2.2.0


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
