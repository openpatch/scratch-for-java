package demos.tiled;

import org.openpatch.scratch.*;

public class Enemy extends Sprite {

  protected String state = "idle";
  protected String dir = "down";

  public Enemy(double x, double y) {
    this.setX(x);
    this.setY(y);

    // begin sprite editor
    // end sprite editor
  }
}
