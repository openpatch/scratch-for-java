import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.AnimatedSprite;

public class GhostSprite extends AnimatedSprite {

  private int speed = 8;

  public GhostSprite() {
    this.addAnimation("idle", "sprites/ghost/ghost_%02d.png", 10);
    this.addSound("laugh", "sounds/ghost.wav");
    this.setSize(70);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.LEFT)) {
      this.changeX(-this.speed);
    } else if (this.isKeyPressed(KeyCode.RIGHT)) {
      this.changeX(this.speed);
    }
    if (this.isKeyPressed(KeyCode.UP)) {
      this.changeY(this.speed);
    } else if (this.isKeyPressed(KeyCode.DOWN)) {
      this.changeY(-this.speed);
    }
    if (this.isKeyPressed(KeyCode.SPACE)) {
      this.playSound("laugh");
    }
    this.playAnimation("idle");
  }
}
