package Clock;

import org.openpatch.scratch.Sprite;

public class HourHandSprite extends Sprite {
  public HourHandSprite() {
    this.addCostume("hand", "Clock/sprites/hour.png");
  }

  public void run() {
    int hour = this.getCurrentHour();
    this.setDirection(90 + hour / 12.0 * 360);
  }
}
