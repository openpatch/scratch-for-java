package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteAddTimer {
  public SpriteAddTimer() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addCostume("yellow", "assets/zeta_yellow_badge.png");
    zeta.addTimer("costumeChange");
    myStage.add(zeta);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      if (zeta.getTimer("costumeChange").everyMillis(500)) {
        zeta.nextCostume();
      }
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteAddTimer();
  }
}
