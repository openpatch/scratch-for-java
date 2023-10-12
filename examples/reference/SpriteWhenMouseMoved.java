import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteWhenMouseMoved {

  class CustomSprite extends Sprite {

    public CustomSprite() {
      this.addCostume("zeta", "assets/zeta_green_badge.png");
      this.addCostume("gamma", "assets/gamma_purple_badge.png");
    }

    @Override
    public void whenMouseMoved(double x, double y) {
      this.setPosition(x, y);
    }
  }

  public SpriteWhenMouseMoved() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {}
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteWhenMouseMoved();
  }
}
