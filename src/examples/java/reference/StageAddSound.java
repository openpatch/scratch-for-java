package reference;
import org.openpatch.scratch.*;

public class StageAddSound {

  public StageAddSound() {
    Stage myStage = new Stage(600, 240);
    myStage.addSound("bump", "assets/bump.wav");
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageAddSound();
  }
}
