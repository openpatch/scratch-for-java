package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteChangeSize {
  public SpriteChangeSize() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Change size by increasing it
    while (myStage.getTimer().forMillis(2000)) {
      gamma.changeSize(2);
      myStage.wait(50);
    }
    
    // Change size by decreasing it
    while (myStage.getTimer().forMillis(4000)) {
      gamma.changeSize(-3);
      myStage.wait(50);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteChangeSize();
  }
}