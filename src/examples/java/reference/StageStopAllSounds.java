package reference;
import org.openpatch.scratch.*;


public class StageStopAllSounds {
  public StageStopAllSounds() {
    Stage myStage = new Stage(600, 240);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.addSound("bump", "impactWood_heavy_001");
    myStage.playSound("music");
    myStage.playSound("bump");
    myStage.wait(2000);
    myStage.stopAllSounds();
  }

  public static void main(String[] args) {
    new StageStopAllSounds();
  }
}
