import org.openpatch.scratch.*;

public class StageAddSound {

  public StageAddSound() {
    Stage myStage = new Stage(254, 100);
    myStage.addSound("bump", "assets/bump.wav");
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageAddSound();
  }
}
