---
name: Coin Collector
---

# Coin Collector

A small game built entirely from the assets that ship with Scratch for Java.
Look into the project folder: there is no images or sounds folder. Every
costume, backdrop and sound is written as a plain name.

![coin collector example](/assets/coin-collector.gif)

Walk with the left and right arrow keys, jump with space, and collect all six
coins.

## Assets by name

The alien, the coins, the grass and the sky all come from the library:

```java
this.addCostume("alienGreen_stand");
this.addAnimation("walk", "alienGreen_walk%d", 2);
this.addSound("handleCoins");
```

The backdrop works the same way:

```java
this.addBackdrop("background");
```

You can look up every available name here:

- [Built-in Sprites](/sprites)
- [Built-in Sounds](/sounds)

## Run it here

Click the stage so it takes the keyboard, then walk with the left and right
arrow keys, jump with space, and collect all six coins.

:::onlineide{height="640px" libraries="scratch"}

```java CoinCollector.java

void main() {
  new CoinCollector();
}

class CoinCollector extends Stage {

  /** The height the ground reaches up to. Sprites stand on this line. */
  public static final double GROUND_TOP = -176;

  private static final int COINS = 6;

  private Text score;
  private int collected = 0;

  public CoinCollector() {
    super(800, 480);

    // A backdrop and a sound, by name - no files needed.
    this.addBackdrop("background");
    this.addSound("jingles_NES00");

    // A row of grass tiles along the bottom.
    for (int x = -400; x < 400; x += 64) {
      this.add(new Ground(x + 32));
    }

    // A few coins to collect, spread across the level. They start to the right
    // of the player, so none of them is picked up right away.
    for (int i = 0; i < COINS; i++) {
      this.add(new Coin(-240 + i * 112, GROUND_TOP + 45));
    }

    this.add(new Player());

    this.score = new Text();
    this.score.setPosition(-330, 200);
    this.score.setTextSize(28);
    this.score.showText("Coins: 0 / " + COINS);
    this.add(this.score);
  }

  /** Called by a coin when the player picks it up. */
  public void collect() {
    this.collected += 1;
    this.score.showText("Coins: " + this.collected + " / " + COINS);

    if (this.collected == COINS) {
      this.playSound("jingles_NES00");

      Text won = new Text();
      won.setPosition(0, 60);
      won.setTextSize(48);
      won.showText("You got them all!");
      this.add(won);
    }
  }
}

/** The alien you steer with the arrow keys. */
class Player extends AnimatedSprite {

  private static final int SIZE = 45;

  /** How far the middle of the costume sits above the feet. */
  private static final double FEET = 128 * SIZE / 100.0;

  private static final double SPEED = 3;
  private static final double JUMP_STRENGTH = 9;
  private static final double GRAVITY = 0.5;

  private double fallSpeed = 0;

  public Player() {
    // Costumes and animations by name. "alienGreen_walk%d" stands for
    // alienGreen_walk1 and alienGreen_walk2.
    this.addCostume("alienGreen_stand");
    this.addCostume("alienGreen_jump");
    this.addAnimation("walk", "alienGreen_walk%d", 2);
    this.setAnimationInterval(120);

    // Sounds by name, just like costumes.
    this.addSound("handleCoins");
    this.addSound("footstep_grass_000");

    this.setSize(SIZE);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setX(-350);
    this.setY(CoinCollector.GROUND_TOP + FEET);
  }

  private boolean isOnGround() {
    return this.getY() <= CoinCollector.GROUND_TOP + FEET;
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE && this.isOnGround()) {
      this.fallSpeed = JUMP_STRENGTH;
      this.playSound("footstep_grass_000");
    }
  }

  public void run() {
    boolean walking = false;

    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.changeX(-SPEED);
      walking = true;
    } else if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.changeX(SPEED);
      walking = true;
    }

    // Fall down until the feet are back on the ground.
    this.fallSpeed -= GRAVITY;
    this.changeY(this.fallSpeed);
    if (this.isOnGround()) {
      this.setY(CoinCollector.GROUND_TOP + FEET);
      this.fallSpeed = 0;
    }

    if (!this.isOnGround()) {
      this.switchCostume("alienGreen_jump");
    } else if (walking) {
      this.playAnimation("walk");
    } else {
      this.switchCostume("alienGreen_stand");
    }

    // Stay inside the stage.
    if (this.getX() < -380) {
      this.setX(-380);
    }
    if (this.getX() > 380) {
      this.setX(380);
    }

    Coin coin = this.getTouchingSprite(Coin.class);
    if (coin != null) {
      this.playSound("handleCoins");
      coin.remove();
      ((CoinCollector) this.getStage()).collect();
    }
  }
}

/** A coin the player can pick up. It bobs up and down a little. */
class Coin extends Sprite {

  private final double startY;
  private double angle = 0;

  public Coin(double x, double y) {
    this.addCostume("coinGold");
    this.setSize(50);
    this.setX(x);
    this.setY(y);
    this.startY = y;
  }

  public void run() {
    this.angle += 3;
    this.setY(this.startY + Math.sin(Math.toRadians(this.angle)) * 8);
  }
}

/** One grass tile of the ground. */
class Ground extends Sprite {

  public Ground(double x) {
    this.addCostume("grassMid");
    this.setSize(50);
    this.setX(x);
    // The tile is 128 pixels high, so its middle sits 32 below its top edge.
    this.setY(CoinCollector.GROUND_TOP - 32);
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/coinCollector
