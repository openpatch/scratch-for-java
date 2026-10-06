package demos.clock;

import org.openpatch.scratch.Clock;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class SecondHandSprite extends Sprite {

  public SecondHandSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("hand", "demos/clock/sprites/second.png");
    // scratch4j:end setup
  }

  public void run() {
    int second = Clock.getSecond();
    if (this.isKeyPressed(KeyCode.SPACE)) {
      int millisecond = Clock.getMillisecond();
      this.setDirection(90 + (second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setDirection(90 + second / 60.0 * 360);
    }
  }
}
