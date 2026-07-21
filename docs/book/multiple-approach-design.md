---
name: Multiple Approach Design
---

# Multiple Approach Design

Scratch for Java is build for supporting different approaches to teaching object-oriented programming and to structure the transition from Scratch to Java.

## Imperative Approach (aka. Classes-later)

When using the imperative approach the whole project is one class. Sprites stay
plain `Sprite` objects, and everything they do is written in the stage's
`run()`. Students meet only the built-in classes, which they already know from
Scratch, and write no classes of their own beyond the stage itself.

Students can learn how to write simple Java programs, without the possible overload of having to deal with custom classes.

The downside to this approach is that you define the behavior of the Sprites inside one class. This differs from Scratch, where you define the behavior of each Sprite individually.

So by using this approach you are trading simplicity with compatibility to the Scratch model.

```java
import org.openpatch.scratch.*;

public class MyProgram extends Stage {
    private Sprite zebra = new Sprite();

    public MyProgram() {
        zebra.addCostume("bunny1_stand");
        this.add(zebra);
    }

    // runs about 60 times a second, for every sprite on this stage
    public void run() {
        zebra.move(1);
        zebra.ifOnEdgeBounce();
    }
}
```

The whole project is one class. There are no sprite classes to write, so
students never meet inheritance beyond the single `extends Stage`, and every
sprite is steered from one place.

## Object-oriented Approach (aka. Classes-first)

This approach is the most similar to the Scratch model, but as you can see it introduces the concept of inheritance right away. Even though one could think that this might be too much for students, you can put Scratch and Java side-by-side and show the transition.

Here are a few comparisons you could draw:
- In Scratch we prepared our project by clicking things. For example, we added a Sprite by clicking the cat icon, or we added a costume by clicking on the costume tabs and then uploaded/or drawn an image. In Java we prepare our project by using constructors. We write text (source code) which replaces our clicking, since we do not have an user interface anymore.
- In Scratch each Sprite and the Stage had a bunch of predefined blocks we could use. In Java we normally start with nothing predefined and have to code everything on our own. But with inheritance we can also use predefined methods (blocks).
- In Scratch we defined names for our Sprites, but did not use them so much. In Java naming is much more important, since we can not work with a Sprite or with the Stage if we did not give it a name. `Sprite cat = new Sprite();`
- In Scratch we added a Sprite to the Stage by creating one. In Java we need to be explizit and write that we want to a specific Sprite to the Stage. `myStage.add(mySprite);`
- In Scratch we often used the wait-block to pause the script of a Sprite for a little while. In Java we can only do that on a global level, meaning pausing the whole program `myStage.wait(100)`. If we want to do something similar in Scratch for Java we need to make use of time and the Timer-class.
- In Scratch we send messages to and from sprites by using a block. In Java we call methods on an object `mySprite.goLeft()`. A method is similar to a custom block.

```java
import org.openpatch.scratch.*;

class Zebra extends Sprite {
    public Zebra() {
        this.addCostume("bunny1_stand");
    }

    // runs 60-times a second
    // similar to a forever loop inside a when green flag pressed block
    public void run() {
        this.move(1);
        this.ifOnEdgeBounce();
    }
}

public class MyStage extends Stage {
    public MyStage() {
        this.add(new Zebra());
    }
}
```

## Enhanced Scratch Approach

The enhanced Scratch approach is based on the object-oriented approach, but introduces other concepts and classes, which differ from Scratch. This approach is build in such a way, that you can start with the object-oriented approach and move to the enhanced approach when you reach a limit.

For example in Scratch for Java you can create an object from the Window-class. This object can hold multiple stages and can switch between those. You can also define the size of the window - by default it uses the same size as a Scratch project.

```java
import org.openpatch.scratch.*;

public class MyProject extends Window {
    public MyProject() {
        super(800, 600);
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();

        this.setStage(stage1);
        // something triggers a stage change
        this.setStage(stage2);
    }
}
```

Another enhancement is the class AnimatedSprite, which makes it easy to use an animated for Sprites. This class should be used early since it overcomes the downside of not having a per-sprite wait-block by abstracting the timing.

```java
import org.openpatch.scratch.*;

public class Zebra extends AnimatedSprite {
    public Zebra() {
        this.addAnimation("walk", "walk_%d.png", 4);
    }

    public void run() {
        this.playAnimation("walk");
    }
}
```
