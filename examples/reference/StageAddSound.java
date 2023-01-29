import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageAddSound {

    public StageAddSound() {
        Stage myStage = new Stage(254, 100);
        myStage.addSound("bump", "assets/bump.wav");
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageAddSound();
    }
}
