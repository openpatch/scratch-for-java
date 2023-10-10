import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;
import org.openpatch.scratch.extensions.recorder.*;

public class AnimatedSpriteIsAnimationPlayed {
  public AnimatedSpriteIsAnimationPlayed() {
    Stage myStage = new Stage(600, 240);
    AnimatedSprite bee = new AnimatedSprite();
    bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    myStage.add(bee);

    Recorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName());
    recorder.start();
    while (true) {
      bee.playAnimation("idle", true);
      if (bee.isAnimationPlayed()) {
        break;
      }
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new AnimatedSpriteIsAnimationPlayed();
  }
}
