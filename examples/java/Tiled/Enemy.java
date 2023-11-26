 

import org.openpatch.scratch.extensions.animation.*;

public class Enemy extends AnimatedSprite {

  protected String state = "idle";
  protected String dir = "down";

  public Enemy(double x, double y) {
    this.setX(x);
    this.setY(y);
  }
}
