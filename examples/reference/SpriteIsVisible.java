import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsVisible {
  public SpriteIsVisible() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.wait(2000);
    gamma.hide();
    myStage.display("Gamma Visible? " + gamma.isVisible());
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsVisible();
  }
}
