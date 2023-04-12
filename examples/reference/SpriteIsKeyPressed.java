import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class SpriteIsKeyPressed {
    public SpriteIsKeyPressed() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        mySprite.changeX(-80);
        mySprite.changeY(30);
        myStage.add(mySprite);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            mySprite.say("Space pressed? " + mySprite.isKeyPressed(KeyCode.VK_SPACE));
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteIsKeyPressed();
    }
}