import org.openpatch.scratch.*;

public class StageRemoveSound {

  public StageRemoveSound() {
    Stage myStage = new Stage(600, 240);
    myStage.addSound("bump", "assets/bump.wav");
    myStage.removeSound("bump");
    myStage.exit();
  }

  public static void main(String[] args) {
    new StageRemoveSound();
  }
}
