import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.extensions.Recorder;

public class AnimatedSpriteIsAnimationPlayed {
    public AnimatedSpriteIsAnimationPlayed() {
        Stage myStage = new Stage(254, 100);
        AnimatedSprite bee = new AnimatedSprite();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        myStage.add(bee);

        Recorder recorder = new GifRecorder("" + this.getClass().getName());
        recorder.start();
        while(true) {
            bee.playAnimation("idle", true);
            if (bee.isAnimationPlayed()) {
                break;
            }
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new AnimatedSpriteIsAnimationPlayed();
    }
}
