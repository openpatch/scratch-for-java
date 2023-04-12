import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class SpriteGetPen {
    public SpriteGetPen() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        mySprite.getPen().down();
        while(myStage.getTimer().forMillis(3000)) {
            mySprite.changeX(5);
            myStage.wait(100);

        }
        mySprite.getPen().up();
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteGetPen();
    }
}
