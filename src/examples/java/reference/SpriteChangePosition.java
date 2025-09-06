package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.math.Vector2;

public class SpriteChangePosition {
  public SpriteChangePosition() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Change position using vectors
    for (int i = 0; i < 6; i++) {
      Vector2 change = new Vector2(50, 30);
      gamma.changePosition(change);
      myStage.wait(400);
      
      change = new Vector2(-30, -20);
      gamma.changePosition(change);
      myStage.wait(400);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteChangePosition();
  }
}