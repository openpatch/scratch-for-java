package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteAddCostumes {
  public SpriteAddCostumes() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite();
    
    // Add costumes from spritesheet (using bee animation as example)
    mySprite.addCostumes("frame", "assets/bee_idle.png", 36, 34);
    mySprite.setSize(200); // Make it bigger to see clearly
    
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Cycle through the costumes added from spritesheet
    for (int i = 0; i < 12; i++) { // Assuming multiple frames were added
      mySprite.switchCostume("frame" + (i % 6)); // Cycle through frames
      myStage.wait(200);
    }
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteAddCostumes();
  }
}