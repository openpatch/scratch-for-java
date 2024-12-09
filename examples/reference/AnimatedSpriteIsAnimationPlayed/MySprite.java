package AnimatedSpriteIsAnimationPlayed;
import org.openpatch.scratch.extensions.animation.AnimatedSprite;

public class MySprite extends AnimatedSprite {
    public MySprite() {
        this.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
    }    

    public void run() {
        this.playAnimation("idle", true);
      if (this.isAnimationPlayed()) {
        this.say("Animation is played");
      } else {
        this.say("Animation is not played");
      }
    }
}
