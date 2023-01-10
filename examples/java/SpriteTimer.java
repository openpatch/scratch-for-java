import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteTimer {
    public SpriteTimer() {
        Stage myStage = new Stage(254, 100);
        Sprite timeMe = new Sprite("zeta", "assets/zeta_green_badge.png");
        timeMe.addCostume("gamma", "assets/gamma_purple_badge.png");
        timeMe.addTimer("identityChange");
        myStage.add(timeMe);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(6000)) {
            if(timeMe.getTimer("identityChange").everyMillis(1000)) {
                timeMe.nextCostume();
            }
        }
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteTimer();
    }
}