package demos.smartRocket;

import org.openpatch.scratch.*;

public class Ziel extends Sprite {

  public Ziel() {
    // begin sprite editor
    this.addCostume("target", "demos/smartRocket/assets/target.png");
    this.setHitbox(new Polygon(new double[] {10, 10, 38, 38}, new double[] {38, 10, 10, 38}));
    // end sprite editor
  }
}
