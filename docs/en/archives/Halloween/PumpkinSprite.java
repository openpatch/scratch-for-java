import org.openpatch.scratch.*;

public class PumpkinSprite extends AnimatedSprite {

  public PumpkinSprite() {
    this.addAnimation("idle", "sprites/pumpkin/pumpkin_%02d.png", 2);
    this.setAnimationInterval(this.pickRandom(300, 380));
    this.setSize(this.pickRandom(5, 15));
    this.setTint(this.pickRandom(0, 255));
    this.setX(this.pickRandom(0, 960));
    this.setY(this.pickRandom(0, 540));
  }

  public void run() {
    this.playAnimation("idle");
  }
}
