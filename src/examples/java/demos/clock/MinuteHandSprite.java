package demos.clock;

import org.openpatch.scratch.Clock;
import org.openpatch.scratch.Sprite;

public class MinuteHandSprite extends Sprite {
  public MinuteHandSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("hand", "demos/clock/sprites/minute.png");
    // scratch4j:end setup
  }

  public void run() {
    int minute = Clock.getMinute();
    this.setDirection(90 + minute / 60.0 * 360);
  }
}
