package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.GifRecorder;

public class SpriteIsTouchingSprite {
  public SpriteIsTouchingSprite() {
    Stage myStage = new Stage(600, 240);

    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    gamma.setPosition(-120, 50);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    zeta.setPosition(120, 50);
    myStage.add(zeta);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (!gamma.isTouchingSprite(zeta)) {
      gamma.changeX(5);
      zeta.changeX(-5);
      myStage.wait(100);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsTouchingSprite();
  }
}
