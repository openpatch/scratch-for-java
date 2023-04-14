import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;
import org.openpatch.scratch.extensions.recorder.*;

public class AnimatedSpriteSetAnimationInterval {
  public AnimatedSpriteSetAnimationInterval() {
    Stage myStage = new Stage(254, 100);
    AnimatedSprite bee = new AnimatedSprite();
    bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    myStage.add(bee);
    bee.changeY(30);

    Recorder recorder = new GifRecorder("" + this.getClass().getName());
    recorder.start();
    while (myStage.getTimer().forMillis(2000)) {
      bee.playAnimation("idle");
      bee.say("Interval: " + bee.getAnimationInterval());
    }
    bee.setAnimationInterval(20);
    myStage.getTimer().reset();
    while (myStage.getTimer().forMillis(2000)) {
      bee.playAnimation("idle");
      bee.say("Interval: " + bee.getAnimationInterval());
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new AnimatedSpriteSetAnimationInterval();
  }
}
