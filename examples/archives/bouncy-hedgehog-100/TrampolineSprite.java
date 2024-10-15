import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
        this.setPosition(0, -120);
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyCode.VK_LEFT) {
            this.changeX(-10);
        } else if (keyCode == KeyCode.VK_RIGHT) {
            this.changeX(10);
        }
    }
}
