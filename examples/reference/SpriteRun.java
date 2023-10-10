import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteRun {

  public SpriteRun() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new Zeta());
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      // wait for 3000 millis
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteRun();
  }
}

class Zeta extends Sprite {

  public Zeta() {
    super("green", "assets/zeta_green_badge.png");
  }

  @Override
  public void run() {
    this.move(5);
  }
}
