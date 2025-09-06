package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePointInDirection {
  public SpritePointInDirection() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Point in different directions
    gamma.pointInDirection(0);   // Point right
    myStage.wait(500);
    gamma.pointInDirection(90);  // Point up
    myStage.wait(500);
    gamma.pointInDirection(180); // Point left
    myStage.wait(500);
    gamma.pointInDirection(270); // Point down
    myStage.wait(500);
    gamma.pointInDirection(45);  // Point diagonal
    myStage.wait(1000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePointInDirection();
  }
}