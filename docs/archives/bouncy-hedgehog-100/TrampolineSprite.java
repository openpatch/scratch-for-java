import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
  public TrampolineSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("trampoline", "trampoline.png");
    // scratch4j:end setup
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
