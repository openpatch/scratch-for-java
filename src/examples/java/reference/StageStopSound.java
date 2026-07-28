package reference;
import org.openpatch.scratch.*;


public class StageStopSound {
  public StageStopSound() {
    Stage myStage = new Stage(600, 240);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
    myStage.wait(2000);
    myStage.stopSound("music");
  }

  public static void main(String[] args) {
    new StageStopSound();
  }
}
