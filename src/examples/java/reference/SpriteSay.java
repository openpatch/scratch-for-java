package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteSay {
  public SpriteSay() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    myStage.add(zeta);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    zeta.say("Hi! I'm Zeta and can say line breaks");
    myStage.wait(3000);
    recorder.snapshot();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteSay();
  }
}
