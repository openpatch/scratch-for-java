 

import org.openpatch.scratch.*;

public class SecondHandSprite extends Sprite {

  public SecondHandSprite() {
    this.addCostume("hand", "sprites/second.png");
  }

  public void run() {
    int second = this.getCurrentSecond();
    if (isKeyPressed(32)) {
      int millisecond = this.getCurrentMillisecond();
      this.setDirection((second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setDirection(second / 60.0 * 360);
    }
  }
}
