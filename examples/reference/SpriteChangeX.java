import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class SpriteChangeX {
    public SpriteChangeX() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(2000);
        mySprite.changeX(100);
        myStage.wait(2000);
    
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteChangeX();
    }
}