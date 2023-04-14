 

import org.openpatch.scratch.*;

public class MinuteHandSprite extends Sprite {
  public MinuteHandSprite() {
    this.addCostume("hand", "sprites/minute.png");
  }

  public void run() {
    int minute = this.getCurrentMinute();
    this.setDirection(minute / 60.0 * 360);
  }
}
