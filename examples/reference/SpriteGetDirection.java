import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetDirection {
    public SpriteGetDirection() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.setDirection(45);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        mySprite.say("Direction: " + mySprite.getDirection());
        myStage.wait(3000);
        recorder.snapshot();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteGetDirection();
    }
}