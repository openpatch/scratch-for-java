import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class SpritePickRandom {
    public SpritePickRandom() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.changeY(40);
        myStage.add(zeta);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            int random = zeta.pickRandom(0, 100);
            zeta.think("Random: " + random);
            myStage.wait(200);
        }
        recorder.stop();
        Window.getInstance().exit();
    }
    public static void main(String[] args) {
        new SpritePickRandom();
    }
}
