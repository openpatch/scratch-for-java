import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteWhenKeyPressed {

  class CustomSprite extends Sprite {

    public CustomSprite() {
      this.addCostume("zeta", "assets/zeta_green_badge.png");
      this.addCostume("gamma", "assets/gamma_purple_badge.png");
    }

    @Override
    public void whenKeyPressed(int keyCode) {
      if (keyCode == KeyCode.VK_W) {
        this.changeY(20);
      } else if (keyCode == KeyCode.VK_S) {
        this.changeY(-20);
      } else if (keyCode == KeyCode.VK_A) {
        this.changeX(-20);
      } else if (keyCode == KeyCode.VK_D) {
        this.changeX(20);
      }
    }
  }

  public SpriteWhenKeyPressed() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000))
      ;
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteWhenKeyPressed();
  }
}
