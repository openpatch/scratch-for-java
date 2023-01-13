import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteTurnLeft {
    public SpriteTurnLeft() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        myStage.add(zeta);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000)) {
            zeta.turnLeft(2);
            myStage.wait(50);
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteTurnLeft();
    }
}