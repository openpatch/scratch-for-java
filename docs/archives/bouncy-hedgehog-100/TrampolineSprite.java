import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    this.addCostume("trampoline", "trampoline.png");
    this.setPosition(0, -120);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.LEFT) {
      this.changeX(-10);
    } else if (keyCode == KeyCode.RIGHT) {
      this.changeX(10);
    }
  }
}
