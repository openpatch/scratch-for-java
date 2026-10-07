---
name: Cat Platformer
---

# Cat Platformer

The Scratch for Java cat in a small platformer. Collect the coffee beans, jump
on the bugs or punch them, and climb up to the steaming mug. Like the
[Coin Collector](/examples/coin-collector), it needs no files: the cat, the
beans, the bugs, the mug and the hearts are built into the library, just like
the grass tiles and the sounds.

![The cat on a platform, with beans, bugs and the mug](/assets/cat-platformer.png)

Walk with the left and right arrow keys, jump with space, punch with X, crouch
with the down arrow, and climb the ladder with up and down.

## The cat's animations

Every animation of the cat is built in. Each frame is 64 by 64 pixels with the
feet on the bottom row, and the frames of an animation are numbered from 1, so
one line adds a whole animation:

```java
this.addAnimation("walk", "cat_walk_%d", 6);
```

![All animations of the cat](/assets/cat-animations.gif)

| Animation | Frames | Plays           |
| --------- | ------ | --------------- |
| `idle`    | 26     | in a loop       |
| `walk`    | 6      | in a loop       |
| `crouch`  | 4      | in a loop       |
| `jump`    | 3      | once, going up  |
| `fall`    | 2      | once, going down |
| `land`    | 2      | once            |
| `punch`   | 6      | once            |
| `hurt`    | 3      | once            |
| `ko`      | 5      | once            |
| `cheer`   | 4      | in a loop       |
| `climb`   | 4      | in a loop       |

The cat faces right, like a sprite in Scratch with direction 90. With
`setRotationStyle(RotationStyle.LEFT_RIGHT)` it turns around when it walks
left.

The things around it are built in too: `coffee_bean_1` to `coffee_bean_4`,
`bug_walk_1` to `bug_walk_4`, `bug_squished_1` and `bug_squished_2`,
`goal_mug_1` to `goal_mug_4`, `heart_full` and `heart_empty`.

## Which animation?

The cat picks its animation from what it is doing, every frame, most important
first:

```java
if (this.hurtFrames > 40) {
  this.play("hurt", true);
} else if (this.punching) {
  this.play("punch", true);
} else if (!this.onGround) {
  this.play(this.fallSpeed > 0 ? "jump" : "fall", true);
} else if (walking) {
  this.play("walk", false);
} else {
  this.play("idle", false);
}
```

`play` is a small helper in `Cat`. An animation that plays once has to start
at its first frame, so the helper resets the animation whenever it changes:

```java
private void play(String name, boolean once) {
  if (!name.equals(this.animation)) {
    this.animation = name;
    this.resetAnimation();
  }
  this.playAnimation(name, once);
}
```

## Run it here

Click the stage so it takes the keyboard.

<!-- demo: catPlatformer -->
:::onlineide{height="640px" libraries="scratch"}

```java CatPlatformer.java

void main() {
  new CatPlatformer();
}

/**
 * The Scratch for Java cat in a small platformer: collect the coffee beans,
 * jump on the bugs (or punch them) and climb up to the steaming mug.
 *
 * <p>
 * Arrow keys walk, climb and crouch, space jumps, X punches. Like the coin
 * collector, it only uses assets built into Scratch for Java - the cat, the
 * beans, the bugs, the mug and the hearts are on the built-in cat sheet, the
 * tiles and the sounds are Kenney's.
 */
class CatPlatformer extends Stage {

  /** The height the ground reaches up to. */
  public static final double GROUND_TOP = -176;

  /** Where the ladder ends: the top of the platform with the mug. */
  public static final double LADDER_TOP = 112;

  private final List<Platform> platforms = new ArrayList<>();
  private final Heart[] hearts = new Heart[3];

  private Text score;
  private int beans = 0;
  private int totalBeans = 0;

  public CatPlatformer() {
    super(800, 480);
    this.addBackdrop("background");

    // The ground, one grass tile at a time.
    for (int x = -400; x < 400; x += 64) {
      this.add(new Ground(x + 32));
    }

    // A ladder from the ground up to the highest platform, added before the
    // platform so it is drawn behind it.
    for (double y = GROUND_TOP + 32; y < LADDER_TOP - 32; y += 64) {
      this.add(new Ladder(232, y, false));
    }
    this.add(new Ladder(232, LADDER_TOP - 32, true));

    // Three platforms, each from its left edge, its top and a number of tiles.
    this.addPlatform(-260, -80, 2);
    this.addPlatform(-40, 16, 2);
    this.addPlatform(200, LADDER_TOP, 3);

    this.addBean(-330, GROUND_TOP + 40);
    this.addBean(-60, GROUND_TOP + 40);
    this.addBean(-196, -80 + 40);
    this.addBean(24, 16 + 40);
    this.addBean(232, -40);
    this.addBean(300, LADDER_TOP + 40);

    this.add(new Bug(-170, -20, GROUND_TOP));
    this.add(new Bug(40, 160, GROUND_TOP));
    this.add(new Bug(280, 340, LADDER_TOP));

    this.add(new Goal(372, LADDER_TOP));
    this.add(new Cat());

    for (int i = 0; i < this.hearts.length; i++) {
      this.hearts[i] = new Heart(-370 + i * 34, 210);
      this.add(this.hearts[i]);
    }
    this.score = new Text();
    this.score.setPosition(-180, 210);
    this.score.setTextSize(24);
    this.add(this.score);
    this.showScore();
  }

  private void addPlatform(double left, double top, int tiles) {
    for (int i = 0; i < tiles; i++) {
      String costume = i == 0 ? "grassHalf_left" : i == tiles - 1 ? "grassHalf_right" : "grassHalf_mid";
      Platform tile = new Platform(costume, left + 32 + i * 64, top);
      this.platforms.add(tile);
      this.add(tile);
    }
  }

  private void addBean(double x, double y) {
    this.add(new Bean(x, y));
    this.totalBeans += 1;
  }

  private void showScore() {
    this.score.showText("Beans: " + this.beans + " / " + this.totalBeans);
  }

  /**
   * Finds what a falling sprite lands on. Between two frames its feet went
   * from feetBefore down to feetAfter; if a platform top (or the ground) lies
   * in between, below x, that is where it stands.
   *
   * @return the height of what it lands on, or NaN while it is still falling
   */
  public double landingHeight(double x, double feetBefore, double feetAfter) {
    double best = Double.NaN;
    for (Platform platform : this.platforms) {
      boolean above = x > platform.getLeft() && x < platform.getRight();
      double top = platform.getTop();
      if (above && feetBefore >= top && feetAfter <= top && !(best >= top)) {
        best = top;
      }
    }
    if (Double.isNaN(best) && feetAfter <= GROUND_TOP) {
      best = GROUND_TOP;
    }
    return best;
  }

  /** Called by a bean when the cat picks it up. */
  public void collectBean() {
    this.beans += 1;
    this.showScore();
  }

  /** Called by the cat when it gets hurt; returns how many hearts are left. */
  public int loseHeart() {
    int left = 0;
    for (Heart heart : this.hearts) {
      if (heart.isFull()) {
        left += 1;
      }
    }
    if (left > 0) {
      this.hearts[left - 1].empty();
      left -= 1;
    }
    return left;
  }

  public void win() {
    this.playSound("jingles_NES00");
    this.showMessage("You made it! " + this.beans + " / " + this.totalBeans + " beans");
  }

  public void lose() {
    this.showMessage("Game over");
  }

  private void showMessage(String message) {
    Text text = new Text();
    text.setPosition(0, 60);
    text.setTextSize(40);
    text.showText(message);
    this.add(text);
  }
}

/** One tile of a platform the cat can stand on. */
class Platform extends Sprite {

  private final double top;

  public Platform(String costume, double x, double top) {
    this.addCostume(costume);
    this.setSize(50);
    this.top = top;
    this.setX(x);
    // The grass of a half tile starts at the top of its 64 pixel costume.
    this.setY(top - 32);
  }

  public double getTop() {
    return this.top;
  }

  public double getLeft() {
    return this.getX() - 32;
  }

  public double getRight() {
    return this.getX() + 32;
  }
}

/** One of the cat's lives, shown in the top left corner. */
class Heart extends Sprite {

  public Heart(double x, double y) {
    this.addCostume("heart_full");
    this.addCostume("heart_empty");
    this.setX(x);
    this.setY(y);
  }

  public boolean isFull() {
    return this.getCurrentCostumeName().equals("heart_full");
  }

  public void empty() {
    this.switchCostume("heart_empty");
  }
}

/** One grass tile of the ground. */
class Ground extends Sprite {

  public Ground(double x) {
    this.addCostume("grassMid");
    this.setSize(50);
    this.setX(x);
    // The tile is 64 pixels high at size 50, so its middle sits 32 below its top.
    this.setY(CatPlatformer.GROUND_TOP - 32);
  }
}

/** One piece of the ladder. Touching it, the cat can climb with the up and down keys. */
class Ladder extends Sprite {

  public Ladder(double x, double y, boolean top) {
    this.addCostume(top ? "ladderTop" : "ladderMid");
    this.setSize(50);
    this.setX(x);
    this.setY(y);
  }
}

/** A bug walking to and fro. Jump on it or punch it - but do not walk into it. */
class Bug extends AnimatedSprite {

  /** The costume is 32 pixels high, the legs end 3 pixels above its bottom. */
  private static final double FEET = 13;

  private final double minX;
  private final double maxX;
  private boolean squished = false;
  private int squishedFrames = 0;

  public Bug(double minX, double maxX, double floor) {
    this.addAnimation("walk", "bug_walk_%d", 4);
    this.addAnimation("squished", "bug_squished_%d", 2);
    this.setAnimationInterval(120);
    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.minX = minX;
    this.maxX = maxX;
    this.setX(minX);
    this.setY(floor + FEET);
  }

  public boolean isSquished() {
    return this.squished;
  }

  public void squish() {
    this.squished = true;
    this.resetAnimation();
  }

  public void run() {
    if (this.squished) {
      this.playAnimation("squished", true);
      this.squishedFrames += 1;
      if (this.squishedFrames > 30) {
        this.remove();
      }
      return;
    }
    this.playAnimation("walk");
    this.move(1);
    if (this.getX() > this.maxX) {
      this.setDirection(-90);
    } else if (this.getX() < this.minX) {
      this.setDirection(90);
    }
  }
}

/** The steaming mug at the end of the level. */
class Goal extends AnimatedSprite {

  public Goal(double x, double floor) {
    this.addAnimation("steam", "goal_mug_%d", 4);
    this.setAnimationInterval(150);
    this.setX(x);
    // the mug stands on the bottom two rows of its 32 pixel costume
    this.setY(floor + 14);
  }

  public void run() {
    this.playAnimation("steam");
  }
}

/** The Scratch for Java cat. Every one of its animations is on the built-in cat sheet. */
class Cat extends AnimatedSprite {

  /** The costumes are 64 pixels high with the feet on the bottom row. */
  private static final double FEET = 32;

  private static final double SPEED = 3;
  private static final double CLIMB_SPEED = 2;
  private static final double JUMP_STRENGTH = 10.5;
  private static final double GRAVITY = 0.5;

  private double fallSpeed = 0;
  private boolean onGround = true;
  private boolean climbing = false;
  private boolean punching = false;
  private boolean dead = false;
  private boolean won = false;
  /** Frames left until the cat can be hurt again. */
  private int hurtFrames = 0;
  /** Frames left of the landing squat. */
  private int landFrames = 0;
  private String animation = "";

  public Cat() {
    // "cat_walk_%d" stands for cat_walk_1 to cat_walk_6.
    this.addAnimation("idle", "cat_idle_%d", 26);
    this.addAnimation("walk", "cat_walk_%d", 6);
    this.addAnimation("crouch", "cat_crouch_%d", 4);
    this.addAnimation("jump", "cat_jump_%d", 3);
    this.addAnimation("fall", "cat_fall_%d", 2);
    this.addAnimation("land", "cat_land_%d", 2);
    this.addAnimation("punch", "cat_punch_%d", 6);
    this.addAnimation("hurt", "cat_hurt_%d", 3);
    this.addAnimation("ko", "cat_ko_%d", 5);
    this.addAnimation("cheer", "cat_cheer_%d", 4);
    this.addAnimation("climb", "cat_climb_%d", 4);
    this.setAnimationInterval(100);
    this.addSound("footstep_grass_000");
    this.addSound("impactPunch_medium_004");
    this.addSound("impactSoft_heavy_000");
    this.addSound("handleCoins");

    this.setRotationStyle(RotationStyle.LEFT_RIGHT);
    this.setX(-360);
    this.setY(CatPlatformer.GROUND_TOP + FEET);
  }

  /** Plays an animation, starting it from its first frame when it changes. */
  private void play(String name, boolean once) {
    if (!name.equals(this.animation)) {
      this.animation = name;
      this.resetAnimation();
    }
    this.playAnimation(name, once);
  }

  private CatPlatformer level() {
    return (CatPlatformer) this.getStage();
  }

  private double feet() {
    return this.getY() - FEET;
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (this.dead || this.won) {
      return;
    }
    if (keyCode == KeyCode.SPACE && (this.onGround || this.climbing)) {
      this.fallSpeed = JUMP_STRENGTH;
      this.onGround = false;
      this.climbing = false;
      this.playSound("footstep_grass_000");
    }
    if (keyCode == KeyCode.X && this.onGround && !this.punching) {
      this.punching = true;
    }
  }

  public void run() {
    if (this.dead) {
      this.fall();
      this.play("ko", true);
      return;
    }
    if (this.won) {
      this.play("cheer", false);
      return;
    }

    boolean walking = this.walk();
    if (!this.climb()) {
      this.fall();
    }
    this.meetBugs();

    Bean bean = this.getTouchingSprite(Bean.class);
    if (bean != null) {
      this.playSound("handleCoins");
      bean.remove();
      this.level().collectBean();
    }
    if (this.isTouchingSprite(Goal.class) && this.onGround) {
      this.won = true;
      this.setTransparency(0);
      this.level().win();
    }

    this.animate(walking);
  }

  /** Left and right; returns whether the cat is walking. */
  private boolean walk() {
    if (this.punching || this.isKeyPressed(KeyCode.DOWN) && this.onGround && !this.isTouchingSprite(Ladder.class)) {
      return false;
    }
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.setDirection(-90);
      this.changeX(-SPEED);
    } else if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.setDirection(90);
      this.changeX(SPEED);
    } else {
      return false;
    }
    this.climbing = false;
    this.setX(Math.max(-380, Math.min(380, this.getX())));
    return true;
  }

  /** Up and down a ladder; returns whether the cat is on one. */
  private boolean climb() {
    boolean onLadder = this.isTouchingSprite(Ladder.class);
    boolean up = this.isKeyPressed(KeyCode.UP);
    boolean down = this.isKeyPressed(KeyCode.DOWN);
    if (onLadder && (up || down) && !this.punching) {
      this.climbing = true;
    }
    if (!onLadder) {
      this.climbing = false;
    }

    if (this.climbing) {
      this.fallSpeed = 0;
      if (up) {
        this.changeY(CLIMB_SPEED);
      } else if (down) {
        this.changeY(-CLIMB_SPEED);
      }
      // At the top of the ladder the cat steps onto the platform, at the
      // bottom onto the ground.
      if (this.feet() >= CatPlatformer.LADDER_TOP) {
        this.setY(CatPlatformer.LADDER_TOP + FEET);
        this.climbing = false;
        this.onGround = true;
      } else if (this.feet() <= CatPlatformer.GROUND_TOP) {
        this.setY(CatPlatformer.GROUND_TOP + FEET);
        this.climbing = false;
        this.onGround = true;
      } else {
        this.onGround = false;
      }
    }
    return this.climbing;
  }

  /** Falls until the feet meet a platform or the ground. */
  private void fall() {
    double before = this.feet();
    this.fallSpeed -= GRAVITY;
    this.changeY(this.fallSpeed);
    double landing = this.level().landingHeight(this.getX(), before, this.feet());
    if (!Double.isNaN(landing) && this.fallSpeed <= 0) {
      if (!this.onGround && this.fallSpeed < -4) {
        this.landFrames = 8;
      }
      this.setY(landing + FEET);
      this.fallSpeed = 0;
      this.onGround = true;
    } else {
      this.onGround = false;
    }
  }

  private void meetBugs() {
    if (this.hurtFrames > 0) {
      this.hurtFrames -= 1;
      // blink while it cannot be hurt
      this.setTransparency(this.hurtFrames / 5 % 2 == 0 ? 0 : 60);
    }
    for (Bug bug : this.getTouchingSprites(Bug.class)) {
      if (bug.isSquished()) {
        continue;
      }
      boolean inFront = (bug.getX() - this.getX()) * (this.getDirection() > 0 ? 1 : -1) > 0;
      if (this.fallSpeed < 0 && this.feet() > bug.getY()) {
        // landed on it
        bug.squish();
        this.fallSpeed = 6;
        this.playSound("impactPunch_medium_004");
      } else if (this.punching && inFront && this.getAnimationFrame() >= 3) {
        bug.squish();
        this.playSound("impactPunch_medium_004");
      } else if (this.hurtFrames == 0) {
        this.getHurt(bug);
      }
    }
  }

  private void getHurt(Bug bug) {
    this.playSound("impactSoft_heavy_000");
    this.punching = false;
    this.hurtFrames = 60;
    // knocked back, away from the bug
    this.changeX(this.getX() < bug.getX() ? -24 : 24);
    this.fallSpeed = 5;
    this.onGround = false;
    if (this.level().loseHeart() == 0) {
      this.dead = true;
      this.setTransparency(0);
      this.level().lose();
    }
  }

  private void animate(boolean walking) {
    if (this.hurtFrames > 40) {
      this.play("hurt", true);
    } else if (this.punching) {
      this.play("punch", true);
      if (this.isAnimationPlayed()) {
        this.punching = false;
      }
    } else if (this.climbing) {
      // only move the arms and legs while climbing
      if (this.isKeyPressed(KeyCode.UP) || this.isKeyPressed(KeyCode.DOWN)) {
        this.play("climb", false);
      }
    } else if (!this.onGround) {
      this.play(this.fallSpeed > 0 ? "jump" : "fall", true);
    } else if (this.landFrames > 0) {
      this.landFrames -= 1;
      this.play("land", true);
    } else if (this.isKeyPressed(KeyCode.DOWN)) {
      this.play("crouch", false);
    } else if (walking) {
      this.play("walk", false);
    } else {
      this.play("idle", false);
    }
  }
}

/** A spinning coffee bean to collect. */
class Bean extends AnimatedSprite {

  public Bean(double x, double y) {
    this.addAnimation("spin", "coffee_bean_%d", 4);
    this.setAnimationInterval(120);
    this.setX(x);
    this.setY(y);
  }

  public void run() {
    this.playAnimation("spin");
  }
}
```

:::
