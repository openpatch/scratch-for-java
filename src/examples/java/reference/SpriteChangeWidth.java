package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteChangeWidth {
  public SpriteChangeWidth() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Change width by increasing and decreasing
    while (myStage.getTimer().forMillis(2000)) {
      gamma.changeWidth(3);
      myStage.wait(50);
    }
    
    while (myStage.getTimer().forMillis(4000)) {
      gamma.changeWidth(-4);
      myStage.wait(50);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteChangeWidth();
  }
}