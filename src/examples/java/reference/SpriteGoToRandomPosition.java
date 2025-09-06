package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGoToRandomPosition {
  public SpriteGoToRandomPosition() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Move to random positions several times
    for (int i = 0; i < 6; i++) {
      gamma.goToRandomPosition();
      myStage.wait(500);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGoToRandomPosition();
  }
}