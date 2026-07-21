package demos.coinCollector;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.extensions.animation.AnimatedSprite;

/** The alien you steer with the arrow keys. */
public class Player extends AnimatedSprite {

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
