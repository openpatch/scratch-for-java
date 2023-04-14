import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;
import org.openpatch.scratch.extensions.recorder.*;

public class AnimatedSpritePlayAnimation {
  public AnimatedSpritePlayAnimation() {
    Stage myStage = new Stage(254, 100);
    AnimatedSprite bee = new AnimatedSprite();
    bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    myStage.add(bee);
    bee.changeX(-30);

    AnimatedSprite bee2 = new AnimatedSprite();
    bee2.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    myStage.add(bee2);
    bee2.changeX(30);

    Recorder recorder = new GifRecorder("" + this.getClass().getName());
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      bee.playAnimation("idle", true);
      bee2.playAnimation("idle");
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new AnimatedSpritePlayAnimation();
  }
}
