import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Stage;

public class StagePlaySound {

    public StagePlaySound() {
        Stage myStage = new Stage(254, 100);
        myStage.addSound("bump", "assets/bump.wav");

        while(myStage.getTimer().forMillis(3000)) {
            myStage.playSound("bump");
            myStage.wait(500);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        new StagePlaySound();
    }
}
