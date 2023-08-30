import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.extensions.animation.AnimatedSprite;

public class GhostSprite extends AnimatedSprite {

  private int speed = 8;

  public GhostSprite() {
    this.addAnimation("idle", "sprites/ghost/ghost_%02d.png", 10);
    this.addSound("laugh", "sounds/ghost.wav");
    this.setSize(70);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.VK_LEFT)) {
      this.changeX(-this.speed);
    } else if (this.isKeyPressed(KeyCode.VK_RIGHT)) {
      this.changeX(this.speed);
    }
    if (this.isKeyPressed(KeyCode.VK_UP)) {
      this.changeY(this.speed);
    } else if (this.isKeyPressed(KeyCode.VK_DOWN)) {
      this.changeY(-this.speed);
    }
    if (this.isKeyPressed(KeyCode.VK_SPACE)) {
      this.playSound("laugh");
    }
    this.playAnimation("idle");
  }
}
