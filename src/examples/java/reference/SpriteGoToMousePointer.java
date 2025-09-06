package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGoToMousePointer {
  public SpriteGoToMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Move to mouse pointer periodically
    while (myStage.getTimer().forMillis(3000)) {
      gamma.goToMousePointer();
      myStage.wait(200);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGoToMousePointer();
  }
}