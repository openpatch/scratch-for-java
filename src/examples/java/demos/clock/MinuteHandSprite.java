package demos.clock;

import org.openpatch.scratch.Clock;
import org.openpatch.scratch.Sprite;

public class MinuteHandSprite extends Sprite {
  public MinuteHandSprite() {
    this.addCostume("hand", "demos/clock/sprites/minute.png");
  }

  public void run() {
    int minute = Clock.getMinute();
    this.setDirection(90 + minute / 60.0 * 360);
  }
}
