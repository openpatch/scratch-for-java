import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    // begin sprite editor
    this.addCostume("trampoline", "trampoline.png");
    // end sprite editor
    this.setPosition(0, -120);
  }

  public void whenKeyPressed(KeyCode key) {
    if (key == KeyCode.LEFT) {
      this.changeX(-10);
    } else if (key == KeyCode.RIGHT) {
      this.changeX(10);
    }
  }
}
