import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;
import org.openpatch.scratch.extensions.recorder.*;

public class AnimatedSpriteSetAnimationFrame {
  public AnimatedSpriteSetAnimationFrame() {
    Stage myStage = new Stage(600, 240);
    AnimatedSprite bee = new AnimatedSprite();
    bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    myStage.add(bee);
    bee.changeY(20);

    Recorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName());
    recorder.start();
    while (myStage.getTimer().forMillis(5000)) {
      bee.playAnimation("idle");
      if (myStage.isKeyPressed(KeyCode.VK_SPACE)) {
        bee.setAnimationFrame(0);
      }
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new AnimatedSpriteSetAnimationFrame();
  }
}
