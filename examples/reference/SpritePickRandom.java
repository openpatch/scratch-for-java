import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpritePickRandom {
  public SpritePickRandom() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    myStage.add(zeta);

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      int random = zeta.pickRandom(0, 100);
      zeta.think("Random: " + random);
      myStage.wait(200);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpritePickRandom();
  }
}
