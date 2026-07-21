---
name: Dodge the Rocks
index: 7
---

# Dodge the Rocks

Every project so far has been one screen that starts when you run it and never
ends. Real games have a title, a game, and a moment where it tells you how you
did. This chapter is about **several screens in one window**.

![a title screen, an alien dodging falling rocks, and a game over screen](/assets/dodge-the-rocks.gif)

Nothing here needs a downloaded file.

## Step 1: The window is not the stage

You have been writing `super(500, 260)` inside a stage since the first chapter,
and it has been doing two jobs at once. A stage is one screen of your project.
The **window** is the thing on your desktop that the screen is drawn in.

Up to now you never said which was which, because a stage that finds no window
quietly makes one for itself. That is what the size was for.

As soon as you want a second screen, the two have to be told apart — otherwise
the second stage would try to make a second window. So the window gets a class
of its own:

```java
import org.openpatch.scratch.*;

public class DodgeWindow extends Window {
  public DodgeWindow() {
    super(600, 400);
    this.setStage(new TitleStage("Dodge the Rocks"));
  }

  public static void main(String[] args) {
    new DodgeWindow();
  }
}
```

The size moved to the window, where it belonged all along, and `setStage` says
which screen to show first. From here on the stages take no size at all — they
are all drawn in the same window.

:::alert{info}

There is only ever **one** window. `new Window(...)` a second time is an error,
and the library will say so. Stages you can make as many as you like.

:::

## Step 2: A screen that waits

The title screen has no sprites on it. It is two pieces of text and one thing it
listens for:

```java
import org.openpatch.scratch.*;

public class TitleStage extends Stage {
  public TitleStage(String message) {
    this.addBackdrop("background");

    Text title = new Text();
    title.setPosition(0, 60);
    title.setTextSize(30);
    title.showText(message);
    this.add(title);

    Text hint = new Text();
    hint.setPosition(0, 10);
    hint.setTextSize(18);
    hint.showText("Press SPACE to play");
    this.add(hint);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Window.getInstance().setStage(new GameStage());
    }
  }
}
```

:::scratchblock
when [space v] key pressed
:::

Two things are worth stopping at.

`whenKeyPressed` on a **stage** is the same block you already know, except that
the stage hears it rather than a sprite. A title screen has no sprite to put it
on, which is exactly why stages can listen too.

`Window.getInstance()` is how you reach the window from anywhere in your project.
You made it in `main`, but you do not have to pass it around — there is only one,
so the library will hand it to you when you ask.

And the message is a **parameter**, which is the whole trick of this chapter.
The same class is the title screen and the game over screen.

## Step 3: The game

The alien is the walker from [Make it Walk](/tutorials/make-it-walk), a little
smaller and a little faster:

```java
import org.openpatch.scratch.*;

public class Alien extends AnimatedSprite {
  public Alien() {
    this.addCostume("alienGreen_stand");
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(150);
    this.setSize(45);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setY(-140);
  }

  public void run() {
    boolean walking = false;

    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(4);
      walking = true;
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(4);
      walking = true;
    }

    if (walking) {
      this.playAnimation("walk");
    } else {
      this.switchCostume("alienGreen_stand");
    }

    this.ifOnEdgeBounce();
  }
}
```

The rocks fall like the coins in [Catch the Coins](/tutorials/catch-the-coins),
but they are worth a point for *missing* you rather than for hitting you:

```java
import org.openpatch.scratch.*;

public class Rock extends Sprite {
  public Rock() {
    this.addCostume("rock");
    this.setSize(40);
    this.dropFromTop();
  }

  private void dropFromTop() {
    this.setX(Random.randomInt(-280, 280));
    this.setY(Random.randomInt(220, 420));
  }

  public void run() {
    this.changeY(-3);

    if (this.isTouchingSprite(Alien.class)) {
      ((GameStage) this.getStage()).gameOver();
    }

    if (this.getY() < -200) {
      ((GameStage) this.getStage()).addDodge();
      this.dropFromTop();
    }
  }
}
```

And the stage they live on keeps the score, exactly as the catching game did:

```java
import org.openpatch.scratch.*;

public class GameStage extends Stage {
  private Text scoreText = new Text();
  private int dodged = 0;

  public GameStage() {
    this.addBackdrop("background");

    this.scoreText.setPosition(0, 165);
    this.scoreText.setTextSize(20);
    this.add(this.scoreText);
    this.showScore();

    this.add(new Alien());
    for (int i = 0; i < 3; i++) {
      this.add(new Rock());
    }
  }

  public void addDodge() {
    this.dodged = this.dodged + 1;
    this.showScore();
  }

  public void gameOver() {
    Window.getInstance().setStage(new TitleStage("You dodged " + this.dodged));
  }

  private void showScore() {
    this.scoreText.showText("Dodged: " + this.dodged);
  }
}
```

Notice what `gameOver` does **not** do. It does not stop the rocks, empty the
score, or put the alien back in the middle. It builds a `TitleStage` and hands
it to the window, and the old game — alien, rocks, score and all — is simply
gone.

## Step 4: Starting again is free

Press SPACE on the game over screen and `TitleStage` runs
`Window.getInstance().setStage(new GameStage())` again. That `new` gives you a
brand new stage: a new score of zero, a new alien in its starting place, three
new rocks at new random heights.

This is why screens are worth the trouble. Resetting a game by hand means
remembering every single thing that changed while it was played, and the bug you
get when you forget one is miserable to find. Throwing the whole screen away and
building another cannot forget anything.

:::alert{info}

In Scratch, a project is one stage, and starting over means setting every
variable back by hand at the top of the green flag script. Being able to throw a
screen away and make a new one is one of the few places where the Java version
is genuinely simpler than the blocks.

:::

## The finished project

If something will not run, you can compare against the finished version.
It needs no assets, only the library itself.

::archive[Project: Dodge the Rocks 100%]{name="dodge-the-rocks-100"}

## Things to try

- The game over screen says "You dodged 1" when you only dodged one rock. Make
  it say "1 rock" and "2 rocks" using an `if`.
- Add a third screen between the title and the game that explains the controls,
  and make SPACE walk through title, help, game.
- Speed the rocks up as the score climbs. `dodged` is on the stage, and the rocks
  can already reach the stage — they call `addDodge` on it.
- Keep the best score so far. It cannot live on `GameStage`, because that object
  is thrown away every game. Where does it have to go instead?
