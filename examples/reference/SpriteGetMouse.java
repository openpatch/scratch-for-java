import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetMouse {
    public SpriteGetMouse() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        mySprite.changeY(30);
        mySprite.changeX(-100);
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (myStage.getTimer().forMillis(3000)) {
            mySprite.say("X: " + mySprite.getMouseX() + " Y: " + mySprite.getMouseY());
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteGetMouse();
    }
}
