import org.openpatch.scratch.Random;
import org.openpatch.scratch.Sprite;

public class HedgehogSprite extends Sprite {

  public HedgehogSprite() {
    // begin sprite editor
    this.addCostume("hedgehog", "hedgehog.png");
    // end sprite editor

    this.setPosition(-180, 140);
    this.setDirection(160);
  }

  public void run() {
    if (this.getY() > -100) {
      this.move(2);
      this.ifOnEdgeBounce();

      if (this.isTouchingSprite(TrampolineSprite.class)) {
        this.setDirection(Random.randomInt(-45, 45));
      }
    } else {
      this.say("Ouch!");
    }
  }
}
