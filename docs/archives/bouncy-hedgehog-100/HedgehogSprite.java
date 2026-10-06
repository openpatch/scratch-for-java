import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Random;

public class HedgehogSprite extends Sprite {

  public HedgehogSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("hedgehog", "hedgehog.png");
    // scratch4j:end setup

    this.pointInDirection(15);
    this.setPosition(-180, 140);
  }

  public void run() {
    if (this.getY() > -120) {
      this.move(1);
      this.ifOnEdgeBounce();

      if (this.isTouchingSprite(TrampolineSprite.class)) {
        this.pointInDirection(Random.random(-45, 45));
      }
    } else {
      this.say("Ouch!", 2000);
    }
  }
}
