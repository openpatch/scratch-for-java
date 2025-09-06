package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSetHeight {
  public SpriteSetHeight() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Change height progressively
    gamma.setHeight(50);
    myStage.wait(800);
    gamma.setHeight(120);
    myStage.wait(800);
    gamma.setHeight(200);
    myStage.wait(800);
    gamma.setHeight(80);
    myStage.wait(800);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSetHeight();
  }
}