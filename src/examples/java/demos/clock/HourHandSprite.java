package demos.clock;

import org.openpatch.scratch.Clock;
import org.openpatch.scratch.Sprite;

public class HourHandSprite extends Sprite {
  public HourHandSprite() {
    this.addCostume("hand", "demos/clock/sprites/hour.png");
  }

  public void run() {
    int hour = Clock.getHour();
    this.setDirection(90 + hour / 12.0 * 360);
  }
}
