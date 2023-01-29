import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Stage;

public class StageRemoveSound {

    public StageRemoveSound() {
        Stage myStage = new Stage(254, 100);
        myStage.addSound("bump", "assets/bump.wav");
        myStage.removeSound("bump");
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageRemoveSound();
    }
}
