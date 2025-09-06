package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteClone {
  public SpriteClone() {
    Stage myStage = new Stage(600, 240);
    Sprite original = new Sprite("gamma", "assets/gamma_purple_badge.png");
    original.setPosition(-100, 0);
    myStage.add(original);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Clone the sprite
    Sprite clone = original.clone();
    clone.setPosition(100, 0);
    myStage.add(clone);
    
    myStage.wait(1500);
    
    // Move both sprites to show they are independent
    original.move(30);
    clone.move(-30);
    
    myStage.wait(1500);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteClone();
  }
}