import org.openpatch.scratch.Sprite;

public class PumpkinSprite extends Sprite {

  public PumpkinSprite() {
    this.addAnimation("idle", "sprites/pumpkin/pumpkin_%02d.png", 2);
    this.setAnimationInterval((this.pickRandom(300, 380)) / 1000.0);
    this.setSize(this.pickRandom(5, 15));
    this.setColorEffect(this.pickRandom(0, 200));
    this.setX(this.pickRandom(-200, 200));
    this.setY(this.pickRandom(-200, 200));
  }

  public void run() {
    this.playAnimation("idle");
  }
}
