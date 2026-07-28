package reference;
import org.openpatch.scratch.*;


public class StagePlaySound {
  public StagePlaySound() {
    Stage myStage = new Stage(600, 240);

    myStage.addSound("bump", "impactWood_heavy_001");
    while (true) {
      myStage.playSound("bump");
      myStage.wait(800);
    }
  }

  public static void main(String[] args) {
    new StagePlaySound();
  }
}
