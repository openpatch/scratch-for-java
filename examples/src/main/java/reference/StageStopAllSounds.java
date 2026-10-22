package reference;
import org.openpatch.scratch.*;


public class StageStopAllSounds {
  public StageStopAllSounds() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.addSound("bump", "impactWood_heavy_001");
    myStage.playSound("music");
    myStage.playSound("bump");
    myStage.waitSeconds(2);
    myStage.stopAllSounds();
  }

  public static void main(String[] args) {
    new StageStopAllSounds();
  }
}
