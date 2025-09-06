package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetWidth {
  public SpriteSetWidth() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Change width progressively
    gamma.setWidth(50);
    myStage.wait(800);
    gamma.setWidth(120);
    myStage.wait(800);
    gamma.setWidth(200);
    myStage.wait(800);
    gamma.setWidth(80);
    myStage.wait(800);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetWidth();
  }
}