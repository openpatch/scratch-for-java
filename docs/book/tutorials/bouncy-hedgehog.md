---
name: Bouncing Hedgehog
index: 2
lang: en
---

# Bouncing Hedgehog

Spike, the bouncing hedgehog, loves to jump on his trampoline, but he's a bit clumsy. Can you move the trampoline so that he doesn't fall to the ground anymore?

In this chapter, you will program your first game. In this game, you will be able to use the arrow keys to move a trampoline from left to right. Your goal is to catch a bouncing target with the trampoline. This project will show you how to add new sprites and backgrounds, and how to control behavior in your project using conditional statements. These skills will also help you with the following projects.

## Step 1: Download Project Template

You can download the images along with a basic framework for your BlueJ project here.

::archive[Project: Bouncy Hedgehog]{name="bouncy-hedgehog"}

## Step 2: Run the Project

Let's take a look at what the project looks like in its initial state. Right-click on the `BouncyHedgehogStage` class and create a new object by clicking on `new BouncyHedgehogStage()`.

A window should open where you can see the playground.

## Step 3: Add the Trampoline to the Stage

Open the TrampolineSprite class in BlueJ by double-clicking. The class is already prepared for you. We will now go through it line by line so you understand what each line means. Don't worry, you don't have to understand everything right away – understanding grows over time.

```java
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
    }
}
```

- Line 1: Here a class is imported. This means we are using source code from someone else to make our work easier. We use the Sprite class from Scratch4j so we don't have to worry about how, for example, images can be displayed on the screen.

- Line 3: Here we define our own class named `TrampolineSprite` and this class should extend the `Sprite` class. In other words, use the functions of the already implemented `Sprite` class.

- Lines 4-6: Here we define a constructor. This is executed as soon as we create a trampoline - more on that later. Here we define that we want to add a costume, which we call `trampoline` and should use the file `trampoline.png`.

Now let's add the trampoline to our stage. Double-click on the `BouncyHedgehogStage` class. Here you see similar lines of source code.

```java
import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {
    
    public BouncyHedgehogStage() {
        this.addBackdrop("playground", "playground.jpg");
    }
}
```

This time, however, the class inherits from Stage (line 3), since this should be a stage. Therefore, no costume is added in the constructor, but a background (see line 6).

So that we can now also see the trampoline, we add a line of source code to the constructor.

```java
import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {
    
    public BouncyHedgehogStage() {
        this.addBackdrop("playground", "playground.jpg");
        this.add(new TrampolineSprite());
    }
}
```

The new line first calls the constructor of the `TrampolineSprite` class and then adds the newly created object to the stage with the method call `this.add(new TrampolineSprite())`.

Run the project again now. The trampoline should now be visible in the center of the stage.

## Step 4: Add the Hedgehog

Can you manage to add the hedgehog? Follow the previous steps and this time use the `HedgehogSprite` class.

## Step 5: Move the Trampoline

So far, both sprites are placed in the center of the stage. Now we will first move the trampoline to a good starting position. Then we will program it so that we can control it with the arrow keys.

To position the trampoline, we add another line to the constructor of the `TrampolineSprite` class.

```java
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
        this.setPosition(0, -120);
    }
}
```

With this line, we position the trampoline in the x-direction (left-right) at 0, which means in the center, and in the y-direction (up-down) at -120, which means 120 pixels down from the center.

Look at the result by running the project. To do this, right-click on the `BouncyHedgehogStage` class and select `new BouncyHedgehogStage()`.

## Step 6: Position the Hedgehog

Try to position the hedgehog at position (x: -180, y: 140).

## Step 7: Move the Trampoline

We want to move the trampoline with the arrow keys. To do this, we extend the `TrampolineSprite` class as follows:

```java
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
        this.setPosition(0, -120);
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyCode.VK_LEFT) {
            this.changeX(-10);
        } else if (keyCode == KeyCode.VK_RIGHT) {
            this.changeX(10);
        }
    }
}
```

We have added a new method `whenKeyPressed` to the class. Methods allow us to implement certain behaviors for the objects of a class. In this case, `whenKeyPressed` is a special method - predefined by Scratch4j. It is always called when a key on the keyboard is pressed. The variable `keyCode` then contains the numeric code of that key.

For example, if you press the key `A`, then the variable `keyCode` contains the value `65`. So that we don't have to remember all the values of individual keys, there is the `KeyCode` class. For example, behind `KeyCode.VK_A` would be the value `65`. This also makes the source code more comprehensible. Since this is again a class from Scratch4j, we must also import it (see first line).

Now we can define what should happen when a key is pressed.

If the pressed key is the left arrow key, then we change the x-position by -10 pixels. So we move the trampoline to the left.
Otherwise, if the pressed key is the right arrow key, then we change the x-position by 10 pixels. So we move the trampoline to the right.

So, now it's time to try out the project again. You already know the steps for that.

## Step 8: Make the Hedgehog Bounce

The hedgehog should automatically fall down and move back up when it touches the trampoline. We achieve this with the following code:

```java
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.extensions.math.Random;

public class HedgehogSprite extends Sprite {

    public HedgehogSprite() {
        this.addCostume("hedgehog", "hedgehog.png");

        this.pointInDirection(15);
        this.setPosition(-180, 140);
    }

    public void run() {
        if (this.getY() > -120) {
            this.move(1);
            this.ifOnEdgeBounce();

            if (this.isTouchingSprite(TrampolineSprite.class)) {
                this.pointInDirection(Random.random(-45, 45));
            }
        } else {
            this.say("Ouch!", 2000);
        }
    }
}
```

Based on the method names (isTouchingSprite, pointInDirection), think about what happens when the project is executed. Run the project and check your assumptions.

## Conclusion

Congratulations, you have programmed your first game with Scratch4j! Even if everything isn't clear to you yet, stick with it – practice makes perfect.

Feel free to continue experimenting: Make the hedgehog faster, reset the trampoline to the center with the spacebar, or swap out the graphics. When you become more confident, try out the [setTint](/reference/sprite/looks/setTint) method.

Keep having fun :smiley:!

::archive[Project: Bouncy Hedgehog 100%]{name="bouncy-hedgehog-100"}
