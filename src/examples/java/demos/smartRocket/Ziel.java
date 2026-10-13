package demos.smartRocket;

import org.openpatch.scratch.*;

public class Ziel extends Sprite {

  public Ziel() {
    // begin sprite editor
    this.addCostume("target", "demos/smartRocket/assets/target.png");
    this.setHitbox(10, 38, 10, 10, 38, 10, 38, 38);
    // end sprite editor
  }
}
