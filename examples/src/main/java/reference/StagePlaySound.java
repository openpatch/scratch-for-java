package reference;
import org.openpatch.scratch.*;


public class StagePlaySound {
  public StagePlaySound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addSound("bump", "impactWood_heavy_001");
    while (true) {
      myStage.playSound("bump");
      myStage.waitSeconds(0.8);
    }
  }

  public static void main(String[] args) {
    new StagePlaySound();
  }
}
