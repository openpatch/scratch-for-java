import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteTimer {
    public SpriteTimer() {
        Stage myStage = new Stage(254, 100);
        Sprite timeMe = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        timeMe.addCostume("gamma", "examples/java/assets/gamma_purple_badge.png");
        timeMe.addTimer("identityChange");
        myStage.add(timeMe);

        while(true) {
            if(timeMe.getTimer("identityChange").everyMillis(1000)) {
                timeMe.nextCostume();
            }
        }
    }

    public static void main(String[] args) {
        new SpriteTimer();
    }
}