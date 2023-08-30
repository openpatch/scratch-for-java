import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class SecondHandSprite extends Sprite {

  public SecondHandSprite() {
    this.addCostume("hand", "sprites/second.png");
  }

  public void run() {
    int second = this.getCurrentSecond();
    if (this.isKeyPressed(KeyCode.VK_SPACE)) {
      int millisecond = this.getCurrentMillisecond();
      this.setDirection(90 + (second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setDirection(90 + second / 60.0 * 360);
    }
  }
}
