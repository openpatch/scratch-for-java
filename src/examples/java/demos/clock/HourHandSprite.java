package demos.clock;

import org.openpatch.scratch.Clock;
import org.openpatch.scratch.Sprite;

public class HourHandSprite extends Sprite {
  public HourHandSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("hand", "demos/clock/sprites/hour.png");
    // scratch4j:end setup
  }

  public void run() {
    int hour = Clock.getHour();
    this.setDirection(90 + hour / 12.0 * 360);
  }
}
