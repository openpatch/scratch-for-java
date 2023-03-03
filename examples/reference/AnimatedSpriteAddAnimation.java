import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.Recorder;

public class AnimatedSpriteAddAnimation {
    public AnimatedSpriteAddAnimation() {
        Stage myStage = new Stage(254, 100);
        AnimatedSprite bee = new AnimatedSprite();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        myStage.add(bee);

        Recorder recorder = new GifRecorder("" + this.getClass().getName());
        recorder.start();
        while(myStage.getTimer().forMillis(5000)) {
            bee.playAnimation("idle");
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new AnimatedSpriteAddAnimation();
    }
}
