import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteDistanceToMousePointer {
  public SpriteDistanceToMousePointer() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    gamma.setPosition(0, 50);
    myStage.add(gamma);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      gamma.changeX(5);
      myStage.display("Distance: " + gamma.distanceToMousePointer());
      myStage.wait(100);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteDistanceToMousePointer();
  }
}
