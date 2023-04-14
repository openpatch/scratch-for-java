import org.openpatch.scratch.*;

public class StagePlaySound {

  public StagePlaySound() {
    Stage myStage = new Stage(254, 100);
    myStage.addSound("bump", "assets/bump.wav");

    while (myStage.getTimer().forMillis(3000)) {
      myStage.playSound("bump");
      myStage.wait(500);
    }
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StagePlaySound();
  }
}
