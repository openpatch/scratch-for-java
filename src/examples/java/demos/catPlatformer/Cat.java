package demos.catPlatformer;

import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.RotationStyle;

/** The Scratch for Java cat. Every one of its animations is on the built-in cat sheet. */
public class Cat extends AnimatedSprite {

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
