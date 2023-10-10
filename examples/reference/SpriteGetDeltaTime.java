import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetDeltaTime {
    public SpriteGetDeltaTime() {
        Stage myStage = new Stage(600, 240);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
        recorder.start();

        while (myStage.getTimer().forMillis(3000)) {
            var dt = mySprite.getDeltaTime();
            mySprite.say("Delta Time: " + dt);
        }

        recorder.stop();
        myStage.exit();
    }

    public static void main(String[] args) {
        new SpriteGetDeltaTime();
    }
}
