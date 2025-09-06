package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePreviousCostume {
  public SpritePreviousCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addCostume("yellow", "assets/zeta_yellow_badge.png");
    myStage.add(zeta);
    
    // Start with yellow costume
    zeta.switchCostume("yellow");
    
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    myStage.wait(1000);
    zeta.previousCostume(); // Switch back to green
    myStage.wait(1000);
    zeta.previousCostume(); // Switch to yellow again (wraps around)
    myStage.wait(1000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePreviousCostume();
  }
}