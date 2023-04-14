import org.openpatch.scratch.*;

public class StageRemoveSound {

  public StageRemoveSound() {
    Stage myStage = new Stage(254, 100);
    myStage.addSound("bump", "assets/bump.wav");
    myStage.removeSound("bump");
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageRemoveSound();
  }
}
