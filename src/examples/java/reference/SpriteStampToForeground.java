package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteStampToForeground {
  public SpriteStampToForeground() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Create stamps at different positions in foreground layer
    gamma.setPosition(-150, 50);
    gamma.stampToForeground();
    myStage.wait(500);
    
    gamma.setPosition(0, 0);
    gamma.stampToForeground();
    myStage.wait(500);
    
    gamma.setPosition(150, -50);
    gamma.stampToForeground();
    myStage.wait(500);
    
    // Move sprite to show stamps remain in foreground
    gamma.setPosition(-100, -100);
    myStage.wait(1000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteStampToForeground();
  }
}