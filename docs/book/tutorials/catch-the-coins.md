---
name: Catch the Coins
index: 3
---

# Catch the Coins

Coins fall from the sky and you catch them. This is the first project with a
**score**, which means the first project that has to remember something.

![an alien catching falling coins while the score counts up](/assets/catch-the-coins.gif)

You need nothing but the three classes below. Every picture and sound comes with
Scratch for Java.

## Step 1: The catcher

Make a class called `Basket`. It is the same arrow-key movement as in
[your first program](/tutorials/getting-started), just sitting near the bottom of
the stage.

```java
import org.openpatch.scratch.*;

public class Basket extends Sprite {
  public Basket() {
    this.addCostume("alienGreen_stand");
    this.setSize(35);
    this.setY(-150);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.move(5);
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.move(5);
    }
    this.ifOnEdgeBounce();
  }
}
```

## Step 2: A falling coin

Make a class called `Coin`. Two new ideas appear here.

The first is **a method of your own**. `dropFromTop()` is not a Scratch for Java
method, it is one you invent because you need the same three lines in two
different places. In Scratch this is a custom block.

The second is `Random.randomInt(a, b)`, which gives a whole number between `a`
and `b`. It is the `pick random` block.

```java
import org.openpatch.scratch.*;

public class Coin extends Sprite {
  public Coin() {
    this.addCostume("coinGold");
    this.setSize(40);
    this.dropFromTop();
  }

  private void dropFromTop() {
    this.setX(Random.randomInt(-270, 270));
    this.setY(Random.randomInt(200, 400));
  }

  public void run() {
    this.changeY(-3);

    if (this.isTouchingSprite(Basket.class)) {
      ((CatchStage) this.getStage()).addPoint();
      this.dropFromTop();
    }

    if (this.getY() < -200) {
      this.dropFromTop();
    }
  }
}
```

:::alert{info}

`isTouchingSprite(Basket.class)` asks "am I touching *any* basket?" rather than
naming one particular sprite. That is why the coin does not need to be told which
basket exists.

:::

The coins start at a random height *above* the stage, so they do not all fall in
a neat row.

## Step 3: The stage that counts

The score lives on the stage, because it belongs to the game rather than to any
one coin.

```java
import org.openpatch.scratch.*;

public class CatchStage extends Stage {
  private Text scoreText = new Text();
  private int score = 0;

  public CatchStage() {
    super(600, 400);
    this.addBackdrop("background");
    this.addSound("handleCoins");

    this.scoreText.setPosition(-270, 170);
    this.scoreText.setTextSize(22);
    this.add(this.scoreText);
    this.showScore();

    this.add(new Basket());
    for (int i = 0; i < 4; i++) {
      this.add(new Coin());
    }
  }

  public void addPoint() {
    this.score = this.score + 1;
    this.playSound("handleCoins");
    this.showScore();
  }

  private void showScore() {
    this.scoreText.showText("Coins: " + this.score);
  }

  public static void main(String[] args) {
    new CatchStage();
  }
}
```

Run it by right-clicking `CatchStage` and choosing `new CatchStage()`.

Three things worth noticing:

- **`private int score`** is a variable that belongs to the stage. In Scratch you
  would have made a variable "for all sprites". This is the same idea: one number
  the whole game shares.
- **The `for` loop** creates four coins. In Scratch you would have copied the
  sprite four times by hand.
- **`addPoint()`** is called by the coin. A coin cannot change the stage's score
  directly, so the stage offers a method that does it. That is what
  `((CatchStage) this.getStage()).addPoint()` means: "ask my stage to add a
  point".

## The finished project

If something will not run, you can compare against the finished version.
It needs no assets, only the library itself.

::archive[Project: Catch the Coins 100%]{name="catch-the-coins-100"}

## Things to try

- Make the coins fall faster the more you have caught.
- Add a second kind of sprite that costs you a point — `bomb` is in the
  [built-in sprites](/sprites).
- Stop the game after 30 seconds using `getTimer()`.
